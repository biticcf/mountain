/**
 * 
 */
package com.github.biticcf.mountain.shackle;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * author: Daniel.Cao
 * date: 2019年1月3日
 * time: 下午7:25:37
 *
 */
public final class MethodMetadata implements Serializable {
	private static final long serialVersionUID = 2058585297608161171L;

	private String configKey;
	private transient Type returnType;
	private transient Class<?>[] argTypes;
	private DomainTemplate template;
	
	MethodMetadata() {
	}

	/**
	 * Used as a reference to this method. 
	 *
	 * @see Shackle#configKey(Class, java.lang.reflect.Method)
	 * 
	 * @return configKey
	 */
	public String configKey() {
		return configKey;
	}

	public MethodMetadata configKey(String configKey) {
		this.configKey = configKey;
		
		return this;
	}
	
	public MethodMetadata argTypes(Class<?>[] argTypes) {
		 this.argTypes = argTypes;
		 
		 return this;
	}
	
	public Class<?>[] argTypes() {
		return argTypes;
	}

	public Type returnType() {
		return returnType;
	}

	public MethodMetadata returnType(Type returnType) {
		this.returnType = returnType;
		
		return this;
	}
	
	public DomainTemplate template() {
		return template;
	}
	
	public MethodMetadata template(DomainTemplate template) {
		this.template = template;
		
		return this;
	}
}
