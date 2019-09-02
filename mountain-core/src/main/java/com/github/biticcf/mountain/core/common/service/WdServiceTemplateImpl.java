/**
 * 
 */
package com.github.biticcf.mountain.core.common.service;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.biticcf.mountain.core.common.lang.WdRuntimeException;
import com.github.biticcf.mountain.core.common.lang.WdServiceException;
import com.github.biticcf.mountain.core.common.result.WdCallbackResult;

/**
 * +事务相关模板实现
 * author: DanielCao
 * date:   2017年5月8日
 * time:   下午1:55:34
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
    
    private final TransactionTemplate transactionTemplate;
    
	public WdServiceTemplateImpl() {
		this.transactionTemplate = null;
	}
	
	public WdServiceTemplateImpl(TransactionTemplate transactionTemplate) {
		this.transactionTemplate = transactionTemplate;
	}
	
	@Override
	public <T> WdCallbackResult<T> execute(final WdServiceCallback<T> action, final Object domain) {
		writeDebugInfo(logger, "进入模板方法开始处理");
		
		WdCallbackResult<T> result = null;
		
		try {
			result = action.executeCheck();
			
			if (result.isSuccess()) {
				result = this.transactionTemplate.execute(
				new TransactionCallback<WdCallbackResult<T>>() {
					public WdCallbackResult<T> doInTransaction(TransactionStatus status) {
						// 回调业务逻辑
						WdCallbackResult<T> iNresult = action.executeAction();
						if (iNresult == null) {
							throw new WdServiceException(SERVICE_NO_RESULT);
						}
						
						if (iNresult.isFailure()) {
							status.setRollbackOnly();
							return iNresult;
						}
						return iNresult;
					}
				});
				
				if (result.isSuccess()) {
					action.executeAfterSuccess();
				} else {
					action.executeAfterFailure(null);
				}
			} else {
				action.executeAfterFailure(null);
			}
			
			writeDebugInfo(logger, "正常退出模板方法");
		} catch (WdServiceException e) {
			writeErrorInfo(logger, "异常退出模板方法A点", e);
			
			action.executeAfterFailure(e);
			
			result = WdCallbackResult.failure(e.getErrorCode(), e.getMessage(), e);
		} catch (WdRuntimeException e) {
			writeErrorInfo(logger, "异常退出模板方法B点", e);
			
			action.executeAfterFailure(e);
			
			result = WdCallbackResult.failure(e.getErrorCode(), e.getMessage(), e);
		} catch (Throwable e) {
			writeErrorInfo(logger, "异常退出模板方法C点", e);
			
			action.executeAfterFailure(e);
			
			if (e instanceof TransientDataAccessResourceException) {
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
		}
		
		writeDebugInfo(logger, "模板执行结束");
		
		return result;
	}
	
	@Override
	public <T> WdCallbackResult<T> executeWithoutTransaction(final WdServiceCallback<T> action, final Object domain) {
		writeDebugInfo(logger, "进入模板方法开始处理");
		
		WdCallbackResult<T> result = null;
		
		try {
			result = action.executeCheck();
			
			if (result.isSuccess()) {
				result = action.executeAction();
				
				if (result == null) {
					throw new WdServiceException(SERVICE_NO_RESULT);
				}
				
				if (result.isSuccess()) {
					action.executeAfterSuccess();
				} else {
					action.executeAfterFailure(null);
				}
			} else {
				action.executeAfterFailure(null);
			}
			
			writeDebugInfo(logger, "正常退出模板方法");
		} catch (WdServiceException e) {
			writeErrorInfo(logger, "异常退出模板方法D点", e);
			
			action.executeAfterFailure(e);
			
			result = WdCallbackResult.failure(e.getErrorCode(), e);
		} catch (WdRuntimeException e) {
			writeErrorInfo(logger, "异常退出模板方法E点", e);
			
			action.executeAfterFailure(e);
			
			result = WdCallbackResult.failure(e.getErrorCode(), e);
		} catch (Throwable e) {
			// 把系统异常转换为服务异常
			writeErrorInfo(logger, "异常退出模板方法F点", e);
			
			action.executeAfterFailure(e);
			
			if (e instanceof TransientDataAccessResourceException) {
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
		}
		
		writeDebugInfo(logger, "模板执行结束");

		return result;
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
