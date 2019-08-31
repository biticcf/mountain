/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * 产品码注解
 * author  DanielCao
 * date    2015年4月1日
 * time    下午7:55:36
 *
 */
@Retention(RetentionPolicy.RUNTIME) 
@Target(ElementType.METHOD)
@Inherited 
@Documented
public @interface ProductCode {
	/**
	 * 编码
	 * @return 编码
	 */
	String code();
	
	/**
	 * 版本号
	 * @return 版本号
	 */
	String version();
	
	/**
	 * 日志级别
	 * @return 日志级别
	 */
	ProductLogLevelEnum logLevel();
}
