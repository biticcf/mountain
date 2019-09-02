/**
 * 
 */
package com.github.biticcf.mountain.core.common.framework;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.github.biticcf.mountain.core.common.aop.FilterBeanNameAutoProxyCreator;
import com.github.biticcf.mountain.core.common.aop.InterceptorChain;
import com.github.biticcf.mountain.core.common.aop.PerformanceInstrumentInterceptor;
import com.github.biticcf.mountain.core.common.aop.PerformanceMonitorInterceptor;
import com.github.biticcf.mountain.core.common.aop.ProductCodeAnnotationInterceptor;

/**
 * author: DanielCao
 * date:   2017年5月9日
 * time:   上午8:22:09
 *
 */
@Configuration
@EnableAsync
public class PerformFramework {
	
	/**
	 * 定义bean 
	 * @return PerformanceMonitorInterceptor实例
	 */
		@Bean(name = "performanceMonitorInterceptor")
	@Qualifier(value = "performanceMonitorInterceptor")
	@ConfigurationProperties(prefix = "performframework")
	public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
		// ${performframework.threshold}
		return new PerformanceMonitorInterceptor();
	}
	
	/**
	 * 定义bean 
	 * @return ProductCodeAnnotationInterceptor实例
	 */
	@Bean(name = "productCodeAnnotationInterceptor")
	@Qualifier(value = "productCodeAnnotationInterceptor")
	public ProductCodeAnnotationInterceptor productCodeAnnotationInterceptor() {
		return new ProductCodeAnnotationInterceptor();
	}
	
	/**
	 * 定义bean 
	 * @return PerformanceInstrumentInterceptor实例
	 */
	@Bean(name = "performanceInstrumentInterceptor")
	@Qualifier(value = "performanceInstrumentInterceptor")
	public PerformanceInstrumentInterceptor performanceInstrumentInterceptor() {
		return new PerformanceInstrumentInterceptor();
	}
	
	/**
	 * 定义bean 
	 * @param productCodeAnnotationInterceptor productCodeAnnotationInterceptor实例
	 * @param performanceMonitorInterceptor performanceMonitorInterceptor实例
	 * @return InterceptorChain实例
	 */
	@Bean(name = "interceptorChain")
	@Qualifier(value = "interceptorChain")
	@ConfigurationProperties(prefix = "performframework")
	public InterceptorChain interceptorChain(
	        ProductCodeAnnotationInterceptor productCodeAnnotationInterceptor,
			PerformanceMonitorInterceptor performanceMonitorInterceptor) {
		InterceptorChain interceptorChain = new InterceptorChain();
		
		List<MethodInterceptor> chains = new ArrayList<MethodInterceptor>();
		chains.add(productCodeAnnotationInterceptor);
		chains.add(performanceMonitorInterceptor);
		interceptorChain.setChains(chains);
		
		return interceptorChain;
	}
	
	/**
	 * 定义bean
	 * @return FilterBeanNameAutoProxyCreator实例
	 */
	@Bean(name = "entranceProxyCreator")
	@ConfigurationProperties(prefix = "performframework.entrance")
	public FilterBeanNameAutoProxyCreator entranceProxyCreator() {
		//${performframework.entrance.proxyTargetClass}
		//${performframework.entrance.interceptorNames}
		//${performframework.entrance.beanNames} 和 ${performframework.entrance.beanNamesExclude}
		return new FilterBeanNameAutoProxyCreator();
	}
	
	/**
	 * 定义bean
	 * @return FilterBeanNameAutoProxyCreator实例
	 */
	@Bean(name = "chainProxyCreator")
	@ConfigurationProperties(prefix = "performframework.chain")
	public FilterBeanNameAutoProxyCreator chainProxyCreator() {
		//${performframework.chain.proxyTargetClass}
		//${performframework.chain.interceptorNames}
		//${performframework.chain.beanNames} 和 ${performframework.chain.beanNamesExclude}
		return new FilterBeanNameAutoProxyCreator();
	}
	
	/**
	 * 高效线程池
	 * @return 线程池
	 */
	@Bean(name = "asyncEffective", initMethod = "initialize", destroyMethod = "destroy")
	@Qualifier(value = "asyncEffective")
	@ConfigurationProperties(prefix = "async.effective")
	public AsyncTaskExecutor asyncEffective() {
		return new ThreadPoolTaskExecutor();
	}
	
	/**
	 * 常规线程池
	 * @return 线程池
	 */
	@Bean(name = "asyncCommon", initMethod = "initialize", destroyMethod = "destroy")
	@Qualifier(value = "asyncCommon")
	@ConfigurationProperties(prefix = "async.common")
	public AsyncTaskExecutor asyncCommon() {
		return new ThreadPoolTaskExecutor();
	}
}
