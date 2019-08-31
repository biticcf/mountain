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
	 * 
	 * @param targetType type of the ShackleTemplate interface
	 * @param method invoked method, present on type or its super.
	 * @return configKey
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
	 * 
	 * @param <T> newInstance type
	 * @param target target
	 * @return target newInstance
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
		 * Allows you to override how reflective dispatch works inside of Shackle.
		 * @param invocationHandlerFactory invocationHandlerFactory
		 * @return invocationHandlerFactory
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
