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
 * @Time:   下午3:41:42
 *
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface ColumnConfig {
	// Po对应的property名称
	String propertyName();
	
	// 列名称
	String columnName();
	
	// 主键标志
	boolean primaryKeyFlag() default false;
	
	// 列类型
	EnuFieldType columnType();
	
	// 是否unsigned标志
	boolean unsignedFlag() default false;
	
	// 列宽度
	int columnLength() default 0;
	
	// 小数宽度
	int decimalLength() default 0;
	
	// 列值是否允许空
	boolean nullable() default true;
	
	// 列默认值
	String defaultValue() default "";
}
