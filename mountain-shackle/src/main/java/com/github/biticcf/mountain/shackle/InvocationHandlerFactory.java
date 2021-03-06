/**
 * 
 */
package com.github.biticcf.mountain.shackle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * author: Daniel.Cao
 * date: 2019年1月3日
 * time: 下午11:37:56
 *
 */
public interface InvocationHandlerFactory {
	InvocationHandler create(Target<?> target, Map<Method, MethodHandler> dispatch);

	/**
	 * Like
	 * {@link InvocationHandler#invoke(Object, java.lang.reflect.Method, Object[])},
	 * except for a single method.
	 */
	interface MethodHandler {

		Object invoke(Object[] argv) throws Throwable;
	}

	static final class Default implements InvocationHandlerFactory {

		@Override
		public InvocationHandler create(Target<?> target, Map<Method, MethodHandler> dispatch) {
			return new ReflectiveShackle.ShackleInvocationHandler(target, dispatch);
		}
	}
}
