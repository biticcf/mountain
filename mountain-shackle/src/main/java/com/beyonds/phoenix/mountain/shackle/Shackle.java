/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

import com.beyonds.phoenix.mountain.shackle.ReflectiveShackle.ParseHandlersByName;

/**
 * author: Daniel.Cao
 * date: 2019年1月3日
 * time: 下午7:23:01
 *
 */
public abstract class Shackle {
	public static Builder builder() {
	    return new Builder();
	}
	/**
	 * Configuration keys are formatted as unresolved <a href=
	 * "http://docs.oracle.com/javase/6/docs/jdk/api/javadoc/doclet/com/sun/javadoc/SeeTag.html"
	 * >see tags</a>. This method exposes that format, in case you need to create
	 * the same value as {@link MethodMetadata#configKey()} for correlation
	 * purposes.
	 *
	 * <p>
	 * Here are some sample encodings:
	 *
	 * <pre>
	 * <ul>
	 *   <li>{@code Route53}: would match a class {@code route53.Route53}</li>
	 *   <li>{@code Route53#list()}: would match a method {@code route53.Route53#list()}</li>
	 *   <li>{@code Route53#listAt(Marker)}: would match a method {@code
	 * route53.Route53#listAt(Marker)}</li>
	 *   <li>{@code Route53#listByNameAndType(String, String)}: would match a method {@code
	 * route53.Route53#listAt(String, String)}</li>
	 * </ul>
	 * </pre>
	 *
	 * Note that there is no whitespace expected in a key!
	 *
	 * @param targetType {@link Target#type() type} of the ShackleTemplate
	 *                   interface.
	 * @param method     invoked method, present on {@code type} or its super.
	 * @see MethodMetadata#configKey()
	 */
	public static String configKey(Class<?> targetType, Method method) {
		StringBuilder builder = new StringBuilder();
		builder.append(targetType.getSimpleName());
		builder.append('#').append(method.getName()).append('(');
		for (Type param : method.getGenericParameterTypes()) {
			param = Types.resolve(targetType, targetType, param);
			builder.append(Types.getRawType(param).getSimpleName()).append(',');
		}
		if (method.getParameterTypes().length > 0) {
			builder.deleteCharAt(builder.length() - 1);
		}
		return builder.append(')').toString();
	}

	/**
	 * Returns a new instance of a Domain callback, defined by annotations in the
	 * {@link Contract}, for the specified {@code target}. You should cache this
	 * result.
	 */
	public abstract <T> T newInstance(Target<T> target);

	public static class Builder {
		private Contract contract;
		private DomainExecutor domainExecutor;
		private InvocationHandlerFactory invocationHandlerFactory = new InvocationHandlerFactory.Default();
		
		public Builder contract(Contract contract) {
			this.contract = contract;
			
			return this;
		}
		
		public Builder domainExecutor(DomainExecutor domainExecutor) {
			this.domainExecutor = domainExecutor;

			return this;
		}

		/**
		 * Allows you to override how reflective dispatch works inside of Feign.
		 */
		public Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
			this.invocationHandlerFactory = invocationHandlerFactory;
			
			return this;
		}

		public <T> T target(Class<T> apiType, String name) {
			return target(new Target.HardCodedTarget<T>(apiType, name));
		}

		public <T> T target(Target<T> target) {
			return build().newInstance(target);
		}

		public Shackle build() {
			Util.checkNotNull(contract, "Contract Can not Null!");
			SynchronousMethodHandler.Factory synchronousMethodHandlerFactory = new SynchronousMethodHandler.Factory(
					domainExecutor);
			ParseHandlersByName handlersByName = new ParseHandlersByName(contract, synchronousMethodHandlerFactory);
			
			return new ReflectiveShackle(handlersByName, invocationHandlerFactory);
		}
	}
}
