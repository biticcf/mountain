/**
 * 
 */
package com.github.biticcf.mountain.core.common.result;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.github.biticcf.mountain.core.common.lang.Logable;
import com.github.biticcf.mountain.core.common.util.CodeGenerator;
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
	Logger LOGGER = LoggerFactory.getLogger("WEB.LOG");
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
        
        CallResult<List<T2>> callResult = execute();
        lm.addMetaData("callResult", callResult);
        
        if (callResult == null) {
			writeErrorLog(LOGGER, lm.toJson());
			
			MDC.remove(TRACE_ID);
			
			return new ReturnResult<List<T1>>(-1, "UNKNOWN ERROR");
        }
		
		if (!callResult.isSuccess()) {
			Throwable throwable = callResult.getThrowable();
			if (throwable == null) {
				writeInfoLog(LOGGER, lm.toJson());
			} else {
				writeErrorLog(LOGGER, lm.toJson(), throwable);
			}
			
			MDC.remove(TRACE_ID);
			
			return new ReturnResult<List<T1>>(callResult.getResultCode(), callResult.getResultMessage());
		}
		
		List<T2> resultModel = callResult.getBusinessResult();
		List<T1> resultT1 =  resultListCast(resultModel, clazz);
		ReturnResult<List<T1>> returnResult = new ReturnResult<List<T1>>(
				callResult.getResultCode(), callResult.getResultMessage(), resultT1);
		
		lm.addMetaData("returnResult", returnResult);
		writeInfoLog(LOGGER, lm.toJson());
		
		MDC.remove(TRACE_ID);
        
        return returnResult;
    }
}