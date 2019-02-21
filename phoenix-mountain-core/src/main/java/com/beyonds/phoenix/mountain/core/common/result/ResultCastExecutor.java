/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.result;

import java.util.Map;

import reactor.core.publisher.Mono;

/**
 * 
 * @Author: Daniel.Cao
 * @Date:   2018年11月20日
 * @Time:   下午3:49:42
 *
 * @param <T> 最终结果对象类型
 * @param <T1> 结果对象类型
 * @param <T2> 原对象类型
 */
public interface ResultCastExecutor<T, T1, T2> extends CastExecutor<T1, T2> {
	
	/**
	 * 分页查询结果集类型转化
	 * @param name 调用者名称
	 * @param method 调用接口的method方法
	 * @param paramValueMap 参数列表
	 * @param clazz 结果对象
	 * @return 转换后的结果集
	 */
	ReturnResult<T> processResult(String name, String method, Map<String, Object> paramValueMap, Class<T1> clazz);
	
	/**
	 * 响应式编程结果
	 * 分页查询结果集类型转化
	 * @param name 调用者名称
	 * @param method 调用接口的method方法
	 * @param paramValueMap 参数列表
	 * @param clazz 结果对象
	 * @return 转换后的结果集
	 */
	Mono<ReturnResult<T>> processReactorResult(String name, String method, Map<String, Object> paramValueMap, Class<T1> clazz);
}
