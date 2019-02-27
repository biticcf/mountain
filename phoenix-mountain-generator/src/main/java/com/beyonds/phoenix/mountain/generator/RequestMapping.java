/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.List;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月12日
 * @Time:   上午12:01:02
 *
 */
class RequestMapping {
	private List<String> requestPaths; //非空
	private List<String> requestMethods; //可空,默认是GET
	private List<String> requestConsumes; //可空
	private List<String> requestProduces; //可空
	private List<String> requestHeaders; //可空
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
