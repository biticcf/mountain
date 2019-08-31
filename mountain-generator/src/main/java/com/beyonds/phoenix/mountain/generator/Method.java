/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * author: Daniel.Cao
 * date:   2019年1月12日
 * time:   上午12:06:30
 *
 */
@JacksonXmlRootElement(localName = "method", namespace = "method")
class Method {
	private String name; //非空
	private String description; //可空
	private Boolean execGenerator; //可空,默认true
	private String  returnRealType; //返回值结果实际类型(不考虑ReturnResult的外层封装)
	private List<Param> params; //可空
	private Boolean listResultFlag; //可空,默认false
	private Boolean pagination; //可空,默认false
	private Boolean withTransaction; //可空,默认true
	private List<String> exceptions; //可空,默认Exception
	private RequestMapping requestMapping; //非空
	
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
	public String getReturnRealType() {
		return returnRealType;
	}
	public void setReturnRealType(String returnRealType) {
		this.returnRealType = returnRealType;
	}
	public List<Param> getParams() {
		return params;
	}
	public void setParams(List<Param> params) {
		this.params = params;
	}
	public Boolean getListResultFlag() {
		return listResultFlag;
	}
	public void setListResultFlag(Boolean listResultFlag) {
		this.listResultFlag = listResultFlag;
	}
	public Boolean getPagination() {
		return pagination;
	}
	public void setPagination(Boolean pagination) {
		this.pagination = pagination;
	}
	public Boolean getWithTransaction() {
		return withTransaction;
	}
	public void setWithTransaction(Boolean withTransaction) {
		this.withTransaction = withTransaction;
	}
	public List<String> getExceptions() {
		return exceptions;
	}
	public void setExceptions(List<String> exceptions) {
		this.exceptions = exceptions;
	}
	public RequestMapping getRequestMapping() {
		return requestMapping;
	}
	public void setRequestMapping(RequestMapping requestMapping) {
		this.requestMapping = requestMapping;
	}
}
