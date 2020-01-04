/**
 * 
 */
package com.github.biticcf.mountain.core.common.result.reactor;

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
import com.github.biticcf.mountain.core.common.util.CodeGenerator;
import com.github.biticcf.mountain.core.common.util.LogModel;

import reactor.core.publisher.Mono;

/**
 * 
 * author: Daniel.Cao
 * date:   2018年11月20日
 * time:   下午3:45:04
 *
 * @param <T1> 结果对象类型
 * @param <T2> 原对象类型
 */
public interface ResultExecutor<T1, T2> extends CastExecutor<T1, T2>, Logable {
	Log LOGGER = LogFactory.getLog("WEB.LOG");
	/**
     * 结果回调
     * @return 结果对象
     */
	CallResult<T2> execute();
	
	/**
	 * 对象类型转换
	 * @param name 接口名称
	 * @param method 接口方法
	 * @param paramValueMap 接口参数
	 * @param clazz 接口对象
	 * @return 结果对象
	 */
	default Mono<ReturnResult<T1>> processResult(String name, String method, Map<String, Object> paramValueMap, Class<T1> clazz) {
		return processResult(name, method, paramValueMap, LogLevelEnum.ALL, clazz);
	}
	
	/**
	 * 对象类型转换
	 * @param name 接口名称
	 * @param method 接口方法
	 * @param paramValueMap 接口参数
	 * @param logLevel 日志级别
	 * @param clazz 接口对象
	 * @return 结果对象
	 */
	default Mono<ReturnResult<T1>> processResult(String name, String method, Map<String, Object> paramValueMap, LogLevelEnum logLevel, Class<T1> clazz) {
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
			String traceId = CodeGenerator.generateCode(CodeGenerator.CODE_PREFIX_TRACE_ID);
			MDC.put(TRACE_ID, traceId);
			
			writeInfoLog(LOGGER, lm.toJson(false));
		}
        
		final LogLevelEnum logLevelFinal = logLevel;
		final LogModel lmFinal = lm;
        return Mono.fromFuture(CompletableFuture.completedFuture(execute())).map(callResult -> {
        	if (LogLevelEnum.ALL.equals(logLevelFinal)) {
        		lmFinal.addMetaData("callResult", callResult);
        	}
        	
        	if (callResult == null) {
        		if (!LogLevelEnum.NEVER.equals(logLevelFinal) && !LogLevelEnum.INPUT.equals(logLevelFinal)) {
        			writeErrorLog(LOGGER, lmFinal.toJson());
        			
        			MDC.remove(TRACE_ID);
        		}
    			
    			return new ReturnResult<T1>(-1, "UNKNOWN ERROR");
            } else if (!callResult.isSuccess()) {
    			Throwable throwable = callResult.getThrowable();
    			if (!LogLevelEnum.NEVER.equals(logLevelFinal)) {
    				if (throwable == null) {
    					writeInfoLog(LOGGER, lmFinal.toJson());
    				} else {
    					writeErrorLog(LOGGER, lmFinal.toJson(), throwable);
    				}
    				
    				MDC.remove(TRACE_ID);
    			}
    			
    			return new ReturnResult<T1>(callResult.getResultCode(), callResult.getResultMessage());
            } else {
            	T2 resultModel = callResult.getBusinessResult();
        		T1 resultT1 =  resultCast(resultModel, clazz);
        		ReturnResult<T1> returnResult = new ReturnResult<T1>(
        				callResult.getResultCode(), callResult.getResultMessage(), resultT1);
        		
        		if (!LogLevelEnum.NEVER.equals(logLevelFinal)) {
        			lmFinal.addMetaData("returnResult", returnResult);
        			writeInfoLog(LOGGER, lmFinal.toJson());
        			
        			MDC.remove(TRACE_ID);
        		}
        		
        		return returnResult;
            }
        });
    }
}
