/**
 * 
 */
package com.github.biticcf.mountain.generator.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * author: Daniel.Cao
 * date:   2019年3月12日
 * time:   下午4:03:56
 *
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Id {
	// 主键顺序(复合主键中使用)
	int index() default 0;
}