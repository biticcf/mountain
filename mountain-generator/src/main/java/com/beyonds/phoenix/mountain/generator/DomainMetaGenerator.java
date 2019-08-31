/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * author: Daniel.Cao
 * date:   2019年1月13日
 * time:   上午11:45:05
 * +解析生成Domain接口定义文件
 */
class DomainMetaGenerator extends GeneratorBase implements Generator {
	// 生成接口定义文件元文件
	@Override
	public List<FileMeta> generatorFileMeta(Project project, List<FileMeta> metaList, Integer type) throws Exception {
		List<FileMeta> fileMetaList = new ArrayList<>();
		
		List<Facade> facades = project.getFacades();
		for (Facade _facade : facades) {
			// 忽略生成
			Boolean execGenerator = _facade.getExecGenerator();
			if (!execGenerator.booleanValue()) {
				continue;
			}
			
			List<Method> methodList = _facade.getMethods();
			if (methodList == null || methodList.isEmpty()) {
				throw new Exception("Facade[" + _facade.getName() + "] Method Metadata Not Found Error!");
			}
			
			for (Method _method : methodList) {
				if (_method == null) {
					continue;
				}
				
				FileMeta fileMeta = generatorDomainFileMeta(_method, _facade, project);
				
				fileMetaList.add(fileMeta);
			}
		}
		
		return fileMetaList;
	}
	
	private FileMeta generatorDomainFileMeta (Method method, Facade facade, Project project) throws Exception {
		FileMeta fileMeta = new FileMeta();
		// importList
		List<String> importList = new ArrayList<>();
		// <javaName, javaPacakge>
		Map<String, String> javaNameMap = new HashMap<>();
		
		fileMeta.setClassType(2); //类
		
		// className
		String className = facade.getName() + makePropertyName(method.getName()) + "Domain";
		fileMeta.setClassName(className);
		fileMeta.setParentClass("AbstractBaseDomain");
		
		// preffix
		String preffix = facade.getName();
		fileMeta.setPreffix(preffix);
		
		// packageName
		String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN);
		fileMeta.setPackageName(packageName);
		
		// genericName
		String returnRealType = method.getReturnRealType();
		String returnRealTypeName = getJavaNameAndImport(returnRealType, javaNameMap, importList);
		String returnRealTypeName1 = returnRealTypeName;
		if (returnRealTypeName.startsWith("java.util.List<") || returnRealTypeName.startsWith("List<")) {
			int index1 = returnRealTypeName.indexOf("<");
			int index2 = returnRealTypeName.lastIndexOf(">");
			
			returnRealTypeName1 = returnRealTypeName.substring(index1 + 1, index2);
		}
		if (method.getListResultFlag().booleanValue() && method.getPagination().booleanValue()) {
			String paginationType = PLUGIN_BASE_DIR + ".core.common.util.PaginationSupport";
			String paginationTypeName = getJavaNameAndImport(paginationType, javaNameMap, importList);
			
			returnRealTypeName = paginationTypeName + "<" + returnRealTypeName1 + ">";
		}
		fileMeta.setGenericName(returnRealTypeName);
		
		// headerAnnotationList
		// @Service
		List<String> headerAnnotationList = new ArrayList<>();
		String service = "org.springframework.stereotype.Service";
		String serviceName = this.getJavaNameAndImport(service, javaNameMap, importList);
		String domainBeanName = Character.toLowerCase(className.charAt(0)) + className.substring(1);
		serviceName += "(\"" + domainBeanName +"\")";
		headerAnnotationList.add("@" + serviceName);
		// @Scope
		String scope = "org.springframework.context.annotation.Scope";
		String scopeName = this.getJavaNameAndImport(scope, javaNameMap, importList);
		scopeName += "(\"prototype\")";
		headerAnnotationList.add("@" + scopeName);
		fileMeta.setHeaderAnnotationList(headerAnnotationList);
		
		// memberList
		List<String> memberList = new ArrayList<>();
		
		Properties properties = project.getProperties();
		String constantContextType = "com." + properties.getCompany() + "." + properties.getScope() + "." + properties.getTemplate() + ".domain.support.ConstantContext";
		String constantContextName = getJavaNameAndImport(constantContextType, javaNameMap, importList);
		String tmpName = constantContextName;
		if (constantContextName.indexOf(".") > 0) {
			int index = constantContextName.lastIndexOf(".");
			tmpName = constantContextName.substring(index + 1);
		}
		String member = "private final " + tmpName + " " + makePropertyName(tmpName);
		memberList.add(member);
		List<Param> paramList = method.getParams();
		if (paramList != null && !paramList.isEmpty()) {
			for (Param _param : paramList) {
				if (_param == null) {
					continue;
				}
				String paramType = _param.getType();
				String paramName = _param.getName();
				String paramTypeName = getJavaNameAndImport(paramType, javaNameMap, importList);
				
				String _member = "private final " + paramTypeName + " " + paramName;
				memberList.add(_member);
			}
		}
		fileMeta.setMemberList(memberList);
		
		// headerContentList
		List<String> headerContentList = new ArrayList<>();
		headerContentList.add("/**");
		headerContentList.add(" * " + className + ".java");
		headerContentList.add(" */");
		fileMeta.setHeaderContentList(headerContentList);
		
		// classContentList
		List<String> classContentList = generatorClassContent(method.getDescription(), method.getName());
		fileMeta.setClassContentList(classContentList);
		
		// methodList
		List<MethodMeta> methodList = generatorFacadeMethodsFileMeta(method, facade, project, importList, javaNameMap, fileMeta);
		fileMeta.setMethodList(methodList);
		
		sortImportList(importList);
		fileMeta.setImportList(importList);
		
		return fileMeta;
	}
	
	//生成Domain MethodList元数据文件
	private List<MethodMeta> generatorFacadeMethodsFileMeta(Method method, Facade facade, Project project, 
			List<String> importList, Map<String, String> javaNameMap, FileMeta domainMeta) throws Exception {
		List<MethodMeta> methodList = new ArrayList<>();
		
		// 构造方法
		MethodMeta conMethodMeta = makeConstractMethod(method, facade, project, domainMeta, importList, javaNameMap);
		methodList.add(conMethodMeta);
		
		// executeCheck
		MethodMeta checkMethodMeta = makeCheckMethod(method, facade, project, domainMeta, importList, javaNameMap);
		methodList.add(checkMethodMeta);
		
		// executeAction
		MethodMeta actionMethodMeta = makeActionMethod(method, facade, project, domainMeta, importList, javaNameMap);
		methodList.add(actionMethodMeta);
		
		// executeAfterSuccess
		MethodMeta afterSuccessMethodMeta = makeAfterSuccessMethod(method, facade, project, importList, javaNameMap);
		methodList.add(afterSuccessMethodMeta);
		
		// executeAfterFailure
		MethodMeta afterFailureMethodMeta = makeAfterFailureMethod(method, facade, project, importList, javaNameMap);
		methodList.add(afterFailureMethodMeta);
		
		return methodList;
	}
	
	private MethodMeta makeConstractMethod(Method method, Facade facade, Project project, FileMeta domainMeta,
			List<String> importList, Map<String, String> javaNameMap) throws Exception {
		MethodMeta methodMeta = new MethodMeta();
		
		methodMeta.setMethodName(domainMeta.getClassName());
		
		List<Param> paramList = method.getParams();
		List<String> parameterList = new ArrayList<>();
		parameterList.add("ConstantContext constantContext");
		List<String> bodyList = new ArrayList<>();
		bodyList.add("super(constantContext);");
		bodyList.add("");
		bodyList.add("this.constantContext = constantContext;");
		if (paramList != null && !paramList.isEmpty()) {
			for (Param _param : paramList) {
				String _pname = _param.getName();
				bodyList.add("this." + _pname + " = " + _pname + ";");
				
				String _ptype = _param.getType();
				String _ptypeName = this.getJavaNameAndImport(_ptype, javaNameMap, importList);
				parameterList.add(_ptypeName + " " + _pname);
			}
		}
		methodMeta.setBodyList(bodyList);
		methodMeta.setParameterList(parameterList);
		
		return methodMeta;
	}
	
	private MethodMeta makeCheckMethod(Method method, Facade facade, Project project, FileMeta domainMeta,
			List<String> importList, Map<String, String> javaNameMap) throws Exception {
		MethodMeta methodMeta = new MethodMeta();
		
		String wdCallbackResult = PLUGIN_BASE_DIR + ".core.common.result.WdCallbackResult";
		String wdCallbackResultName = getJavaNameAndImport(wdCallbackResult, javaNameMap, importList);
		String genericType = domainMeta.getGenericName();
		String returnType = wdCallbackResultName + "<" + genericType + ">";
		methodMeta.setReturnType(returnType);
		methodMeta.setMethodName("executeCheck");
		
		List<String> annotationList = new ArrayList<>();
		annotationList.add("@Override");
		methodMeta.setAnnotationList(annotationList);
		
		Properties properties = project.getProperties();
		String resultEnumType = "com." + properties.getCompany() + "." + properties.getScope() + "." + properties.getTemplate() + ".model.enums.ResultEnum";
		String resultEnumTypeName = getJavaNameAndImport(resultEnumType, javaNameMap, importList);
		List<String> bodyList = new ArrayList<>();
		bodyList.add("// 业务处理之前的参数校验，不受事务管理");
		bodyList.add("// TODO");
		bodyList.add("");
		bodyList.add("return " + wdCallbackResultName + ".success(" + resultEnumTypeName + ".SUCCESS.getCode());");
		methodMeta.setBodyList(bodyList);
		
		return methodMeta;
	}
	
	private MethodMeta makeActionMethod(Method method, Facade facade, Project project, FileMeta domainMeta, 
			List<String> importList, Map<String, String> javaNameMap) throws Exception {
		MethodMeta methodMeta = new MethodMeta();
		
		String wdCallbackResult = PLUGIN_BASE_DIR + ".core.common.result.WdCallbackResult";
		String wdCallbackResultName = getJavaNameAndImport(wdCallbackResult, javaNameMap, importList);
		String genericType = domainMeta.getGenericName();
		String returnType = wdCallbackResultName + "<" + genericType + ">";
		methodMeta.setReturnType(returnType);
		methodMeta.setMethodName("executeAction");
		
		List<String> annotationList = new ArrayList<>();
		annotationList.add("@Override");
		methodMeta.setAnnotationList(annotationList);
		
		Properties properties = project.getProperties();
		String resultEnumType = "com." + properties.getCompany() + "." + properties.getScope() + "." + properties.getTemplate() + ".model.enums.ResultEnum";
		String resultEnumTypeName = getJavaNameAndImport(resultEnumType, javaNameMap, importList);
		List<String> bodyList = new ArrayList<>();
		bodyList.add("// 核心业务逻辑处理代码段，如果有事务会受到事务管制");
		bodyList.add("// TODO");
		bodyList.add("");
		bodyList.add("return " + wdCallbackResultName + ".success(" + resultEnumTypeName + ".SUCCESS.getCode(), null);");
		methodMeta.setBodyList(bodyList);
		
		return methodMeta;
	}
	
	private MethodMeta makeAfterSuccessMethod(Method method, Facade facade, Project project, 
			List<String> importList, Map<String, String> javaNameMap) throws Exception {
		MethodMeta methodMeta = new MethodMeta();
		
		methodMeta.setReturnType("void");
		methodMeta.setMethodName("executeAfterSuccess");
		
		List<String> annotationList = new ArrayList<>();
		annotationList.add("@Override");
		methodMeta.setAnnotationList(annotationList);
		
		List<String> bodyList = new ArrayList<>();
		bodyList.add("// 业务成功处理完毕后的附加处理,比如更新缓存、通知第三方等，不受事务管理");
		bodyList.add("// TODO");
		methodMeta.setBodyList(bodyList);
		
		return methodMeta;
	}
	
	private MethodMeta makeAfterFailureMethod(Method method, Facade facade, Project project, 
			List<String> importList, Map<String, String> javaNameMap) throws Exception {
		MethodMeta methodMeta = new MethodMeta();
		
		methodMeta.setReturnType("void");
		methodMeta.setMethodName("executeAfterFailure");
		
		List<String> parameterList = new ArrayList<>();
		parameterList.add("Throwable e");
		methodMeta.setParameterList(parameterList);
		
		List<String> annotationList = new ArrayList<>();
		annotationList.add("@Override");
		methodMeta.setAnnotationList(annotationList);
		
		List<String> bodyList = new ArrayList<>();
		bodyList.add("// 业务处理失败后的附加处理");
		bodyList.add("// TODO");
		methodMeta.setBodyList(bodyList);
		
		return methodMeta;
	}
}
