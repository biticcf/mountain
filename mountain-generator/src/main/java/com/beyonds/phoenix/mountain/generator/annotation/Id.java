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
 * @Time:   下午4:03:56
 *
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Id {
	// 主键顺序(复合主键中使用)
	int index() default 0;
}
