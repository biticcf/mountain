/**
 * 
 */
package com.github.biticcf.mountain.shackle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.github.biticcf.mountain.core.common.service.WdServiceCallback;

/**
 * author: Daniel.Cao
 * date:   2019年1月3日
 * time:   下午4:15:03
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ShackleDomain {
	/**
	 * The name of the service with optional protocol prefix. 
	 * @return value
	 */
	@AliasFor("name")
	String value() default "";
	
	/**
	 * The name of the method with optional protocol prefix.
	 * @return name
	 */
	@AliasFor("value")
	String name() default "";
	
	/**
	 * Sets the Qualifier value for the shackle domain.
	 * @return qualifier
	 */
	String qualifier() default "";
	/**
	 * 
	 * @return configurations
	 */
	Class<?>[] configuration() default {};
	
	/**
	 * The bean id/name of domain
	 * @return domainName
	 */
	String domainName() default "";
	
	/**
	 * 
	 * @return domain
	 */
	Class<? extends WdServiceCallback<?>> domain();
	
	/**
	 * +是否启用事务
	 * @return withTrans 启用事务标志
	 */
	boolean withTrans() default true;
	
	/**
	 * 自定义事务模板bean名称
	 * 默认是用系统定义的bean
	 * 需要多数据源，可以在此指定自定义的模板bean名称
	 * @return wdServiceTemplateBeanName 事务明半bean名称
	 */
	String wdServiceTemplateBeanName() default "";
	
	/**
	 * Whether to mark the shackle domain proxy as a primary bean. Defaults to true.
	 * @return primary
	 */
	boolean primary() default true;
}
