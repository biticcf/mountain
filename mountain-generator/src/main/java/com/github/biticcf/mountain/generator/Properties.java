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
 * date:   2019年1月11日
 * time:   下午11:47:26
 *
 */
@JacksonXmlRootElement(localName = "properties", namespace = "properties")
class Properties {
	private String name; //非空
	private String company; //非空
	private String scope; //非空
	private String template; //非空
	private Boolean override; //可空
	@JacksonXmlProperty(localName = "dir")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "dirs")
	private List<Dir> dirs; //可空
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getTemplate() {
		return template;
	}
	public void setTemplate(String template) {
		this.template = template;
	}
	public Boolean getOverride() {
		return override;
	}
	public void setOverride(Boolean override) {
		this.override = override;
	}
	public List<Dir> getDirs() {
		return dirs;
	}
	public void setDirs(List<Dir> dirs) {
		this.dirs = dirs;
	}
}
