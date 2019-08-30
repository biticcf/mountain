/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.aop;

import java.lang.annotation.Annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beyonds.phoenix.mountain.core.common.annotation.ProductCode;
import com.beyonds.phoenix.mountain.core.common.annotation.ProductLogLevelEnum;

/**
 * 
 * @Author: DanielCao
 * @Date:   2017年5月9日
 * @Time:   上午8:20:36
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
		Annotation[] annotation = invocation.getMethod().getAnnotations();
		if (annotation != null && annotation.length > 0) {
			for (Annotation annotations : annotation) {
				if (annotations instanceof ProductCode) {
					ProductCode productCode = (ProductCode) annotations;
					// 级别为输出业务日志
					if (productCode.logLevel().compareTo(ProductLogLevelEnum.INFO) == 0) {
						StringBuilder builder = new StringBuilder();
						builder.append("方法:");
						builder.append(name).append(";");
						builder.append("产品码：").append(productCode.code()).append(";");
						log.info(builder.toString());
					}
				}
			}
		}
		return invocation.proceed();
	}
}
