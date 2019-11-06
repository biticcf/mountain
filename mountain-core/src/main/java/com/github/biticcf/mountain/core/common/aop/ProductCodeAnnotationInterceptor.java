/**
 * 
 */
package com.github.biticcf.mountain.core.common.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.biticcf.mountain.core.common.annotation.ProductCode;
import com.github.biticcf.mountain.core.common.annotation.ProductLogLevelEnum;

/**
 * 
 * author: DanielCao
 * date:   2017年5月9日
 * time:   上午8:20:36
 *
 */
public class ProductCodeAnnotationInterceptor implements MethodInterceptor {
	private static Logger log = LoggerFactory.getLogger(ProductCodeAnnotationInterceptor.class);

	/**
	 * 输出业务日志，日志分两种级别，一种为开发时候debug,正式环境为info。info会输出业务日志
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String name = invocation.getMethod().getDeclaringClass().getName() 
				+ "." + invocation.getMethod().getName();
		
		ProductCode productCode = invocation.getMethod().getAnnotation(ProductCode.class);
		if (productCode != null) {
			StringBuilder builder = new StringBuilder();
			builder.append("方法:");
			builder.append(name).append(";");
			builder.append("产品码：").append(productCode.code()).append(";");
			// 级别为输出业务日志
			if (ProductLogLevelEnum.INFO.compareTo(productCode.logLevel()) == 0) {
				if (log.isInfoEnabled()) {
					log.info(builder.toString());
				}
			} else if (ProductLogLevelEnum.DEBUG.compareTo(productCode.logLevel()) == 0) {
				if (log.isDebugEnabled()) {
					log.debug(builder.toString());
				}
			} else if (ProductLogLevelEnum.WARN.compareTo(productCode.logLevel()) == 0) {
				if (log.isWarnEnabled()) {
					log.warn(builder.toString());
				}
			}
		}
		
		return invocation.proceed();
	}
}
