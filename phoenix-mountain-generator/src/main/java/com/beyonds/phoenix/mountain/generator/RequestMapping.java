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
 * @Time:   上午12:01:02
 *
 */
@JacksonXmlRootElement(localName = "requestMapping", namespace = "requestMapping")
class RequestMapping {
	@JacksonXmlProperty(localName = "requestPath")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "requestPaths")
	private List<String> requestPaths; //非空
	
	@JacksonXmlProperty(localName = "requestMethod")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "requestMethods")
	private List<String> requestMethods; //可空,默认是GET
	
	
	@JacksonXmlProperty(localName = "requestConsume")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "requestConsumes")
	private List<String> requestConsumes; //可空
	
	@JacksonXmlProperty(localName = "requestProduce")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "requestProduces")
	private List<String> requestProduces; //可空
	
	@JacksonXmlProperty(localName = "requestHeader")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "requestHeaders")
	private List<String> requestHeaders; //可空
	
	@JacksonXmlProperty(localName = "requestParam")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "requestParams")
	private List<String> requestParams; //可空

	public List<String> getRequestPaths() {
		return requestPaths;
	}

	public void setRequestPaths(List<String> requestPaths) {
		this.requestPaths = requestPaths;
	}

	public List<String> getRequestMethods() {
		return requestMethods;
	}

	public void setRequestMethods(List<String> requestMethods) {
		this.requestMethods = requestMethods;
	}

	public List<String> getRequestConsumes() {
		return requestConsumes;
	}

	public void setRequestConsumes(List<String> requestConsumes) {
		this.requestConsumes = requestConsumes;
	}

	public List<String> getRequestProduces() {
		return requestProduces;
	}

	public void setRequestProduces(List<String> requestProduces) {
		this.requestProduces = requestProduces;
	}

	public List<String> getRequestHeaders() {
		return requestHeaders;
	}

	public void setRequestHeaders(List<String> requestHeaders) {
		this.requestHeaders = requestHeaders;
	}

	public List<String> getRequestParams() {
		return requestParams;
	}

	public void setRequestParams(List<String> requestParams) {
		this.requestParams = requestParams;
	}
}
