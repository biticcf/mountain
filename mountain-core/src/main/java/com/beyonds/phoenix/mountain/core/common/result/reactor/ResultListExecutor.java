/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.result.reactor;

import java.util.ArrayList;
import java.util.List;
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
 * author: DanielCao
 * date:   2018年7月24日
 * time:   下午7:25:16
 *
 * @param <T1> 结果数据类型
 * @param <T2> 原数据类型
 */
public interface ResultListExecutor<T1, T2> extends CastExecutor<T1, T2>, Logable {
	Logger LOGGER = LoggerFactory.getLogger("WEB.LOG");
	
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
    			
    			return new ReturnResult<List<T1>>(-1, "UNKNOWN ERROR");
            } else if (!callResult.isSuccess()) {
    			Throwable throwable = callResult.getThrowable();
    			if (throwable == null) {
    				writeInfoLog(LOGGER, lm.toJson());
    			} else {
    				writeErrorLog(LOGGER, lm.toJson(), throwable);
    			}
    			
    			MDC.remove(TRACE_ID);
    			
    			return new ReturnResult<List<T1>>(callResult.getResultCode(), callResult.getResultMessage());
            } else {
            	List<T2> resultModel = callResult.getBusinessResult();
        		List<T1> resultT1 =  resultListCast(resultModel, clazz);
        		ReturnResult<List<T1>> returnResult = new ReturnResult<List<T1>>(
        				callResult.getResultCode(), callResult.getResultMessage(), resultT1);
        		
        		lm.addMetaData("returnResult", returnResult);
        		writeInfoLog(LOGGER, lm.toJson());
        		
        		MDC.remove(TRACE_ID);
                
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