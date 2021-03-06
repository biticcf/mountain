/**
 * 
 */
package com.github.biticcf.mountain.core.common.result.reactor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.slf4j.MDC;

import com.github.biticcf.mountain.core.common.lang.Logable;
import com.github.biticcf.mountain.core.common.model.LogLevelEnum;
import com.github.biticcf.mountain.core.common.result.CallResult;
import com.github.biticcf.mountain.core.common.result.CastExecutor;
import com.github.biticcf.mountain.core.common.result.ReturnResult;
import com.github.biticcf.mountain.core.common.trace.TraceContext;
import com.github.biticcf.mountain.core.common.util.LogModel;

import reactor.core.publisher.Mono;

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
     * 结果回调
     * @return 结果集
     */
	CallResult<List<T2>> execute();
	
	/**
	 * List类型的结果类型转换
	 * @param name 接口名称
	 * @param method 接口方法
	 * @param paramValueMap 接口参数
	 * @param clazz 结果对象
	 * @return 组装后的结果对象
	 */
	default Mono<ReturnResult<List<T1>>> processResult(String name, String method, 
			Map<String, Object> paramValueMap, Class<T1> clazz) {
		return processResult(name, method, paramValueMap, LogLevelEnum.ALL, clazz);
	}
	
	/**
	 * List类型的结果类型转换
	 * @param name 接口名称
	 * @param method 接口方法
	 * @param paramValueMap 接口参数
	 * @param logLevel 日志级别
	 * @param clazz 结果对象
	 * @return 组装后的结果对象
	 */
	default Mono<ReturnResult<List<T1>>> processResult(String name, String method, 
			Map<String, Object> paramValueMap, LogLevelEnum logLevel, Class<T1> clazz) {
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
        
        final LogLevelEnum logLevelFinal = logLevel;
		final LogModel lmFinal = lm;
        return Mono.fromFuture(CompletableFuture.completedFuture(execute())).map(callResult -> {
        	if (callResult == null) {
        		if (!LogLevelEnum.NEVER.equals(logLevelFinal) && !LogLevelEnum.INPUT.equals(logLevelFinal)) {
        			lmFinal.addMetaData("callResult", callResult);
        			writeErrorLog(LOGGER, lmFinal.toJson());
        			
        			MDC.remove(TRACE_ID);
        		}
        		
        		TraceContext.deleteTrace();
    			
    			return new ReturnResult<List<T1>>(-1, "UNKNOWN ERROR");
            } else if (!callResult.isSuccess()) {
    			Throwable throwable = callResult.getThrowable();
    			if (!LogLevelEnum.NEVER.equals(logLevelFinal)) {
    				lmFinal.addMetaData("callResult", callResult);
    				if (throwable == null) {
    					writeInfoLog(LOGGER, lmFinal.toJson());
    				} else {
    					writeErrorLog(LOGGER, lmFinal.toJson(), throwable);
    				}
    				
    				MDC.remove(TRACE_ID);
    			}
    			
    			TraceContext.deleteTrace();
    			
    			return new ReturnResult<List<T1>>(callResult.getResultCode(), callResult.getResultMessage());
            } else {
            	List<T2> resultModel = callResult.getBusinessResult();
        		List<T1> resultT1 =  resultListCast(resultModel, clazz);
        		ReturnResult<List<T1>> returnResult = new ReturnResult<List<T1>>(
        				callResult.getResultCode(), callResult.getResultMessage(), resultT1);
        		
        		if (!LogLevelEnum.NEVER.equals(logLevelFinal)) {
        			lmFinal.addMetaData("returnResult", returnResult);
        			writeInfoLog(LOGGER, lmFinal.toJson());
        			
        			MDC.remove(TRACE_ID);
        		}
        		
        		TraceContext.deleteTrace();
                
                return returnResult;
            }
        });
    }
	
	/**
	 * 类型转换
	 * @param sre 原始类型
	 * @param clazz 结果对象
	 * @return 结果集
	 */
	default List<T1> resultListCast(List<T2> sre, Class<T1> clazz) {
		List<T1> listT1 = new ArrayList<>();
		
		if (sre != null) {
			for (int i = 0; i < sre.size(); i++) {
				T1 _vT1 = resultCast(sre.get(i), clazz);
				if (_vT1 != null) {
					listT1.add(_vT1);
				}
			}
		}
		
		return listT1;
	}
}
