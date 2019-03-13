/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.List;

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
	private Boolean reGenerator; //是否重新生成Facade文件,true重新生成,false不重新生成
	
	private Boolean daoCodeFlag; //是否生成DAO层代码，需要先定义{name}Po文件
	private Boolean defaultCreateFlag; //是否生成默认的创建接口(需要daoCodeFlag=true)
	private Boolean defaultDeleteFlag; //是否生成默认的删除接口(需要daoCodeFlag=true)
	private Boolean defaultUpdateFlag; //是否生成默认的更新接口(需要daoCodeFlag=true)
	private Boolean defaultQueryItemFlag; //是否生成默认的查询(按照id查询)接口(需要daoCodeFlag=true)
	private Boolean defaultQueryPagesFlag; //是否生成默认的查询(分页查询)接口(需要daoCodeFlag=true)
	
	private List<String> annotations; //可空
	private RequestMapping requestMapping; //可空
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
	public Boolean getReGenerator() {
		return reGenerator;
	}
	public void setReGenerator(Boolean reGenerator) {
		this.reGenerator = reGenerator;
	}
	public Boolean getDaoCodeFlag() {
		return daoCodeFlag;
	}
	public void setDaoCodeFlag(Boolean daoCodeFlag) {
		this.daoCodeFlag = daoCodeFlag;
	}
	public Boolean getDefaultCreateFlag() {
		return defaultCreateFlag;
	}
	public void setDefaultCreateFlag(Boolean defaultCreateFlag) {
		this.defaultCreateFlag = defaultCreateFlag;
	}
	public Boolean getDefaultDeleteFlag() {
		return defaultDeleteFlag;
	}
	public void setDefaultDeleteFlag(Boolean defaultDeleteFlag) {
		this.defaultDeleteFlag = defaultDeleteFlag;
	}
	public Boolean getDefaultUpdateFlag() {
		return defaultUpdateFlag;
	}
	public void setDefaultUpdateFlag(Boolean defaultUpdateFlag) {
		this.defaultUpdateFlag = defaultUpdateFlag;
	}
	public Boolean getDefaultQueryItemFlag() {
		return defaultQueryItemFlag;
	}
	public void setDefaultQueryItemFlag(Boolean defaultQueryItemFlag) {
		this.defaultQueryItemFlag = defaultQueryItemFlag;
	}
	public Boolean getDefaultQueryPagesFlag() {
		return defaultQueryPagesFlag;
	}
	public void setDefaultQueryPagesFlag(Boolean defaultQueryPagesFlag) {
		this.defaultQueryPagesFlag = defaultQueryPagesFlag;
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
