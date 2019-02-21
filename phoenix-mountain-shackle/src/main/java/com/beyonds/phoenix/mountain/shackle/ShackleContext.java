/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月3日
 * @Time:   下午7:14:16
 *
 */
public class ShackleContext extends NamedContextFactory<ShackleTemplateSpecification> {
	
	public ShackleContext() {
		super(ShackleTemplatesConfiguration.class, "shackle", "shackle.template.name");
	}
	
	public <T> T getInstance(String name, Class<T> type, Object... args) {
		AnnotationConfigApplicationContext context = getContext(name);
		if (BeanFactoryUtils.beanNamesForTypeIncludingAncestors(context, type).length > 0) {
			return context.getBean(type, args);
		}
		return null;
	}
	
	public Object getInstanceByName(String name, String beanName, Object... args) {
		AnnotationConfigApplicationContext context = getContext(name);
		
		return context.getBean(beanName, args);
	}
}
