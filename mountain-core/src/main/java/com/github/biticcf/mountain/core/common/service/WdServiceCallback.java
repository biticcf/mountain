/**
 * 
 */
package com.github.biticcf.mountain.core.common.service;

import com.github.biticcf.mountain.core.common.result.WdCallbackResult;

/**
 * +回调方法定义
 * author: DanielCao
 * date:   2017年5月8日
 * time:   下午1:52:26
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
     * +避免名称歧义,用以替代executeAfter方法
     */
    default void executeAfterSuccess() {
    	// DO NOTHING
    }
    /**
     * +该方法主要在业务失败之后执行相关操作
     * @param e 业务抛出的异常或者null
     */
    default void executeAfterFailure(Throwable e) {
    	// DO NOTHING
    }
}
