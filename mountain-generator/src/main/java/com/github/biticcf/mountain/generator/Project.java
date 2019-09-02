/**
 * 
 */
package com.github.biticcf.mountain.generator;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * author: Daniel.Cao
 * date:   2019年1月12日
 * time:   上午12:15:57
 *
 */
@JacksonXmlRootElement(localName = "project", namespace = "project")
class Project {
	private Properties properties; //非空
	@JacksonXmlProperty(localName = "model")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "models")
	private List<Model> models; //非空
	@JacksonXmlProperty(localName = "facade")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "facades")
	private List<Facade> facades; //非空
	
	public Properties getProperties() {
		return properties;
	}
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	public List<Model> getModels() {
		return models;
	}
	public void setModels(List<Model> models) {
		this.models = models;
	}
	public List<Facade> getFacades() {
		return facades;
	}
	public void setFacades(List<Facade> facades) {
		this.facades = facades;
	}
}
