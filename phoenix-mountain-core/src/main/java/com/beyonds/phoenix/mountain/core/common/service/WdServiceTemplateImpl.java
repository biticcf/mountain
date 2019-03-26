/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.service;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.beyonds.phoenix.mountain.core.common.lang.WdRuntimeException;
import com.beyonds.phoenix.mountain.core.common.lang.WdServiceException;
import com.beyonds.phoenix.mountain.core.common.result.WdCallbackResult;

/**
 * 事务相关模板实现
 * @Author: DanielCao
 * @Date:   2017年5月8日
 * @Time:   下午1:55:34
 *
 */
public class WdServiceTemplateImpl implements WdServiceTemplate {
	private static Logger logger = LoggerFactory.getLogger(WdServiceTemplateImpl.class);
	
	public static final int    SERVICE_NO_RESULT                = 0xFF0001;
    public static final int    SERVICE_SYSTEM_FALIURE           = 0xFF0002;
    public static final int    NO_ALIVE_DATASOURCE              = 0xFF0004;
    public static final int    SERVICE_METHOD_ARGS_LESS_THAN_1  = 0xFF0003;
    public static final int    EXECUTE_SQL_TRANS_FALIURE        = 0xFF0005;
    public static final int    EXECUTE_SQL_FALIURE              = 0xFF0006;
    
    public static final String CONTEXT_INVOKE_METHOD           = "invokeMethod";
    public static final String CONTEXT_INVOCATION              = "methodInvocation";
    
    private TransactionTemplate transactionTemplate;
	
	public WdServiceTemplateImpl() {
		
	}
	
	public WdServiceTemplateImpl(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	@Override
	public <T> WdCallbackResult<T> execute(final WdServiceCallback<T> action, final Object domain) {
		return execute(action, domain, true);
	}
	
	@Override
	public <T> WdCallbackResult<T> executeWithoutTransaction(final WdServiceCallback<T> action, final Object domain) {
		return execute(action, domain, false);
	}
	
	/**
	 * +事务管理核心流程
	 * @param <T> 返回的业务对象
	 * @param action 业务处理流程
	 * @param domain 保留字段
	 * @param withTrans 是否需要事务,true-需要事务;false-不需要事务
	 * @return 处理有得到的业务对象结果集
	 */
	public <T> WdCallbackResult<T> execute(final WdServiceCallback<T> action, final Object domain, 
			final boolean withTrans) {
		writeDebugInfo(logger, "进入模板方法开始处理");
		
		WdCallbackResult<T> result = null;

		try {
			// 设置标志,保证executeCheck()内不执行事务
			TransStatusHolder.disableTransaction();
			result = action.executeCheck();
			
			if (result.isSuccess()) {
				if(withTrans) {
					// 设置标志,保证executeAction()内执行事务
					TransStatusHolder.enableTransaction();
				} else {
					// 设置标志,保证executeAction()内不执行事务
					TransStatusHolder.disableTransaction();
				}
				
				result = this.transactionTemplate.execute(
				new TransactionCallback<WdCallbackResult<T>>() {
					public WdCallbackResult<T> doInTransaction(TransactionStatus status) {
						// 回调业务逻辑
						WdCallbackResult<T> iNresult = action.executeAction();
						if (iNresult == null) {
							throw new WdServiceException(SERVICE_NO_RESULT);
						}
						// 扩展点
						templateExtensionInTransaction(iNresult);
						if (iNresult.isFailure()) {
							status.setRollbackOnly();
							return iNresult;
						}
						return iNresult;
					}
				});
				
				if (result.isSuccess()) {
					// 设置标志,保证executeAfter()内不执行事务
					TransStatusHolder.disableTransaction();
					action.executeAfter();
					
					templateExtensionAfterTransaction(result);
				}
			}
			
			writeDebugInfo(logger, "正常退出模板方法");
		} catch (WdServiceException e) {
			writeErrorInfo(logger, "异常退出模板方法A点", e);
			
			result = WdCallbackResult.failure(e.getErrorCode(), e.getMessage(), e);
		} catch (WdRuntimeException e) {
			writeErrorInfo(logger, "异常退出模板方法B点", e);
			
			result = WdCallbackResult.failure(e.getErrorCode(), e.getMessage(), e);
		} catch (Throwable e) {
			writeErrorInfo(logger, "异常退出模板方法C点", e);
			if (e instanceof TransientDataAccessResourceException) { //SQLException
				TransientDataAccessResourceException tdarException = (TransientDataAccessResourceException) e;
				Throwable e1 = tdarException.getCause();
				String msg = tdarException.getMessage();
				
				if (e1 != null && e1 instanceof SQLException && msg != null && msg.indexOf("Connection is read-only") >= 0) {
					writeErrorInfo(logger, "在非事务生命周期中执行了事务操作", e);
					result = WdCallbackResult.failure(EXECUTE_SQL_TRANS_FALIURE, e);
				} else {
					writeErrorInfo(logger, "执行数据库操作失败", e);
					result = WdCallbackResult.failure(EXECUTE_SQL_FALIURE, e);
				}
			} else {
				result = WdCallbackResult.failure(SERVICE_SYSTEM_FALIURE, e);
			}
		} finally {
			// 确保清除标志
			TransStatusHolder.removeTransStatus();
		}
		
		writeDebugInfo(logger, "模板执行结束");
		
		return result;
	}
	
	/**
	 * 扩展点：模板提供的允许不同类型业务在<b>事务内</b>进行扩展的一个点
	 * 
	 * @param <T> 实际结果类型
	 * @param result 前一步处理结果
	 * 
	 */
	protected <T> void templateExtensionInTransaction(WdCallbackResult<T> result) {
		// DUMY
	}

	/**
	 * @param <T> 实际结果类型
	 * @param result 前一步处理结果
	 */
	protected <T> void templateExtensionAfterTransaction(WdCallbackResult<T> result) {
		// DUMY
	}

	/**
	 * @param <T> 实际结果类型
	 * @param result 前一步处理结果
	 */
	protected <T> void templateExtensionAfterExecute(WdCallbackResult<T> result) {
		// DUMY
	}
	
	/**
	 * debug级别的日志
	 * @param tmpLogger logger
	 * @param message 消息
	 */
	private void writeDebugInfo(final Logger tmpLogger, final String message) {
		if (tmpLogger.isDebugEnabled()) {
			tmpLogger.debug(message);
		}
	}
	
	/**
	 * error级别的日志
	 * @param tmpLogger logger
	 * @param message 消息
	 * @param e 异常
	 */
	private void writeErrorInfo(final Logger tmpLogger, final String message, final Throwable e) {
		tmpLogger.error(message, e);
	}
}
