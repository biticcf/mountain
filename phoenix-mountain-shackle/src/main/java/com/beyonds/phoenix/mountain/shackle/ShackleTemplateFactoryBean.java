/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.Assert;

import com.beyonds.phoenix.mountain.core.common.lang.Logable;
import com.beyonds.phoenix.mountain.core.common.service.ReferContext;
import com.beyonds.phoenix.mountain.core.common.service.WdServiceTemplate;


/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月3日
 * @Time:   下午7:05:13
 *
 */
class ShackleTemplateFactoryBean implements FactoryBean<Object>, 
                                            InitializingBean, 
                                            ApplicationContextAware,
                                            Logable {
	private static Logger logger = LoggerFactory.getLogger(ShackleTemplateFactoryBean.class);
	
	private Class<?> type;
	private String name;
	private ApplicationContext applicationContext;
	
	private String wdServiceTemplateBeanName;
	private String defaultServiceTemplateBeanName;
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.hasText(this.name, "Name must be set");
	}

	@Override
	public Object getObject() throws Exception {
		return getTarget();
	}
	
	@SuppressWarnings("unchecked")
	<T> T getTarget() {
		ShackleContext context = applicationContext.getBean(ShackleContext.class);
		Shackle.Builder builder = shackle(context);
		
		return (T) builder.target(new Target.HardCodedTarget<>(this.type, this.name));
	}
	
	protected <T> T getOptional(ShackleContext context, Class<T> type) {
		return context.getInstance(this.name, type);
	}
	
	protected Shackle.Builder shackle(ShackleContext context) {
		// @formatter:off
		DomainExecutor domainExecutor = getOptional(context, DomainExecutor.class);
		if (domainExecutor == null) {
			this.writeInfoLog(logger, "Configuration domainExecutor is null, Begin define domainExecutor ...");
			
			// 为了解决一个bean定义多个不同名称的实例,默认事务模板的名称是wdServiceTemplate
			if (defaultServiceTemplateBeanName == null || defaultServiceTemplateBeanName.trim().equals("")) {
				defaultServiceTemplateBeanName = "wdServiceTemplate";
			}
			WdServiceTemplate wdServiceTemplate = getByTypeOrName(WdServiceTemplate.class, defaultServiceTemplateBeanName);
			WdServiceTemplate wdServiceTemplateClass = getByNameOptional(wdServiceTemplateBeanName, WdServiceTemplate.class);
			WdServiceTemplate wdServiceTemplateUsed = wdServiceTemplate;
			if (wdServiceTemplateClass != null) {
				this.writeInfoLog(logger, "Defined WdServiceTemplate On Service Class Instead Of In Configuration.");
				
				wdServiceTemplateUsed = wdServiceTemplateClass;
			}
			
			ReferContext referContext = get(context, ReferContext.class);
			
			domainExecutor = new DomainExecutor.Default(wdServiceTemplateUsed, referContext, applicationContext);
		}
		Contract contract = (Contract.Default) get(context, Contract.class);
		Shackle.Builder builder = get(context, Shackle.Builder.class)
				// required values
				.contract(contract)
				.domainExecutor(domainExecutor);
		// @formatter:on

		configureShackle(context, builder);

		return builder;
	}
	
	protected void configureShackle(ShackleContext context, Shackle.Builder builder) {
		ShackleTemplateProperties properties = applicationContext.getBean(ShackleTemplateProperties.class);
		if (properties != null) {
			if (properties.isDefaultToProperties()) {
				configureUsingConfiguration(context, builder);
				configureUsingProperties(properties.getConfig().get(properties.getDefaultConfig()), builder);
				configureUsingProperties(properties.getConfig().get(this.name), builder);
			} else {
				configureUsingProperties(properties.getConfig().get(properties.getDefaultConfig()), builder);
				configureUsingProperties(properties.getConfig().get(this.name), builder);
				configureUsingConfiguration(context, builder);
			}
		} else {
			configureUsingConfiguration(context, builder);
		}
	}
	
	protected void configureUsingConfiguration(ShackleContext context, Shackle.Builder builder) {
		//
	}
	
	protected void configureUsingProperties(ShackleTemplateProperties.ShackleTemplateConfiguration config, Shackle.Builder builder) {
		if (config == null) {
			return;
		}
		
		if (Objects.nonNull(config.getContract())) {
			builder.contract(getOrInstantiate(config.getContract()));
		}
	}
	
	private <T> T getByTypeOrName(Class<T> tClass, String beanName) {
		try {
			return applicationContext.getBean(tClass);
		} catch (Exception e) {
			try {
				return applicationContext.getBean(beanName, tClass);
			} catch (Exception e1) {
				;
			}
		}
		
		return null;
	}
	
	private <T> T getOrInstantiate(Class<T> tClass) {
		try {
			return applicationContext.getBean(tClass);
		} catch (NoSuchBeanDefinitionException e) {
			return BeanUtils.instantiateClass(tClass);
		}
	}
	
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
	
	protected <T> T get(ShackleContext context, Class<T> type, Object... args) {
		T instance = context.getInstance(this.name, type, args);
		if (instance == null) {
			throw new IllegalStateException("No bean found of type " + type + " for " + this.name);
		}
		return instance;
	}
	
	@SuppressWarnings("unchecked")
	protected <T> T get(ShackleContext context, String beanName, Class<T> type) {
		Object instance = context.getInstanceByName(this.name, beanName);
		if (instance == null) {
			throw new IllegalStateException("No bean found of name " + beanName + " for " + this.name);
		}
		
		if (!instance.getClass().isAssignableFrom(type) ) {
			throw new IllegalStateException("The name " + beanName + " not found for " + this.name);
		}
		
		return (T) instance;
	}
	
	@Override
	public Class<?> getObjectType() {
		return this.type;
	}
	
	@Override
	public boolean isSingleton() {
		return true;
	}

	public Class<?> getType() {
		return type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public ApplicationContext getApplicationContext() {
		return applicationContext;
	}
	
	public String getWdServiceTemplateBeanName() {
		return wdServiceTemplateBeanName;
	}

	public void setWdServiceTemplateBeanName(String wdServiceTemplateBeanName) {
		this.wdServiceTemplateBeanName = wdServiceTemplateBeanName;
	}

	public String getDefaultServiceTemplateBeanName() {
		return defaultServiceTemplateBeanName;
	}

	public void setDefaultServiceTemplateBeanName(String defaultServiceTemplateBeanName) {
		this.defaultServiceTemplateBeanName = defaultServiceTemplateBeanName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShackleTemplateFactoryBean that = (ShackleTemplateFactoryBean) o;
		return Objects.equals(applicationContext, that.applicationContext) &&
			   Objects.equals(name, that.name) &&
			   Objects.equals(wdServiceTemplateBeanName, that.wdServiceTemplateBeanName) &&
			   Objects.equals(type, that.type) && 
			   Objects.equals(defaultServiceTemplateBeanName, that.defaultServiceTemplateBeanName);
	}

	@Override
	public int hashCode() {
		return Objects.hash(applicationContext, name, wdServiceTemplateBeanName, type, defaultServiceTemplateBeanName);
	}

	@Override
	public String toString() {
		return new StringBuilder("ShackleTemplateFactoryBean{")
				.append("type=").append(type).append(", ")
				.append("name='").append(name).append("', ")
				.append("wdServiceTemplateBeanName='").append(wdServiceTemplateBeanName).append("', ")
				.append("defaultServiceTemplateBeanName='").append(defaultServiceTemplateBeanName).append("', ")
				.append("applicationContext=").append(applicationContext)
				.append("}").toString();
	}
}
