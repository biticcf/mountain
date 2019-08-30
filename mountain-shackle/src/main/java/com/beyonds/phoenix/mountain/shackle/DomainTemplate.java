/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.io.Serializable;

import com.beyonds.phoenix.mountain.core.common.service.WdServiceCallback;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月3日
 * @Time:   下午7:58:34
 *
 */
public final class DomainTemplate implements Serializable {
	private static final long serialVersionUID = 7486102436678905156L;
	
	private Class<? extends WdServiceCallback<?>> callback;
	private boolean withTrans;
	private String wdServiceTemplateBeanName;
	private String domainName;
	
	public DomainTemplate(Class<? extends WdServiceCallback<?>> callback, boolean withTrans, 
			String wdServiceTemplateBeanName, String domainName) {
		this.callback = callback;
		this.withTrans = withTrans;
		this.wdServiceTemplateBeanName = wdServiceTemplateBeanName;
		this.domainName = domainName;
	}
	
	public DomainTemplate(DomainTemplate toCopy) {
	    this.callback = toCopy.callback;
	    this.withTrans = toCopy.withTrans;
	    this.wdServiceTemplateBeanName = toCopy.wdServiceTemplateBeanName;
	    this.domainName = toCopy.domainName;
	  }
	
	public Domain domain() {
		return Domain.create(callback, withTrans, wdServiceTemplateBeanName, domainName);
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
		return domain().toString();
	}
	
	interface Factory {

	    /**
	     * create a domain template using args passed to a method invocation.
	     */
		DomainTemplate create(Object[] argv);
	  }
}
