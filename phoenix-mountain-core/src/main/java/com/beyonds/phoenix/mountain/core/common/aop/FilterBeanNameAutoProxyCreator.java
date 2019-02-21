/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.aop;

import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.StringUtils;
/**
 * 
 * @Author: DanielCao
 * @Date:   2017年10月28日
 * @Time:   下午8:27:15
 *
 */
public class FilterBeanNameAutoProxyCreator extends BeanNameAutoProxyCreator {
	private static final long serialVersionUID = 7134809589522023021L;
	
	//需要过滤的类名
	private List<String> beanNamesExclude;
	
	/**
	 * 参数设置
	 * @param beanNamesExclude 不包含的名称列表
	 */
	public void setBeanNamesExclude(String... beanNamesExclude) {
		if (beanNamesExclude == null) {
			this.beanNamesExclude = new ArrayList<String>();
		} else {
			this.beanNamesExclude = new ArrayList<String>(beanNamesExclude.length);
			for (String mappedName : beanNamesExclude) {
				this.beanNamesExclude.add(StringUtils.trimWhitespace(mappedName));
			}
		}
	}
	
	/**
	 * 排除不需要代理的类
	 */
	@Override
	protected boolean shouldSkip(Class<?> beanClass, String beanName) {
		if (this.beanNamesExclude != null) {
			for (String mappedName : this.beanNamesExclude) {
				if (FactoryBean.class.isAssignableFrom(beanClass)) {
					if (!mappedName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
						continue;
					}
					mappedName = mappedName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
				}
				if (isMatch(beanName, mappedName)) {
					if (this.beanNamesExclude.contains(beanName)) {
						return true;
					}
				}
				BeanFactory beanFactory = getBeanFactory();
				if (beanFactory != null) {
					String[] aliases = beanFactory.getAliases(beanName);
					for (String alias : aliases) {
						if (isMatch(alias, mappedName)) {
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
}
