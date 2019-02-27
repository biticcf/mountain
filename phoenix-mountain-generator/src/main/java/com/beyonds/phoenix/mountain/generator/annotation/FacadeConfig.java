/**
 * 
 */
package com.beyonds.phoenix.mountain.generator.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年2月27日
 * @Time:   上午9:53:47
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface FacadeConfig {
	// Facade名称前缀,只允许大写字母开头,后面跟小写字母,如Mydemo
	String name() default "";
	
	// Demo接口定义说明
	String description() default "接口定义说明";
	
	// 是否执行该facade的代码生成，true表示生成，false表示忽略
	boolean execGenerator() default false;
	
	// 是否生成Swagger文档 
	boolean useSwagger() default true;
}
