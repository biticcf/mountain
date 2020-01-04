/**
 * 
 */
package com.github.biticcf.mountain.core.common.aop;

import java.time.Duration;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * +性能监控阈值
 * author: Daniel.Cao
 * date:   2019年10月10日
 * time:   上午10:19:07
 *
 */
public class PerformanceMonitorInterceptor implements MethodInterceptor {
	private static Log logger = LogFactory.getLog(PerformanceMonitorInterceptor.class);
	
	private Duration threshold = Duration.ofMillis(100); //时间阈值

	public PerformanceMonitorInterceptor() {
		super();
	}
	
	/**
	 * 判断方法调用的时间是否超过阈值，如果是，则打印性能日志.
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String name = invocation.getMethod().getDeclaringClass().getName() 
				+ "." + invocation.getMethod().getName();
		
		Profiler.start("Invoking method: " + name);

		try {
			return invocation.proceed();
		} finally {
			Profiler.release();
			if (Profiler.isReleased()) {
				/*
				 * 这个判断，
				 * 保证了PerformanceMonitorInterceptor兼容了PerformanceInstrumentInterceptor的功能
				 */
				long elapseTime = Profiler.getDuration();
				if (elapseTime > threshold.toMillis()) {
					StringBuilder builder = new StringBuilder();
					builder.append(" ").append(name); // 方法
					builder.append(" ").append(threshold.toMillis()).append(" (ms)"); // 阈值(ms)
					builder.append(" ").append(elapseTime).append(" (ms)\r\n"); // 实际执行时间(ms)
					builder.append(Profiler.dump());
					logger.info(builder.toString());
				} else {
					if (logger.isDebugEnabled()) {
						StringBuilder builder = new StringBuilder();
						builder.append(" ").append(name); // 方法
						// 实际执行时间为 (ms)
						builder.append(" ").append(elapseTime).append(" (ms)\r\n");
					}
				}
				
				Profiler.reset();
			}
		}
	}

	/**
	 * @return the threshold
	 */
	public Duration getThreshold() {
		return threshold;
	}

	/**
	 * @param threshold the threshold to set
	 */
	public void setThreshold(Duration threshold) {
		this.threshold = threshold;
	}
}
