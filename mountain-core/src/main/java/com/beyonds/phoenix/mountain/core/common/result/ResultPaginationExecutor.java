/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.result;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.beyonds.phoenix.mountain.core.common.lang.Logable;
import com.beyonds.phoenix.mountain.core.common.util.CodeGenerator;
import com.beyonds.phoenix.mountain.core.common.util.LogModel;
import com.beyonds.phoenix.mountain.core.common.util.PaginationSupport;

/**
 * 
 * @Author: DanielCao
 * @Date:   2018年7月18日
 * @Time:   下午7:49:20
 *
 * @param <T1> 结果类型
 * @param <T2> 原始类型
 */
public interface ResultPaginationExecutor<T1, T2> extends CastExecutor<T1, T2>, Logable {
	Logger LOGGER = LoggerFactory.getLogger("WEB.LOG");
	/**
     * +结果回调
     * @return 回调结果
     */
	CallResult<PaginationSupport<T2>> execute();
	
	/**
	 * +结果集型转化,分页
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
        
        CallResult<PaginationSupport<T2>> callResult = execute();
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
		
		PaginationSupport<T2> resultModel = callResult.getBusinessResult();
		PaginationSupport<T1> resultT1 =  resultPaginationCast(resultModel, clazz);
		
		List<T1> tmpList = resultT1.getItems();
		PaginationMeta meta = new PaginationMeta(resultT1.getTotalCount(), resultT1.getPageSize(), resultT1.getCurrentPage());
		
		ReturnResult<List<T1>> returnResult = new ReturnResult<List<T1>>(callResult.getResultCode(), callResult.getResultMessage(), tmpList, meta);
		
		lm.addMetaData("returnResult", returnResult);
		writeInfoLog(LOGGER, lm.toJson());
		
		MDC.remove(TRACE_ID);
        
        return returnResult;
    }
	
	/**
	 * +可扩展功能,对象转换
	 * @param sre 原对象
	 * @param clazz 结果对象
	 * @return 转换后的结果
	 */
	default PaginationSupport<T1> resultPaginationCast(PaginationSupport<T2> sre, Class<T1> clazz) {
		List<T1> listT1 = new ArrayList<>();
		int totalCount = 0;
		int pageSize = PaginationSupport.DEFAULT_PAGESIZE;
		int currentPage = 1;
		
		if (sre != null) {
			listT1 = resultListCast(sre.getItems(), clazz);
			totalCount = sre.getTotalCount();
			pageSize = sre.getPageSize();
			currentPage = sre.getCurrentPage();
		}
		
		return new PaginationSupport<T1>(listT1, totalCount, pageSize, currentPage);
	}
}
