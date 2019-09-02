/**
 * 
 */
package com.github.biticcf.mountain.shackle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.biticcf.mountain.core.common.service.WdServiceCallback;

/**
 * author: Daniel.Cao
 * date: 2019年1月3日
 * time: 下午10:37:19
 *
 */
public interface Contract {
	/**
	 * 
	 * @param targetType type
	 * @return list
	 */
	List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType);

	abstract class BaseContract implements Contract {

		@Override
		public List<MethodMetadata> parseAndValidateMetadata(Class<?> targetType) {
			Util.checkState(targetType.getTypeParameters().length == 0, "Parameterized types unsupported: %s",
					targetType.getSimpleName());
			Util.checkState(targetType.getInterfaces().length <= 1, "Only single inheritance supported: %s",
					targetType.getSimpleName());
			if (targetType.getInterfaces().length == 1) {
				Util.checkState(targetType.getInterfaces()[0].getInterfaces().length == 0,
						"Only single-level inheritance supported: %s", targetType.getSimpleName());
			}
			Map<String, MethodMetadata> result = new LinkedHashMap<String, MethodMetadata>();
			for (Method method : targetType.getMethods()) {
				if (method.getDeclaringClass() == Object.class || (method.getModifiers() & Modifier.STATIC) != 0
						|| Util.isDefault(method)) {
					continue;
				}
				MethodMetadata metadata = parseAndValidateMetadata(targetType, method);
				Util.checkState(!result.containsKey(metadata.configKey()), "Overrides unsupported: %s",
						metadata.configKey());
				result.put(metadata.configKey(), metadata);
			}
			return new ArrayList<MethodMetadata>(result.values());
		}

		/**
		 * 
		 * @param targetType type
		 * @param method method
		 * @return MethodMetadata
		 */
		protected MethodMetadata parseAndValidateMetadata(Class<?> targetType, Method method) {
			MethodMetadata data = new MethodMetadata();
			data.returnType(Types.resolve(targetType, targetType, method.getGenericReturnType()));
			data.configKey(Shackle.configKey(targetType, method));

			if (targetType.getInterfaces().length == 1) {
				processAnnotationOnClass(data, targetType.getInterfaces()[0]);
			}
			processAnnotationOnClass(data, targetType);

			for (Annotation methodAnnotation : method.getAnnotations()) {
				processAnnotationOnMethod(data, methodAnnotation, method);
			}

			return data;
		}

		/**
		 * Called by parseAndValidateMetadata twice, first on the declaring class, then
		 * on the target type (unless they are the same).
		 *
		 * @param data metadata collected so far relating to the current java method.
		 * @param clz  the class to process
		 * 
		 */
		protected abstract void processAnnotationOnClass(MethodMetadata data, Class<?> clz);

		/**
		 * @param data       metadata collected so far relating to the current java
		 *                   method.
		 * @param annotation annotations present on the current method annotation.
		 * @param method     method currently being processed.
		 */
		protected abstract void processAnnotationOnMethod(MethodMetadata data, Annotation annotation, Method method);
	}

	class Default extends BaseContract {
		@Override
		protected void processAnnotationOnClass(MethodMetadata data, Class<?> targetType) {
			//
		}
		
		@Override
		protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
			Class<? extends Annotation> annotationType = methodAnnotation.annotationType();
			if (annotationType == ShackleDomain.class) {
				String shackleDomain = ShackleDomain.class.cast(methodAnnotation).value();
				Util.checkState(Util.emptyToNull(shackleDomain) != null,
						"ShackleDomain annotation was empty on method %s.", method.getName());
				
				ShackleDomain domain = (ShackleDomain) methodAnnotation;
				Class<? extends WdServiceCallback<?>> callback = domain.domain();
				String wdServiceTemplateBeanName = domain.wdServiceTemplateBeanName();
				String domainName = domain.domainName();
				
				DomainTemplate domainTemplate = new DomainTemplate(callback, domain.withTrans(), wdServiceTemplateBeanName, domainName);
				
				data.template(domainTemplate);
				
				data.argTypes(method.getParameterTypes());
			}
		}
	}
}
