/**
 * 
 */
package com.github.biticcf.mountain.core.common.lang;

import org.slf4j.Logger;

/**
 * author: DanielCao
 * date:   2018年5月28日
 * time:   上午9:35:38
 *
 */
public interface Logable {
	/**
	 * +输出错误日志
	 * @param logger 输出器
	 * @param message 输出消息
	 * @param t 异常信息
	 */
	default void writeErrorLog(Logger logger, String message, Throwable t) {
		if (logger.isErrorEnabled()) {
			logger.error(message, t);
		}
	}

	/**
	 * 输出错误日志
	 * @param logger 输出器
	 * @param message 输出消息
	 */
	default void writeErrorLog(Logger logger, String message) {
		if (logger.isErrorEnabled()) {
			logger.error(message);
		}
	}

	/**
	 * 输出警告日志
	 * @param logger 输出器
	 * @param message 输出消息
	 */
	default void writeWarnLog(Logger logger, String message) {
		if (logger.isWarnEnabled()) {
			logger.warn(message);
		}
	}

	/**
	 * 输出信息日志
	 * @param logger 输出器
	 * @param message 输出消息
	 */
	default void writeInfoLog(Logger logger, String message) {
		if (logger.isInfoEnabled()) {
			logger.info(message);
		}
	}

	/**
	 * 输出调试日志
	 * @param logger 输出器
	 * @param message 输出消息
	 */
	default void writeDebugLog(Logger logger, String message) {
		if (logger.isDebugEnabled()) {
			logger.debug(message);
		}
	}
}
