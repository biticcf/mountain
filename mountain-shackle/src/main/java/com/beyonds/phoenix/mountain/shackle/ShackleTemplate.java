/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * author: Daniel.Cao
 * date:   2019年1月3日
 * time:   下午4:15:03
 *
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShackleTemplate {
	/**
	 * 
	 * @return value
	 */
	@AliasFor("name")
	String value() default "";
	/**
	 * 
	 * @return name
	 */
	@AliasFor("value")
	String name() default "";
	/**
	 * 
	 * @return qualifier
	 */
	String qualifier() default "";
	/**
	 * 
	 * @return configurations
	 */
	Class<?>[] configuration() default {};
	
	/**
	 * 自定义事务模板bean名称
	 * 默认是用系统定义的bean
	 * 需要多数据源，可以在此指定自定义的模板bean名称
	 * @return  wdServiceTemplateBeanName 事务明半bean名称
	 */
	String wdServiceTemplateBeanName() default "";
	
	/**
	 * Whether to mark the shackle template proxy as a primary bean. Defaults to true.
	 * @return primary
	 */
	boolean primary() default true;
}
