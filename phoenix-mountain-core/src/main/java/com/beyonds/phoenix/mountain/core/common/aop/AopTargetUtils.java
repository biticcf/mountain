/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.aop;

import java.lang.reflect.Field;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.lang.Nullable;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月17日
 * @Time:   下午2:16:23
 * 解析spring的代理对象，获取原被代理对象
 */
public class AopTargetUtils {
	/** 
	 * 获取 目标对象 
	 * @param proxy 代理对象 
	 * @return 目标对象
	 * @throws Exception 
	 */
	public static Object getTarget(Object proxy) throws ProxyBeansException {
		if (!AopUtils.isAopProxy(proxy)) {
			return proxy;
		}
		
		try {
			if (AopUtils.isJdkDynamicProxy(proxy)) {
				proxy = getJdkDynamicProxyTargetObject(proxy);
			} else {
				proxy = getCglibProxyTargetObject(proxy);
			}
		} catch (Exception e) {
			throw new ProxyBeansException(e.getMessage(), e.getCause());
		}
		
		return getTarget(proxy);
	}
	
	private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
		Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
		h.setAccessible(true);
		
		Object dynamicAdvisedInterceptor = h.get(proxy);
		Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
		advised.setAccessible(true);
		
		Object target = ((AdvisedSupport) advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
		
		return target;
	}
	
	private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
		Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
		h.setAccessible(true);
		
		AopProxy aopProxy = (AopProxy) h.get(proxy);
		Field advised = aopProxy.getClass().getDeclaredField("advised");
		advised.setAccessible(true);
		
		Object target = ((AdvisedSupport) advised.get(aopProxy)).getTargetSource().getTarget();
		
		return target;
	}
	
	static class ProxyBeansException extends BeansException {
		private static final long serialVersionUID = -7491852792495065907L;

		public ProxyBeansException(String msg) {
			super(msg);
		}
		
		public ProxyBeansException(@Nullable String msg, @Nullable Throwable cause) {
			super(msg, cause);
		}
	}
}
