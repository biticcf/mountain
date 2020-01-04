/**
 * 
 */
package com.github.biticcf.mountain.shackle;

import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

import com.github.biticcf.mountain.core.common.lang.Logable;
import com.github.biticcf.mountain.core.common.result.CallResult;
import com.github.biticcf.mountain.core.common.result.WdCallbackResult;
import com.github.biticcf.mountain.core.common.service.ReferContext;
import com.github.biticcf.mountain.core.common.service.WdServiceCallback;
import com.github.biticcf.mountain.core.common.service.WdServiceTemplate;

/**
 * author: Daniel.Cao
 * date: 2019年1月3日
 * time: 下午11:09:16
 *
 */
public interface DomainExecutor {

	CallResult<?> execute(Domain domain, Object[] args, Class<?>[] argTypes) throws Exception;
	
	default <T> CallResult<T> makeResult(WdCallbackResult<T> result) {
		return (CallResult<T>) CallResult.makeCallResult(result.isSuccess(),
				result.getResultCode(), result.getResultMessage(), result.getBusinessObject(), result.getThrowable());
	}

	public static class Default implements DomainExecutor, Logable {
		private static Log logger = LogFactory.getLog(Default.class);

		private final WdServiceTemplate wdServiceTemplate;
		private final ReferContext referContext;
		private final ApplicationContext applicationContext;

		/**
		 * 
		 * @param wdServiceTemplate WdServiceTemplate
		 * @param referContext ReferContext
		 * @param applicationContext ApplicationContext
		 */
		public Default(WdServiceTemplate wdServiceTemplate, ReferContext referContext, ApplicationContext applicationContext) {
			this.wdServiceTemplate = wdServiceTemplate;
			this.referContext = referContext;
			this.applicationContext = applicationContext;
		}

		@Override
		public CallResult<?> execute(Domain domain, Object[] args, Class<?>[] argTypes) throws Exception {
			boolean withTrans = domain.withTrans();
			Class<? extends WdServiceCallback<?>> callbackClz = domain.callback();
			// 处理wdServiceTemplate
			String wdServiceTemplateBeanName = domain.wdServiceTemplateBeanName();
			WdServiceTemplate wdServiceTemplateMethod = getByNameOptional(wdServiceTemplateBeanName, WdServiceTemplate.class);
			WdServiceTemplate wdServiceTemplateUsed = this.wdServiceTemplate;
			if (wdServiceTemplateMethod != null) {
				this.writeInfoLog(logger, "Defined WdServiceTemplate On Method Instead Of In Configuration.");
				
				wdServiceTemplateUsed = wdServiceTemplateMethod;
			}
			if (wdServiceTemplateUsed == null) {
				throw new NoSuchBeanDefinitionException("WdServiceTemplate");
			}
			
			// 处理构造方法的参数
			int argv = 0;
			if (args != null && args.length > 0) {
				argv = args.length;
			}
			Object[] argsNew = new Object[argv + 1];
			Class<?>[] parameterTypes = new Class<?>[argv + 1];
			argsNew[0] = this.referContext;
			parameterTypes[0] = this.referContext.getClass();
			for (int i = 1; i < argsNew.length; i++) {
				argsNew[i] = args[i - 1];
				parameterTypes[i] = argTypes[i - 1];
			}
			// 处理bean of domain(使用by name)
			String domainName = domain.domainName();
			if (domainName == null || domainName.trim().equals("")) {
				String simpleName = callbackClz.getSimpleName();
				domainName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
			}
			WdServiceCallback<?> callback = getByNameAndArgs(domainName, argsNew);
			// 如果通过动态代理无法找到实例,则新生成一个实例
			if (callback == null) {
				this.writeWarnLog(logger, "Domain bean[" + callbackClz + "] Not found, Create a new Instance.");
				
	            Constructor<? extends WdServiceCallback<?>> ct = null;
	            try {
	                ct = callbackClz.getConstructor(parameterTypes);
	            } catch (Exception e) {
	                ct = null;
	            }
	            if (ct == null) {
	            	this.writeErrorLog(logger, "Domain bean[" + callbackClz + "] Not found, Create new Instance Fail, Exit!");
	            	
	                throw new NoSuchMethodException("No Suitable Public Constructor!");
	            }
	            callback = ct.newInstance(argsNew);
			}
			if (callback == null) {
				throw new NoSuchBeanDefinitionException("WdServiceCallback", "With Args[" + argsNew + "].");
			}
			
			// 执行业务逻辑
			WdCallbackResult<?> result = null;
			if (!withTrans) {
				result = wdServiceTemplateUsed.executeWithoutTransaction(callback, null);
			} else {
				result = wdServiceTemplateUsed.execute(callback, null);
			}
			
			return makeResult(result);
		}
		
		/**
		 * 根据名称查找bean,不存在返回null
		 * @param domain domain
		 * @param beanName beanName
		 * @param tClass tClass
		 * @param <T> 对象泛型
		 * @return 对象实例
		 */
		@SuppressWarnings("unchecked")
		private <T> T getByNameOptional(String beanName, Class<T> tClass) {
			if (beanName == null || beanName.trim().equals("")) {
				return null;
			}
			beanName = beanName.trim();
			
			Object beanObj = null;
			try {
				beanObj = applicationContext.getBean(beanName);
			} catch (NoSuchBeanDefinitionException e) {
				;
			}
			
			if (beanObj == null) {
				return null;
			}
			
			if (!tClass.isAssignableFrom(beanObj.getClass())) {
				throw new NoSuchBeanDefinitionException(beanName);
			}
			
			return (T) beanObj;
		}
		
		/**
		 * 根据beanName和构造方法的参数查找bean
		 * 不存在抛出NoSuchBeanDefinitionException异常
		 * @param domain domain
		 * @param args args
		 * @param <T> 对象泛型
		 * @return 对象实例
		 */
		@SuppressWarnings("unchecked")
		private <T> T getByNameAndArgs(String beanName, Object... args) {
			return (T) applicationContext.getBean(beanName, args);
		}
	}
}
