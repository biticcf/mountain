/**
 * 
 */
package com.github.biticcf.mountain.shackle;

import static java.lang.String.format;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * author: Daniel.Cao
 * date: 2019年1月3日
 * time: 下午10:41:15
 *
 */
public class Util {

	public static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
		}
	}

	public static boolean isDefault(Method method) {
		// Default methods are public non-abstract, non-synthetic, and non-static
		// instance methods
		// declared in an interface.
		// method.isDefault() is not sufficient for our usage as it does not check
		// for synthetic methods. As a result, it picks up overridden methods as well as
		// actual default methods.
		final int SYNTHETIC = 0x00001000;
		return ((method.getModifiers()
				& (Modifier.ABSTRACT | Modifier.PUBLIC | Modifier.STATIC | SYNTHETIC)) == Modifier.PUBLIC)
				&& method.getDeclaringClass().isInterface();
	}

	public static String emptyToNull(String string) {
		return string == null || string.isEmpty() ? null : string;
	}

	public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
		if (reference == null) {
			// If either of these parameters is null, the right thing happens anyway
			throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
		}
		return reference;
	}
}
