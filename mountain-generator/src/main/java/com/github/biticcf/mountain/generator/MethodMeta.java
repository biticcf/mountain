/**
 * 
 */
package com.github.biticcf.mountain.generator;

import java.util.List;

/**
 * author: Daniel.Cao
 * date:   2019年1月9日
 * time:   下午9:41:00
 * +方法元文件
 */
class MethodMeta {
	private String methodName; //方法名称
	private String returnType; //返回类型
	private String genericReturnType; //返回类型泛型
	
	private boolean withTransaction; //是否支持事务
	private int     listType; // 返回结果集合类型,0-非集合,1-list集合,2-PaginationSupport集合
	
	private List<String> annotationList; //方法上的注解列表
	private List<String> parameterList; //参数列表
	private List<String> exceptionList; //抛出异常列表
	private List<String> contentList; //注释列表,每个值一行代码
	
	private List<String> bodyList; //方法体,每个值一行代码

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getGenericReturnType() {
		return genericReturnType;
	}

	public void setGenericReturnType(String genericReturnType) {
		this.genericReturnType = genericReturnType;
	}

	public boolean isWithTransaction() {
		return withTransaction;
	}

	public void setWithTransaction(boolean withTransaction) {
		this.withTransaction = withTransaction;
	}
	
	public int getListType() {
		return listType;
	}

	public void setListType(int listType) {
		this.listType = listType;
	}

	public List<String> getAnnotationList() {
		return annotationList;
	}

	public void setAnnotationList(List<String> annotationList) {
		this.annotationList = annotationList;
	}

	public List<String> getParameterList() {
		return parameterList;
	}

	public void setParameterList(List<String> parameterList) {
		this.parameterList = parameterList;
	}

	public List<String> getExceptionList() {
		return exceptionList;
	}

	public void setExceptionList(List<String> exceptionList) {
		this.exceptionList = exceptionList;
	}

	public List<String> getContentList() {
		return contentList;
	}

	public void setContentList(List<String> contentList) {
		this.contentList = contentList;
	}
	
	public List<String> getBodyList() {
		return bodyList;
	}

	public void setBodyList(List<String> bodyList) {
		this.bodyList = bodyList;
	}
}
