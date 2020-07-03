/**
 * 
 */
package com.github.biticcf.mountain.core.common.result;

import java.util.List;
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
 * author: DanielCao
 * date:   2018年7月24日
 * time:   下午7:25:16
 *
 * @param <T1> 结果数据类型
 * @param <T2> 原数据类型
 */
public interface ResultListExecutor<T1, T2> extends CastExecutor<T1, T2>, Logable {
	Log LOGGER = LogFactory.getLog("WEB.LOG");
	/**
     * +结果回调
     * @return 结果集
     */
	CallResult<List<T2>> execute();
	
	/**
	 * +结果集型转化,不分页
	 * @param name 调用者名称
	 * @param method 调用接口的method方法
	 * @param paramValueMap 参数列表
	 * @param clazz 结果对象
	 * @return 转换后的结果集
	 */
	default ReturnResult<List<T1>> processResult(
			String name, String method, Map<String, Object> paramValueMap, Class<T1> clazz) {
		return processResult(name, method, paramValueMap, LogLevelEnum.ALL, clazz);
	}
	
	/**
	 * +结果集型转化,不分页
	 * @param name 调用者名称
	 * @param method 调用接口的method方法
	 * @param paramValueMap 参数列表
	 * @param logLevel 日志级别
	 * @param clazz 结果对象
	 * @return 转换后的结果集
	 */
	default ReturnResult<List<T1>> processResult(
			String name, String method, Map<String, Object> paramValueMap, LogLevelEnum logLevel, Class<T1> clazz) {
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
        
        CallResult<List<T2>> callResult = execute();
        
        if (callResult == null) {
        	if (!LogLevelEnum.NEVER.equals(logLevel) && !LogLevelEnum.INPUT.equals(logLevel)) {
        		lm.addMetaData("callResult", callResult);
        		writeErrorLog(LOGGER, lm.toJson());
        		
        		MDC.remove(TRACE_ID);
        	}
        	
        	TraceContext.deleteTrace();
        	
			return new ReturnResult<List<T1>>(-1, "UNKNOWN ERROR");
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
			
			return new ReturnResult<List<T1>>(callResult.getResultCode(), callResult.getResultMessage());
		}
		
		List<T2> resultModel = callResult.getBusinessResult();
		List<T1> resultT1 =  resultListCast(resultModel, clazz);
		ReturnResult<List<T1>> returnResult = new ReturnResult<List<T1>>(
				callResult.getResultCode(), callResult.getResultMessage(), resultT1);
		
		if (!LogLevelEnum.NEVER.equals(logLevel)) {
			lm.addMetaData("returnResult", returnResult);
			writeInfoLog(LOGGER, lm.toJson());
			
			MDC.remove(TRACE_ID);
		}
        
		TraceContext.deleteTrace();
		
        return returnResult;
    }
}
