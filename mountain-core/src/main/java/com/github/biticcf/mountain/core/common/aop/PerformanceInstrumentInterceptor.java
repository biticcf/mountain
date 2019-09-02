/**
 * 
 */
package com.github.biticcf.mountain.core.common.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * author  DanielCao
 * date    2015年4月1日
 * time    下午7:57:34
 *
 */
public class PerformanceInstrumentInterceptor implements MethodInterceptor {
	
	public PerformanceInstrumentInterceptor() {
        super();
    }
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		String name = invocation.getMethod().getDeclaringClass().getName() + "."
                + invocation.getMethod().getName();
		
		Profiler.enter("Invoking method: " + name);
		
		try {
			return invocation.proceed();
		} finally {
			Profiler.release();
		}
	}
}
