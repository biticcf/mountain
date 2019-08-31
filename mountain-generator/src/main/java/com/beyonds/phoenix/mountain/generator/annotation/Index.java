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
 * author: Daniel.Cao
 * date:   2019年3月12日
 * time:   下午4:06:09
 *
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Index {
	// 索引名称
	String indexName();
	
	// 索引序号(复合索引中使用)
	int index() default 0;
	
	// 索引类型:#EnuIndexType
	EnuIndexType indexType() default EnuIndexType.NORMAL;
	
	// 索引函数:#EnuIndexMethod
	EnuIndexMethod indexMethod() default EnuIndexMethod.BTREE;
	
	// 备注
	String comment() default "";
}
