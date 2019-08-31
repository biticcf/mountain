/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.List;

/**
 * author: Daniel.Cao
 * date:   2019年1月9日
 * time:   下午9:35:30
 * +类元文件
 */
class FileMeta {
	private String className; //类名
	private String preffix; //类名前缀
	private String packageName; //所属包名
	private int    classType; //类的类型,1-接口,2-类,3-抽象类
	
	private String genericName; //泛型变量名称
	
	private String parentClass; //上级父类
	private List<String> superInterfaceList; //上级接口列表
	
	private List<String> importList; //导入包列表
	private List<String> headerAnnotationList; //文件头注解列表
	private List<String> memberList; // 类成员列表
	private List<MethodMeta> methodList; //方法列表
	
	private List<String> headerContentList; //文件头注释列表,每个值一行代码
	private List<String> classContentList; //类注释列表,每个值一行代码
	
	private boolean reGenerator = true; //是否重新生成文件
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getPreffix() {
		return preffix;
	}
	public void setPreffix(String preffix) {
		this.preffix = preffix;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public int getClassType() {
		return classType;
	}
	public void setClassType(int classType) {
		this.classType = classType;
	}
	public String getGenericName() {
		return genericName;
	}
	public void setGenericName(String genericName) {
		this.genericName = genericName;
	}
	public String getParentClass() {
		return parentClass;
	}
	public void setParentClass(String parentClass) {
		this.parentClass = parentClass;
	}
	public List<String> getSuperInterfaceList() {
		return superInterfaceList;
	}
	public void setSuperInterfaceList(List<String> superInterfaceList) {
		this.superInterfaceList = superInterfaceList;
	}
	public List<String> getImportList() {
		return importList;
	}
	public void setImportList(List<String> importList) {
		this.importList = importList;
	}
	public List<String> getHeaderAnnotationList() {
		return headerAnnotationList;
	}
	public void setHeaderAnnotationList(List<String> headerAnnotationList) {
		this.headerAnnotationList = headerAnnotationList;
	}
	public List<String> getMemberList() {
		return memberList;
	}
	public void setMemberList(List<String> memberList) {
		this.memberList = memberList;
	}
	public List<MethodMeta> getMethodList() {
		return methodList;
	}
	public void setMethodList(List<MethodMeta> methodList) {
		this.methodList = methodList;
	}
	public List<String> getHeaderContentList() {
		return headerContentList;
	}
	public void setHeaderContentList(List<String> headerContentList) {
		this.headerContentList = headerContentList;
	}
	public List<String> getClassContentList() {
		return classContentList;
	}
	public void setClassContentList(List<String> classContentList) {
		this.classContentList = classContentList;
	}
	public boolean isReGenerator() {
		return reGenerator;
	}
	public void setReGenerator(boolean reGenerator) {
		this.reGenerator = reGenerator;
	}
}
