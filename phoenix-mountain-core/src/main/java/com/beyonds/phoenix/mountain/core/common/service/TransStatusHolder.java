/**
 * @Info:      TransStatusHolder.java
 * @Copyright: 2019
 */
package com.beyonds.phoenix.mountain.core.common.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.NamedThreadLocal;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年3月25日
 * @Time:   下午5:00:45
 * +设置是否启用事务标志
 * +避免在非事务通道执行事务
 * +避免出现使用失误导致数据不一致
 */
public final class TransStatusHolder {
	private static final Log logger = LogFactory.getLog(TransStatusHolder.class);
	
	private static final ThreadLocal<Boolean> transStatus = new NamedThreadLocal<>("TransactionalStatus");
	
	/**
	 * +获取当前线程的事务状态
	 * @return 事务状态,默认是启用事务
	 */
	public static boolean getTransStatus() {
		Boolean status = transStatus.get();
		
		boolean _status = status == null ? true : status.booleanValue();
		if (logger.isDebugEnabled()) {
			logger.debug("getTransStatus = [" + status + ", " + _status + "].");
		}
		
		return _status;
	}
	
	/**
	 * +标志当前线程启用事务
	 */
	public static void enableTransaction() {
		setTransStatus(true);
	}
	
	/**
	 * +标志当前线程不启用事务
	 */
	public static void disableTransaction() {
		setTransStatus(false);
	}
	
	/**
	 * +设置当前线程的事务状态
	 * @param status 事务状态,默认是启用事务
	 */
	private static void setTransStatus(Boolean status) {
		boolean _status = status == null ? true : status.booleanValue();
		if (logger.isDebugEnabled()) {
			logger.debug("setTransStatus = [" + status + ", " + _status + "].");
		}
		
		transStatus.set(_status);
	}
	
	/**
	 * +移除当前线程的事务状态标志
	 */
	public static void removeTransStatus() {
		transStatus.remove();
		
		if (logger.isDebugEnabled()) {
			logger.debug("RemoveTransStatus!");
		}
	}
}
