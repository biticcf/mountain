/**
 * 
 */
package com.github.biticcf.mountain.core.common.framework;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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
 * +框架性能监控配置
 * author: Daniel.Cao
 * date:   2019年10月23日
 * time:   上午9:29:36
 *
 */
@Configuration(proxyBeanMethods = false)
@EnableAsync
public class PerformAutoConfig {
	
	/**
	 * +定义bean 
	 * @return PerformanceMonitorInterceptor实例
	 */
	@ConditionalOnMissingBean(PerformanceMonitorInterceptor.class)
	@Bean
	@ConfigurationProperties(prefix = "performframework")
	public PerformanceMonitorInterceptor performanceMonitorInterceptor() {
		// ${performframework.threshold}
		return new PerformanceMonitorInterceptor();
	}
	
	/**
	 * +定义bean 
	 * @return ProductCodeAnnotationInterceptor实例
	 */
	@ConditionalOnMissingBean(ProductCodeAnnotationInterceptor.class)
	@Bean
	public ProductCodeAnnotationInterceptor productCodeAnnotationInterceptor() {
		return new ProductCodeAnnotationInterceptor();
	}
	
	/**
	 * +定义bean 
	 * @return PerformanceInstrumentInterceptor实例
	 */
	@ConditionalOnMissingBean(PerformanceInstrumentInterceptor.class)
	@Bean
	public PerformanceInstrumentInterceptor performanceInstrumentInterceptor() {
		return new PerformanceInstrumentInterceptor();
	}
	
	/**
	 * +定义bean 
	 * @param productCodeAnnotationInterceptor productCodeAnnotationInterceptor实例
	 * @param performanceMonitorInterceptor performanceMonitorInterceptor实例
	 * @return InterceptorChain实例
	 */
	@ConditionalOnMissingBean(name = "interceptorChain")
	@Bean(name = "interceptorChain")
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
	 * +定义bean
	 * @return FilterBeanNameAutoProxyCreator实例
	 */
	@ConditionalOnMissingBean(name = "entranceProxyCreator")
	@Bean(name = "entranceProxyCreator")
	@ConfigurationProperties(prefix = "performframework.entrance")
	public FilterBeanNameAutoProxyCreator entranceProxyCreator() {
		//${performframework.entrance.proxyTargetClass}
		//${performframework.entrance.interceptorNames}
		//${performframework.entrance.beanNames} 和 ${performframework.entrance.beanNamesExclude}
		return new FilterBeanNameAutoProxyCreator();
	}
	
	/**
	 * +定义bean
	 * @return FilterBeanNameAutoProxyCreator实例
	 */
	@ConditionalOnMissingBean(name = "chainProxyCreator")
	@Bean(name = "chainProxyCreator")
	@ConfigurationProperties(prefix = "performframework.chain")
	public FilterBeanNameAutoProxyCreator chainProxyCreator() {
		//${performframework.chain.proxyTargetClass}
		//${performframework.chain.interceptorNames}
		//${performframework.chain.beanNames} 和 ${performframework.chain.beanNamesExclude}
		return new FilterBeanNameAutoProxyCreator();
	}
	
	/**
	 * +高效线程池
	 * @return 线程池
	 */
	@ConditionalOnMissingBean(name = "asyncEffective")
	@Bean(name = "asyncEffective", initMethod = "initialize", destroyMethod = "destroy")
	@ConfigurationProperties(prefix = "async.effective")
	public AsyncTaskExecutor asyncEffective() {
		return new ThreadPoolTaskExecutor();
	}
	
	/**
	 * +常规线程池
	 * @return 线程池
	 */
	@ConditionalOnMissingBean(name = "asyncCommon")
	@Bean(name = "asyncCommon", initMethod = "initialize", destroyMethod = "destroy")
	@ConfigurationProperties(prefix = "async.common")
	public AsyncTaskExecutor asyncCommon() {
		return new ThreadPoolTaskExecutor();
	}
}
