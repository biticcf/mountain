/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.aop;

import java.util.ArrayList;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * author: DanielCao
 * date:   2017年5月9日
 * time:   上午8:20:20
 *
 */
public class InterceptorChain implements MethodInterceptor {
	private List<MethodInterceptor> chains = new ArrayList<MethodInterceptor>();
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		InterceptorChainSupport support = new InterceptorChainSupport(
				invocation, new ArrayList<MethodInterceptor>(chains));
        return support.proceed();
	}
	
	public void setChains(List<MethodInterceptor> chains) {
        this.chains = chains;
    }
}
