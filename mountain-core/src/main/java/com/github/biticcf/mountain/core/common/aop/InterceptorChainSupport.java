/**
 * 
 */
package com.github.biticcf.mountain.core.common.aop;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * author: DanielCao
 * date:   2017年5月9日
 * time:   下午3:18:33
 *
 */
public class InterceptorChainSupport implements MethodInvocation {
	private static Log  logger = LogFactory.getLog(InterceptorChainSupport.class);

    private MethodInvocation  proxy;
    private List<MethodInterceptor> chains;
    
    public InterceptorChainSupport(MethodInvocation proxy, List<MethodInterceptor> chains) {
        this.proxy = proxy;
        this.chains = chains;
    }
    
	@Override
	public Object[] getArguments() {
		return proxy.getArguments();
	}
	
	@Override
	public Object proceed() throws Throwable {
		if (null != chains) {
            if (chains.size() > 0) {
                if (logger.isDebugEnabled()) {
                    logger.debug(" [ " + Thread.currentThread().getId() + " ] Invoke Chanin [ "
                                 + chains.size() + " ] , name is : " + chains.get(0).getClass());
                }
                return chains.remove(0).invoke(this);
            }
        }
        return proxy.proceed();
	}
	
	@Override
	public Object getThis() {
		return proxy.getThis();
	}
	
	@Override
	public AccessibleObject getStaticPart() {
		return proxy.getStaticPart();
	}
	
	@Override
	public Method getMethod() {
		return proxy.getMethod();
	}
}
