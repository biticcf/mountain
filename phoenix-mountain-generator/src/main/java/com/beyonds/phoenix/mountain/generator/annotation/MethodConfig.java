/**
 * 
 */
package com.beyonds.phoenix.mountain.generator.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年2月27日
 * @Time:   上午10:06:17
 *
 */
@Documented
@Retention(RUNTIME)
@Target(METHOD)
public @interface MethodConfig {
	// 方法名称
	String name() default "";
	
	// 方法定义说明
	String description() default "方法定义说明";
	
	// 查询结果是否列表(不分页的列表)
	boolean listResultFlag() default false;
	
	// 查询结果是否列表(分页的列表)
	boolean paginationFlag() default false;
	
	// 该方法对应业务是否支持事务
	boolean withTransaction() default true;
}
