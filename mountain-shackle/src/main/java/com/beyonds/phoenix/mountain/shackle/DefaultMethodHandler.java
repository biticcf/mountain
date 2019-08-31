/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.beyonds.phoenix.mountain.shackle.InvocationHandlerFactory.MethodHandler;

/**
 * author: Daniel.Cao
 * date: 2019年1月4日
 * time: 上午12:28:58
 *
 */
final class DefaultMethodHandler implements MethodHandler {

	private final MethodHandle unboundHandle;

	private MethodHandle handle;

	public DefaultMethodHandler(Method defaultMethod) {
		try {
			Class<?> declaringClass = defaultMethod.getDeclaringClass();
			Field field = Lookup.class.getDeclaredField("IMPL_LOOKUP");
			field.setAccessible(true);
			Lookup lookup = (Lookup) field.get(null);

			this.unboundHandle = lookup.unreflectSpecial(defaultMethod, declaringClass);
		} catch (NoSuchFieldException ex) {
			throw new IllegalStateException(ex);
		} catch (IllegalAccessException ex) {
			throw new IllegalStateException(ex);
		}
	}

	public void bindTo(Object proxy) {
		if (handle != null) {
			throw new IllegalStateException("Attempted to rebind a default method handler that was already bound");
		}
		handle = unboundHandle.bindTo(proxy);
	}

	@Override
	public Object invoke(Object[] argv) throws Throwable {
		if (handle == null) {
			throw new IllegalStateException("Default method handler invoked before proxy has been bound.");
		}
		return handle.invokeWithArguments(argv);
	}
}
