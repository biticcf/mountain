/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.service;

import com.beyonds.phoenix.mountain.core.common.result.WdCallbackResult;

/**
 * +服务模板定义
 * author: DanielCao
 * date:   2017年5月8日
 * time:   下午1:53:45
 *
 */
public interface WdServiceTemplate {
	/**
	 * 事务模板
	 * @param <T> 结果对象类型
	 * @param action 业务执行方案
	 * @param domain 业务归属
	 * @return 封装的结果对象
	 */
	<T> WdCallbackResult<T> execute(WdServiceCallback<T> action, Object domain);
	
	/**
	 * 非事务模板
	 * @param <T> 结果对象类型
	 * @param action 业务执行方案
	 * @param domain 业务归属
	 * @return 封装的结果对象
	 */
	<T> WdCallbackResult<T> executeWithoutTransaction(WdServiceCallback<T> action, Object domain);
}
