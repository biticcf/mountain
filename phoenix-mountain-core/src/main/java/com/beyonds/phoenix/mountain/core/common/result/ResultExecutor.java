/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.result;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.beyonds.phoenix.mountain.core.common.lang.Logable;
import com.beyonds.phoenix.mountain.core.common.util.CodeGenerator;
import com.beyonds.phoenix.mountain.core.common.util.LogModel;

import reactor.core.publisher.Mono;

/**
 * 
 * @Author: Daniel.Cao
 * @Date:   2018年11月20日
 * @Time:   下午3:45:04
 *
 * @param <T1> 结果对象类型
 * @param <T2> 原对象类型
 */
public interface ResultExecutor<T1, T2> extends ResultCastExecutor<T1, T1, T2>, Logable {
	Logger LOGGER = LoggerFactory.getLogger("WEB.LOG");
	/**
     * 结果回调
     * @return 结果对象
     */
	CallResult<T2> execute();
	
	@Override
	default ReturnResult<T1> processResult(String name, String method, Map<String, Object> paramValueMap, Class<T1> clazz) {
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
        
        CallResult<T2> callResult = execute();
        lm.addMetaData("callResult", callResult);
        
        if (callResult == null) {
			writeErrorLog(LOGGER, lm.toJson());
			
			MDC.remove(TRACE_ID);
			
			return new ReturnResult<T1>(-1, "UNKNOWN ERROR");
        }
		
		if (!callResult.isSuccess()) {
			Throwable throwable = callResult.getThrowable();
			if (throwable == null) {
				writeInfoLog(LOGGER, lm.toJson());
			} else {
				writeErrorLog(LOGGER, lm.toJson(), throwable);
			}
			
			MDC.remove(TRACE_ID);
			
			return new ReturnResult<T1>(callResult.getResultCode(), callResult.getResultMessage());
		}
		
		T2 resultModel = callResult.getBusinessResult();
		T1 resultT1 = resultCast(resultModel, clazz);
		ReturnResult<T1> returnResult = new ReturnResult<T1>(
				callResult.getResultCode(), callResult.getResultMessage(), resultT1);
		
		lm.addMetaData("returnResult", returnResult);
		writeInfoLog(LOGGER, lm.toJson());
		
		MDC.remove(TRACE_ID);
        
        return returnResult;
    }
	
	@Override
	default Mono<ReturnResult<T1>> processReactorResult(String name, String method, 
			Map<String, Object> paramValueMap, Class<T1> clazz) {
		return Mono.just(processResult(name, method, paramValueMap, clazz));
    }
}
