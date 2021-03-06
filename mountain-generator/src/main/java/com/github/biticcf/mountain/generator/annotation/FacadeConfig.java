/**
 * 
 */
package com.github.biticcf.mountain.generator.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * author: Daniel.Cao
 * date:   2019年2月27日
 * time:   上午9:53:47
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
	
	// 是否重新生成Facade文件,true重新生成,false不重新生成
	boolean reGenerator() default false;
	
	// 是否生成DAO层代码，需要先定义{name}Po文件
	boolean genDaoCode() default false;
}
