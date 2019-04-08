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
 * @Date:   2019年3月12日
 * @Time:   下午3:17:27
 *
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface TableConfig {
	// Po名称(与Facade一致)
	String poName() default "";
	
	// Po对应数据库中的table名称
	String tableName();
	
	// 数据库表说明
	String comment() default "";
	
	// 是否生成对应的Model文件
	boolean genModel() default true;
	
	// 数据库引擎:InnoDB、MyISAM
	EnuTableEngine tableEngine() default EnuTableEngine.InnoDB;
	
	// 数据库表默认编码
	String tableCharset() default "utf8";
	
	// 数据库表默认编码校对集
	String tableCharsetCollate() default "utf8_general_ci";
	
	// 数据库表行格式:COMPACT、COMPRESSED、DEFAULT、DYNAMIC、FIXED、REDUNDANT
	// 如果字段较多text且较长，适合用DYNAMIC
	EnuRowFormat rowFormat() default EnuRowFormat.DYNAMIC;
	
	// 是否重新生成po类
	boolean reGeneratorPo() default false;
	
	// 是否重新生成model类
	boolean reGeneratorModel() default false;
}
