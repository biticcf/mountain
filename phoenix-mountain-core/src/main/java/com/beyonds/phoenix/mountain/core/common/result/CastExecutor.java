/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.result;

import java.util.ArrayList;
import java.util.List;

import com.beyonds.phoenix.mountain.core.common.util.ClazzConverter;

/**
 * 
 * @Author: Daniel.Cao
 * @Date:   2018年11月20日
 * @Time:   下午3:49:42
 *
 * @param <T1> 结果对象类型
 * @param <T2> 原对象类型
 */
public interface CastExecutor<T1, T2> {
	String TRACE_ID = "traceId";
	/**
	 * 可扩展功能,返回结果定制
	 * @param sre 原对象
	 * @param clazz 结果对象
	 * @return 结果对象
	 */
    @SuppressWarnings("unchecked")
	default T1 resultCast(T2 sre, Class<T1> clazz) {
    	if (sre == null) {
    		return null;
    	}
		if (sre.getClass().isAssignableFrom(clazz)) {
			return (T1) sre;
		}
		
		return ClazzConverter.converterClass(sre, clazz);
	}
    
    /**
	 * 可扩展功能,类型转换
	 * @param src 原始类型
	 * @param clazz 结果对象
	 * @return 结果集
	 */
	default List<T1> resultListCast(List<T2> src, Class<T1> clazz) {
		List<T1> listT1 = new ArrayList<>();
		
		List<T1> tmpList = ClazzConverter.converterClass(src, clazz);
		if (tmpList != null && !tmpList.isEmpty()) {
			listT1.addAll(tmpList);
		}
		
		return listT1;
	}
}
