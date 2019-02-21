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
 * @Date:   2019年1月12日
 * @Time:   上午12:11:37
 *
 */
@JacksonXmlRootElement(localName = "facade", namespace = "facade")
class Facade {
	private String name; //非空,首字母必须大写
	private String description; //可空
	private Boolean execGenerator; //可空,默认true
	private Boolean useSwagger; //可空,默认false
	@JacksonXmlProperty(localName = "annotation")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "annotations")
	private List<String> annotations; //可空
	private RequestMapping requestMapping; //可空
	@JacksonXmlProperty(localName = "method")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "methods")
	private List<Method> methods; //非空
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getExecGenerator() {
		return execGenerator;
	}
	public void setExecGenerator(Boolean execGenerator) {
		this.execGenerator = execGenerator;
	}
	public Boolean getUseSwagger() {
		return useSwagger;
	}
	public void setUseSwagger(Boolean useSwagger) {
		this.useSwagger = useSwagger;
	}
	public List<String> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}
	public RequestMapping getRequestMapping() {
		return requestMapping;
	}
	public void setRequestMapping(RequestMapping requestMapping) {
		this.requestMapping = requestMapping;
	}
	public List<Method> getMethods() {
		return methods;
	}
	public void setMethods(List<Method> methods) {
		this.methods = methods;
	}
}
