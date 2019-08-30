/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月3日
 * @Time:   下午4:00:31
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(ShackleTemplatesRegistrar.class)
public @interface EnableShackleTemplates {
	/**
	 * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
	 * declarations e.g.: {@code @ComponentScan("org.my.pkg")} instead of
	 * {@code @ComponentScan(basePackages="org.my.pkg")}.
	 * @return the array of 'basePackages'.
	 */
	String[] value() default {};

	/**
	 * Base packages to scan for annotated components.
	 * <p>
	 * {@link #value()} is an alias for (and mutually exclusive with) this attribute.
	 * <p>
	 * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
	 * package names.
	 *
	 * @return the array of 'basePackages'.
	 */
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #basePackages()} for specifying the packages to
	 * scan for annotated components. The package of each class specified will be scanned.
	 * <p>
	 * Consider creating a special no-op marker class or interface in each package that
	 * serves no purpose other than being referenced by this attribute.
	 *
	 * @return the array of 'basePackageClasses'.
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * A custom <code>@Configuration</code> for all shackle templates. Can contain override
	 * <code>@Bean</code> definition for the pieces that make up the shackle.
	 *
	 * @see ShackleTemplatesConfiguration for the defaults
	 */
	Class<?>[] defaultConfiguration() default {};

	/**
	 * List of classes annotated with @ShackleTemplate. If not empty, disables classpath scanning.
	 * @return
	 */
	Class<?>[] templates() default {};
	
	/**
	 * 默认事务模板名称,如果定义了多个事务模板,这里需要用存在的一个模板名称覆盖
	 * @return
	 */
	String defaultServiceTemplateBeanName() default "wdServiceTemplate";
}
