/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.service;

import com.beyonds.phoenix.mountain.core.common.result.WdCallbackResult;

/**
 * 回调方法定义
 * @Author: DanielCao
 * @Date:   2017年5月8日
 * @Time:   下午1:52:26
 *
 * @param <T> 结果对象类型
 */
public interface WdServiceCallback<T> {
	/**
	 * 该方法主要是在执行事务之前做参数和业务逻辑检查
	 * @return 结果集
	 */
	WdCallbackResult<T> executeCheck();
	/**
	 * 该方法主要是在事务中执行
	 * @return 结果集
	 */
    WdCallbackResult<T> executeAction();
    /**
     * 该方法主要是在事务成功提交之后执行的事务无关操作
     */
    void executeAfter();
}
