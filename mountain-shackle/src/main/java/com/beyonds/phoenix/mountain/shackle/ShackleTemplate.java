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
	 * The name of the service with optional protocol prefix. Synonym for {@link #name()
	 * name}. A name must be specified for all templates, whether or not a url is provided.
	 * Can be specified as property key, eg: ${propertyKey}.
	 */
	@AliasFor("name")
	String value() default "";
	/**
	 * The name of the service with optional protocol prefix. Synonym for {@link #value() value}.
	 */
	@AliasFor("value")
	String name() default "";
	
	/**
	 * Sets the <code>@Qualifier</code> value for the shackle template.
	 */
	String qualifier() default "";

	/**
	 * A custom <code>@Configuration</code> for the shackle template. Can contain override
	 * <code>@Bean</code> definition for the pieces that make up the template.
	 *
	 * @see ShackleTemplatesConfiguration for the defaults
	 */
	Class<?>[] configuration() default {};
	
	/**
	 * 自定义事务模板bean名称
	 * 默认是用系统定义的bean
	 * 需要多数据源，可以在此指定自定义的模板bean名称
	 * @return 事务明半bean名称
	 */
	String wdServiceTemplateBeanName() default "";
	
	/**
	 * Whether to mark the shackle template proxy as a primary bean. Defaults to true.
	 */
	boolean primary() default true;
}
