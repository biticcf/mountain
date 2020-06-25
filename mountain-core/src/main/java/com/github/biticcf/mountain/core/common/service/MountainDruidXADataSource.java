/**
 * @info:      MyDruidDataSource.java
 * @copyright: 2019
 */
package com.github.biticcf.mountain.core.common.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.pool.xa.DruidXADataSource;

/**
 * author: Daniel.Cao
 * date:   2019年10月12日
 * time:   下午5:18:07
 * + 加载自定义的druid的filter
 */
public class MountainDruidXADataSource extends DruidXADataSource implements ApplicationContextAware {
	private static final long serialVersionUID = 5392430401971221841L;

	private ApplicationContext applicationContext;
	
	private List<Filter> extFilters;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	/**
	 * +根据bean的name设置fliter
	 * @param filters bean的name
	 */
	public void setExtFilters(List<String> filters) {
		if (filters == null || filters.isEmpty()) {
			return;
		}
		
		this.extFilters = new ArrayList<>();
		// 加载新的filters
		for (String beanName : filters) {
			Filter filter = null;
			try {
				filter = applicationContext.getBean(beanName, Filter.class);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (filter != null) {
				extFilters.add(filter);
			}
		}
		if (!extFilters.isEmpty()) {
			setProxyFilters(extFilters);
		}
	}
	
	/**
	 * + 获取所有的extFilters
	 * @return extFilters
	 */
	public List<Filter> getExtFilters (){
		return this.extFilters;
	}
}
