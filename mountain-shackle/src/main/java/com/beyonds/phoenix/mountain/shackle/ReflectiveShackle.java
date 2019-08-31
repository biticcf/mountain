/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.beyonds.phoenix.mountain.shackle.InvocationHandlerFactory.MethodHandler;

/**
 * author: Daniel.Cao
 * date: 2019年1月3日
 * time: 下午11:40:19
 *
 */
public class ReflectiveShackle extends Shackle {
	private final ParseHandlersByName targetToHandlersByName;
	private final InvocationHandlerFactory factory;

	ReflectiveShackle(ParseHandlersByName targetToHandlersByName, InvocationHandlerFactory factory) {
		this.targetToHandlersByName = targetToHandlersByName;
		this.factory = factory;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T newInstance(Target<T> target) {
		Map<String, MethodHandler> nameToHandler = targetToHandlersByName.apply(target);
		Map<Method, MethodHandler> methodToHandler = new LinkedHashMap<Method, MethodHandler>();
		List<DefaultMethodHandler> defaultMethodHandlers = new LinkedList<DefaultMethodHandler>();

		for (Method method : target.type().getMethods()) {
			if (method.getDeclaringClass() == Object.class) {
				continue;
			} else if (Util.isDefault(method)) {
				DefaultMethodHandler handler = new DefaultMethodHandler(method);
				defaultMethodHandlers.add(handler);
				methodToHandler.put(method, handler);
			} else {
				methodToHandler.put(method, nameToHandler.get(Shackle.configKey(target.type(), method)));
			}
		}
		InvocationHandler handler = factory.create(target, methodToHandler);
		T proxy = (T) Proxy.newProxyInstance(target.type().getClassLoader(), new Class<?>[] { target.type() }, handler);

		for (DefaultMethodHandler defaultMethodHandler : defaultMethodHandlers) {
			defaultMethodHandler.bindTo(proxy);
		}
		
		return proxy;
	}

	static class ShackleInvocationHandler implements InvocationHandler {

		private final Target<?> target;
		private final Map<Method, MethodHandler> dispatch;

		ShackleInvocationHandler(Target<?> target, Map<Method, MethodHandler> dispatch) {
			this.target = Util.checkNotNull(target, "target");
			this.dispatch = Util.checkNotNull(dispatch, "dispatch for %s", target);
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			if ("equals".equals(method.getName())) {
				try {
					Object otherHandler = args.length > 0 && args[0] != null ? Proxy.getInvocationHandler(args[0])
							: null;
					return equals(otherHandler);
				} catch (IllegalArgumentException e) {
					return false;
				}
			} else if ("hashCode".equals(method.getName())) {
				return hashCode();
			} else if ("toString".equals(method.getName())) {
				return toString();
			}
			
			return dispatch.get(method).invoke(args);
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ShackleInvocationHandler) {
				ShackleInvocationHandler other = (ShackleInvocationHandler) obj;
				
				return target.equals(other.target);
			}
			return false;
		}

		@Override
		public int hashCode() {
			return target.hashCode();
		}

		@Override
		public String toString() {
			return target.toString();
		}
	}

	static final class ParseHandlersByName {

		private final Contract contract;
		private final SynchronousMethodHandler.Factory factory;

		ParseHandlersByName(Contract contract, SynchronousMethodHandler.Factory factory) {
			this.contract = contract;
			this.factory = factory;
		}

		public Map<String, MethodHandler> apply(Target<?> key) {
			List<MethodMetadata> metadata = contract.parseAndValidateMetadata(key.type());
			Map<String, MethodHandler> result = new LinkedHashMap<String, MethodHandler>();
			for (MethodMetadata md : metadata) {
				BuildTemplateByResolvingArgs buildTemplate = new BuildTemplateByResolvingArgs(md);

				result.put(md.configKey(), factory.create(key, md, buildTemplate));
			}
			return result;
		}
	}

	private static class BuildTemplateByResolvingArgs implements DomainTemplate.Factory {
		protected final MethodMetadata metadata;

		private BuildTemplateByResolvingArgs(MethodMetadata metadata) {
			this.metadata = metadata;
		}

		@Override
		public DomainTemplate create(Object[] argv) {
			return new DomainTemplate(metadata.template());
		}
	}

}
