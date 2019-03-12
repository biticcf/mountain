/**
 * 
 */
package com.beyonds.phoenix.mountain.generator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年3月12日
 * @Time:   下午4:34:45
 *
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ForeignKey {
	// 外键名称
	String keyName();
	
	// 索引序号(复合外键中使用)
	int index() default 0;
	
	// 引用schema
	String refSchema() default "";
	
	// 引用table
	String refTable() default "";
	
	// 引用列
	String[] refFields() default {};
	
	// 删除引发操作:CASCADE、NO ACTION、RESTRICT、SET NULL
	String deleteAction() default "SET NULL";
	
	// 更新引发操作:CASCADE、NO ACTION、RESTRICT、SET NULL
	String updateAction() default "NO ACTION";
}
