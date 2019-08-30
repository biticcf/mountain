/**
 *
 */
package com.beyonds.phoenix.mountain.core.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.alibaba.fastjson.JSON;

/**
 * @author  DanielCao
 * @date    2014年6月26日
 * @time    上午10:10:59
 * 类转化器
 * 借助FastJson从一个类转化为另一个类
 * 主要是用来转化同一个模型的Model和DO
 * 他们具有基本相同的属性名称
 */
public final class ClazzConverter {
	private ClazzConverter() {
		
	}
	/**
	 * 具有相同属性名称的对象转化
	 * @param <T1> 出参类型
	 * @param <T2> 入参类型
	 * @param srcClazz 待转化的对象
	 * @param dstClazz 结果对象类型
	 * @return 结果对象值
	 */
	public static <T1, T2> T1 converterClass(final T2 srcClazz, Class<T1> dstClazz) {
		String str = JSON.toJSONString(srcClazz);
		if (str == null || str.trim().equals("")) {
			return null;
		}
		
		return JSON.parseObject(str.trim(), dstClazz);
	}
	/**
	 * 集合转化
	 * @param <T1> 出参类型
	 * @param <T2> 入参类型
	 * @param srcList 待转化的对象集合
	 * @param dstClazz 结果对象类型
	 * @return 结果对象值
	 */
	public static <T1, T2> List<T1> converterClass(final List<T2> srcList, Class<T1> dstClazz) {
		List<T1> list = new ArrayList<>();
		
		if (srcList == null || srcList.isEmpty()) {
			return list;
		}
		
		String str = JSON.toJSONString(srcList);
		if (str == null || str.trim().equals("")) {
			return list;
		}
		
		list = JSON.parseArray(str.trim(), dstClazz);
		
		return list == null ? new ArrayList<T1>() : list;
	}
	
	/**
	 * 集合转化
	 * @param <T1> 出参类型
	 * @param <T2> 入参类型
	 * @param srcList 待转化的对象集合
	 * @param dstClazz 结果对象类型
	 * @return 结果对象值
	 */
	public static <T1, T2> Collection<T1> converterClass(final Collection<T2> srcList, Class<T1> dstClazz) {
		List<T1> list = new ArrayList<>();
		
		if (srcList == null || srcList.isEmpty()) {
			return list;
		}
		
		String str = JSON.toJSONString(srcList);
		if (str == null || str.trim().equals("")) {
			return list;
		}
		
		Collection<T1> c = JSON.parseArray(str.trim(), dstClazz);
		if(c == null) {
			return list;
		}
		
		return c;
	}
	
	/**
	 * 分页集合转化
	 * @param <T1> 出参类型
	 * @param <T2> 入参类型
	 * @param src 待转化的分页对象集合
	 * @param dstClazz 结果对象类型
	 * @return 结果对象值
	 */
	public static <T1, T2> PaginationSupport<T1> converterClass(final PaginationSupport<T2> src, Class<T1> dstClazz) {
		if (src == null) {
			return null;
		}
		
		List<T1> list = converterClass(src.getItems(), dstClazz);
		
		return new PaginationSupport<>(list, src.getTotalCount(), src.getPageSize(), src.getCurrentPage());
	}
}
