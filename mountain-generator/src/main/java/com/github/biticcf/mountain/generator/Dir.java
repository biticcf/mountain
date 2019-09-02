/**
 * 
 */
package com.github.biticcf.mountain.generator;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * author: Daniel.Cao
 * date:   2019年1月11日
 * time:   下午11:37:33
 *
 */
@JacksonXmlRootElement(localName = "dir", namespace = "dir")
class Dir {
	private String name; //非空
	private String value; //非空
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
}
