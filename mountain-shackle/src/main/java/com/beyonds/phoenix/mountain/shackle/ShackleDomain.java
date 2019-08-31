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

import com.beyonds.phoenix.mountain.core.common.service.WdServiceCallback;

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
	 * The name of the service with optional protocol prefix. Synonym for {@link #name()
	 * name}. A name must be specified for all domains, whether or not a url is provided.
	 * Can be specified as property key, eg: ${propertyKey}.
	 */
	@AliasFor("name")
	String value() default "";
	/**
	 * The name of the method with optional protocol prefix. Synonym for {@link #value() value}.
	 */
	@AliasFor("value")
	String name() default "";
	
	/**
	 * Sets the <code>@Qualifier</code> value for the shackle domain.
	 */
	String qualifier() default "";

	/**
	 * A custom <code>@Configuration</code> for the shackle domain. Can contain override
	 * <code>@Bean</code> definition for the pieces that make up the domain.
	 *
	 * @see ShackleTemplatesConfiguration for the defaults
	 */
	Class<?>[] configuration() default {};
	
	/**
	 * The bean id/name of domain
	 * @return
	 */
	String domainName() default "";

	/**
	 * Business domain class for the specified business interface. The domain class must
	 * implement the WdServiceCallback<?> interface and be a valid @Scope("prototype") spring bean.
	 */
	Class<? extends WdServiceCallback<?>> domain();
	
	/**
	 * +是否启用事务
	 * @return 启用事务标志
	 */
	boolean withTrans() default true;
	
	/**
	 * 自定义事务模板bean名称
	 * 默认是用系统定义的bean
	 * 需要多数据源，可以在此指定自定义的模板bean名称
	 * @return 事务明半bean名称
	 */
	String wdServiceTemplateBeanName() default "";
	
	/**
	 * Whether to mark the shackle domain proxy as a primary bean. Defaults to true.
	 */
	boolean primary() default true;
}
