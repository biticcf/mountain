/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月11日
 * @Time:   下午11:53:41
 *
 */
@JacksonXmlRootElement(localName = "model", namespace = "model")
class Model {
	private String name; //非空
	@JacksonXmlProperty(localName = "dirRef")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "includeDirs")
	private List<String> includeDirs; //非空
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getIncludeDirs() {
		return includeDirs;
	}
	public void setIncludeDirs(List<String> includeDirs) {
		this.includeDirs = includeDirs;
	}
}
