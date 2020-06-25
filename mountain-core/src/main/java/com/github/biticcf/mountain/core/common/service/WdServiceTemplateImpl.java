/**
 * 
 */
package com.github.biticcf.mountain.core.common.service;

import java.sql.SQLException;

import org.springframework.dao.TransientDataAccessResourceException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

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
    private final TransactionOperations transactionTemplate;
    
	public WdServiceTemplateImpl() {
		this.transactionTemplate = null;
	}
	
	public WdServiceTemplateImpl(TransactionOperations transactionTemplate) {
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
}
