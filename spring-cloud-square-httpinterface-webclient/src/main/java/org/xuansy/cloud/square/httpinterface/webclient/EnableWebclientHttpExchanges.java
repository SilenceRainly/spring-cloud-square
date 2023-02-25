package org.xuansy.cloud.square.httpinterface.webclient;

import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.AliasFor;
import org.springframework.web.service.annotation.HttpExchange;

import java.lang.annotation.*;

/**
 * @author : xuansy
 * @version : 1.0
 * @email : 1292798418@qq.com
 * @date : 2023/2/25 10:14
 * @project_name : spring-cloud-square
 * @package_name : org.xuansy.cloud.square.httpinterface
 * @name : EnableHttpExchanges
 * @description :
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(WebClientHttpExchangesRegistrar.class)
public @interface EnableWebclientHttpExchanges {

	/**
	 * Alias for the {@link #basePackages()} attribute. Allows for more concise annotation
	 * declarations e.g.: {@code @ComponentScan("org.my.pkg")} instead of
	 * {@code @ComponentScan(basePackages="org.my.pkg")}.
	 *
	 * @return the array of 'basePackages'.
	 */
	@AliasFor(value = "basePackages")
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
	@AliasFor(value = "value")
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
	 * List of classes annotated with @HttpExchange. If not empty, disables classpath
	 * scanning.
	 *
	 * @return an array of {@link HttpExchange}-annotated classes
	 */
	Class<?>[] httpExchanges() default {};

}
