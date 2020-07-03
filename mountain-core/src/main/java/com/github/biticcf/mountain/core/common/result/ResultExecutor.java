/**
 * 
 */
package com.github.biticcf.mountain.core.common.result;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;

import com.github.biticcf.mountain.core.common.lang.Logable;
import com.github.biticcf.mountain.core.common.model.LogLevelEnum;
import com.github.biticcf.mountain.core.common.trace.TraceContext;
import com.github.biticcf.mountain.core.common.util.LogModel;

/**
 * 
 * author: Daniel.Cao
 * date:   2020年07月03日
 * time:   上午09:16:04
 *
 * @param <T1> 结果对象类型
 * @param <T2> 原对象类型
 */
public interface ResultExecutor<T1, T2> extends CastExecutor<T1, T2>, Logable {
	Log LOGGER = LogFactory.getLog("WEB.LOG");
	/**
     * +结果回调
     * @return 结果对象
     */
	CallResult<T2> execute();
	
	/**
	 * +结果类型转化,默认输出所有参数
	 * @param name 调用者名称
	 * @param method 调用接口的method方法
	 * @param paramValueMap 参数列表
	 * @param clazz 结果对象
	 * @return 转换后的结果集
	 */
	default ReturnResult<T1> processResult(String name, String method, Map<String, Object> paramValueMap, Class<T1> clazz) {
		return processResult(name, method, paramValueMap, LogLevelEnum.ALL, clazz);
	}
	
	/**
	 * +结果类型转化
	 * @param name 调用者名称
	 * @param method 调用接口的method方法
	 * @param paramValueMap 参数列表
	 * @param logLevel 输出日志级别，详见 #LogLevelEnu
	 * @param clazz 结果对象
	 * @return 转换后的结果集
	 */
	default ReturnResult<T1> processResult(String name, String method, Map<String, Object> paramValueMap, LogLevelEnum logLevel, Class<T1> clazz) {
		// 默认是ALL
		if (logLevel == null) {
        	logLevel = LogLevelEnum.ALL;
        }
		
		LogModel lm = null;
        if (!LogLevelEnum.NEVER.equals(logLevel)) {
        	lm = LogModel.newLogModel(name);
        	
        	lm.addMetaData("method", method);
        	if (paramValueMap != null && !paramValueMap.isEmpty()) {
        		for (String _key : paramValueMap.keySet()) {
        			lm.addMetaData(_key, paramValueMap.get(_key));
        		}
        	}
        	
        	String traceId = TraceContext.getTrace();
            MDC.put(TRACE_ID, traceId);
            
            lm.addMetaData(TRACE_ID, traceId);
            
        	writeInfoLog(LOGGER, lm.toJson(false));
        }
        
        CallResult<T2> callResult = execute();
        
        if (callResult == null) {
        	if (!LogLevelEnum.NEVER.equals(logLevel) && !LogLevelEnum.INPUT.equals(logLevel)) {
        		lm.addMetaData("callResult", callResult);
        		writeErrorLog(LOGGER, lm.toJson());
        		
        		MDC.remove(TRACE_ID);
        	}
			
        	TraceContext.deleteTrace();
        	
			return new ReturnResult<T1>(-1, "UNKNOWN ERROR");
        }
		
		if (!callResult.isSuccess()) {
			Throwable throwable = callResult.getThrowable();
			if (!LogLevelEnum.NEVER.equals(logLevel)) {
				lm.addMetaData("callResult", callResult);
				if (throwable == null) {
					writeInfoLog(LOGGER, lm.toJson());
				} else {
					writeErrorLog(LOGGER, lm.toJson(), throwable);
				}
				
				MDC.remove(TRACE_ID);
			}
			
			TraceContext.deleteTrace();
			
			return new ReturnResult<T1>(callResult.getResultCode(), callResult.getResultMessage());
		}
		
		T2 resultModel = callResult.getBusinessResult();
		T1 resultT1 = resultCast(resultModel, clazz);
		ReturnResult<T1> returnResult = new ReturnResult<T1>(
				callResult.getResultCode(), callResult.getResultMessage(), resultT1);
		
		if (!LogLevelEnum.NEVER.equals(logLevel)) {
			if (LogLevelEnum.ALL.equals(logLevel)) {
				lm.addMetaData("returnResult", returnResult);
			}
			writeInfoLog(LOGGER, lm.toJson());
			
			MDC.remove(TRACE_ID);
		}
		
		TraceContext.deleteTrace();
		
        return returnResult;
    }
}
