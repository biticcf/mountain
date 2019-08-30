/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.List;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月11日
 * @Time:   下午11:56:38
 *
 */
class Param {
	private String name; //非空
	private String type; //非空
	private String description; //可空
	private Boolean nullable; //可空,默认false
	private String defaultValue; //可空
	private String requestType; //默认是RequestParam,请求参数类型:RequestParam,PathVariable,RequestBody,RequestHeader
	private List<String> annotations; // 可空
	private String pathVar; //MatrixVariable中使用字段
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Boolean getNullable() {
		return nullable;
	}
	public void setNullable(Boolean nullable) {
		this.nullable = nullable;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getRequestType() {
		return requestType;
	}
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
	public List<String> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<String> annotations) {
		this.annotations = annotations;
	}
	public String getPathVar() {
		return pathVar;
	}
	public void setPathVar(String pathVar) {
		this.pathVar = pathVar;
	}
}
