/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.result.reactor;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.beyonds.phoenix.mountain.core.common.lang.Logable;
import com.beyonds.phoenix.mountain.core.common.result.CallResult;
import com.beyonds.phoenix.mountain.core.common.result.CastExecutor;
import com.beyonds.phoenix.mountain.core.common.result.ReturnResult;
import com.beyonds.phoenix.mountain.core.common.util.CodeGenerator;
import com.beyonds.phoenix.mountain.core.common.util.LogModel;

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
	Logger LOGGER = LoggerFactory.getLogger("WEB.LOG");
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
        final LogModel lm = LogModel.newLogModel(name);
        lm.addMetaData("method", method);
        if (paramValueMap != null && !paramValueMap.isEmpty()) {
        	for (String _key : paramValueMap.keySet()) {
        		lm.addMetaData(_key, paramValueMap.get(_key));
        	}
        }
        String traceId = CodeGenerator.generateCode(CodeGenerator.CODE_PREFIX_TRACE_ID);
        MDC.put(TRACE_ID, traceId);
        
        writeInfoLog(LOGGER, lm.toJson(false));
        
        return Mono.fromFuture(CompletableFuture.completedFuture(execute())).map(callResult -> {
        	lm.addMetaData("callResult", callResult);
        	
        	if (callResult == null) {
    			writeErrorLog(LOGGER, lm.toJson());
    			
    			MDC.remove(TRACE_ID);
    			
    			return new ReturnResult<T1>(-1, "UNKNOWN ERROR");
            } else if (!callResult.isSuccess()) {
    			Throwable throwable = callResult.getThrowable();
    			if (throwable == null) {
    				writeInfoLog(LOGGER, lm.toJson());
    			} else {
    				writeErrorLog(LOGGER, lm.toJson(), throwable);
    			}
    			
    			MDC.remove(TRACE_ID);
    			
    			return new ReturnResult<T1>(callResult.getResultCode(), callResult.getResultMessage());
            } else {
            	T2 resultModel = callResult.getBusinessResult();
        		T1 resultT1 =  resultCast(resultModel, clazz);
        		ReturnResult<T1> returnResult = new ReturnResult<T1>(
        				callResult.getResultCode(), callResult.getResultMessage(), resultT1);
        		
        		lm.addMetaData("returnResult", returnResult);
        		writeInfoLog(LOGGER, lm.toJson());
        		
        		MDC.remove(TRACE_ID);
        		
        		return returnResult;
            }
        });
    }
}
