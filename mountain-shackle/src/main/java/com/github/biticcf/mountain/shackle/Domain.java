/**
 * 
 */
package com.github.biticcf.mountain.shackle;

import com.github.biticcf.mountain.core.common.service.WdServiceCallback;

/**
 * author: Daniel.Cao
 * date:   2019年1月3日
 * time:   下午8:02:06
 *
 */
public final class Domain {
	private Class<? extends WdServiceCallback<?>> callback;
	private boolean withTrans;
	// 支持自定义事务模板注入
	private String wdServiceTemplateBeanName;
	private String domainName;
	
	public static Domain create(
			Class<? extends WdServiceCallback<?>> callback, 
			boolean withTrans,
			String wdServiceTemplateBeanName,
			String domainName) {
		return new Domain(callback, withTrans, wdServiceTemplateBeanName, domainName);
	}
	
	Domain(Class<? extends WdServiceCallback<?>> callback, boolean withTrans, 
			String wdServiceTemplateBeanName, String domainName) {
		this.callback = callback;
		this.withTrans = withTrans;
		this.wdServiceTemplateBeanName = wdServiceTemplateBeanName;
		this.domainName = domainName;
	}

	public Class<? extends WdServiceCallback<?>> callback() {
		return callback;
	}

	public boolean withTrans() {
		return withTrans;
	}
	
	public String wdServiceTemplateBeanName() {
		return wdServiceTemplateBeanName;
	}
	
	public String domainName() {
		return domainName;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
	    builder.append(callback).append(' ')
	           .append(withTrans).append(' ')
	           .append(wdServiceTemplateBeanName).append(' ')
	           .append(domainName);
	    
	    return builder.toString();
	  }
}
