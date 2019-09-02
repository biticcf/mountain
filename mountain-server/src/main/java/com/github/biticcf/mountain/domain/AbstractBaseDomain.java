/**
 * 
 */
package com.github.biticcf.mountain.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.biticcf.mountain.core.common.service.WdServiceCallback;
import com.github.biticcf.mountain.domain.support.ConstantContext;

/**
 * author: DanielCao
 * date:   2017年5月9日
 * time:   下午5:00:52
 *
 * @param <T> 数据类型
 */
public abstract class AbstractBaseDomain<T> implements WdServiceCallback<T> {
	protected static Logger logger = LoggerFactory.getLogger("COMMON.LOG");
	
	private ConstantContext constantContext;
	
	public AbstractBaseDomain(ConstantContext constantContext) {
		this.constantContext = constantContext;
	}
	
	public ConstantContext getConstantContext() {
		return constantContext;
	}
	
	/**
	 * info日志输出
	 * @param message 日志信息
	 */
	protected void info(String message) {
		if (logger.isInfoEnabled()) {
			logger.info(message);
		}
	}

	/**
	 * warn日志输出
	 * @param message 日志信息
	 */
	protected void warn(String message) {
		if (logger.isWarnEnabled()) {
			logger.warn(message);
		}
	}

	/**
	 * error日志输出
	 * @param message 日志信息
	 */
	protected void error(String message) {
		if (logger.isErrorEnabled()) {
			logger.error(message);
		}
	}
	
	/**
	 * error日志输出
	 * @param message 日志信息
	 * @param th 异常信息
	 */
	protected void error(String message, Throwable th) {
		if (logger.isErrorEnabled()) {
			logger.error(message, th);
		}
	}

}
