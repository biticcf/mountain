/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月13日
 * @Time:   上午11:45:05
 * +解析生成Facade接口定义文件
 */
class FacadeMetaGenerator extends GeneratorBase implements Generator {
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
			
			FileMeta fileMeta = new FileMeta();
			
			fileMeta.setClassType(1); //接口
			
			// className
			String className = _facade.getName() + "Facade";
			fileMeta.setClassName(className);
			
			// preffix
			String preffix = _facade.getName();
			fileMeta.setPreffix(preffix);
			
			// packageName
			String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_FACADE);
			fileMeta.setPackageName(packageName);
			
			// genericName
			// null
			
			// importList
			List<String> importList = new ArrayList<>();
			// <javaName, javaPacakge>
			Map<String, String> javaNameMap = new HashMap<>();
			
			// headerAnnotationList
			List<String> headerAnnotationList = new ArrayList<>();
			List<String> annotations = _facade.getAnnotations();
			if (annotations != null && !annotations.isEmpty()) {
				for (String _annotation : annotations) {
					String _annotationName = getJavaNameAndImport(_annotation, javaNameMap, importList);
					headerAnnotationList.add("@" + _annotationName);
				}
			}
			//添加RequestMapping
			dealRequestMapping(_facade.getRequestMapping(), headerAnnotationList, importList, javaNameMap);
			fileMeta.setHeaderAnnotationList(headerAnnotationList);
			
			// memberList
			// null
			
			// headerContentList
			List<String> headerContentList = new ArrayList<>();
			headerContentList.add("/**");
			headerContentList.add(" * " + className + ".java");
			headerContentList.add(" */");
			fileMeta.setHeaderContentList(headerContentList);
			
			// classContentList
			List<String> classContentList = generatorClassContent(_facade.getDescription(), className);
			fileMeta.setClassContentList(classContentList);
			
			// methodList
			List<MethodMeta> methodList = new ArrayList<>();
			List<Method> defineMethodList = _facade.getMethods();
			generatorFacadeMethodsFileMeta(methodList, defineMethodList, importList, 
					project.getProperties(), _facade, javaNameMap, type);
			fileMeta.setMethodList(methodList);
			
			sortImportList(importList);
			fileMeta.setImportList(importList);
			fileMetaList.add(fileMeta);
		}
		
		return fileMetaList;
	}
	
	//生成Facade MethodList元数据文件
	private void generatorFacadeMethodsFileMeta(List<MethodMeta> methodList, List<Method> defineMethodList,
			List<String> importList, Properties properties, Facade facade, Map<String, String> javaNameMap,
			Integer type) throws Exception {
		for (Method _method : defineMethodList) {
			// 忽略生成(该功能暂未实现)
			Boolean execGenerator = _method.getExecGenerator();
			execGenerator = true;
			if (!execGenerator.booleanValue()) {
				continue;
			}
			
			MethodMeta _methodMeta = new MethodMeta();
			
			// methodName
			String methodName = _method.getName();
			_methodMeta.setMethodName(methodName);
			
			// returnType
			String returnType = PLUGIN_BASE_DIR + ".core.common.result.ReturnResult";
			String returnTypeName = getJavaNameAndImport(returnType, javaNameMap, importList);
			String newReturnTypeName = returnTypeName;
			// genericReturnType
			String  returnRealType = _method.getReturnRealType();
			String genericReturnType = getJavaNameAndImport(returnRealType, javaNameMap, importList);
			if (isEmpty(genericReturnType)) {
				newReturnTypeName = returnTypeName;
			} else {
				newReturnTypeName = returnTypeName + "<" + genericReturnType + ">";
			}
			if (type != null && type.intValue() == 2) {
				String monoType = "reactor.core.publisher.Mono";
				String monoName = getJavaNameAndImport(monoType, javaNameMap, importList);
				
				newReturnTypeName = monoName + "<" + newReturnTypeName + ">";
			}
			_methodMeta.setReturnType(newReturnTypeName);
			_methodMeta.setGenericReturnType(genericReturnType);
			
			// withTransaction
			boolean withTransaction = _method.getWithTransaction();
			_methodMeta.setWithTransaction(withTransaction);
			
			// listType返回结果集合类型,0-非集合,1-list集合,2-PaginationSupport集合
			int listType = 0;
			Boolean listResultFlag = _method.getListResultFlag();
			Boolean pagination = _method.getPagination();
			if (listResultFlag.booleanValue()) {
				if (pagination.booleanValue()) {
					listType = 2;
				} else {
					listType = 1;
				}
			} else {
				listType = 0;
			}
			_methodMeta.setListType(listType);
			
			// annotationList
			// request
			RequestMapping requestMapping = _method.getRequestMapping();
			List<String> annotationList = new ArrayList<>();
			dealRequestMapping(requestMapping, annotationList, importList, javaNameMap);
			_methodMeta.setAnnotationList(annotationList);
			
			// contentList
			List<String> contentList = new ArrayList<>();
			contentList.add("/**");
			String description = _method.getDescription();
			if (isEmpty(description)) {
				description = _method.getName();
			}
			contentList.add(" * +" + description);
			
			// parameterList
			List<String> parameterList = new ArrayList<>();
			List<Param> params = _method.getParams();
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i ++) {
					Param param = params.get(i);
					String desc = param.getDescription();
					if (isEmpty(desc)) {
						desc = "添加说明";
					}
					contentList.add(" * @param " + param.getName() + " " + desc);
					
					String paramString = dealParam(param, _method, facade, importList, javaNameMap);
					parameterList.add(paramString);
				}
			}
			_methodMeta.setParameterList(parameterList);
			
			contentList.add(" * ");
			contentList.add(" * @return 添加说明");
			
			// exceptionList
			List<String> exceptionList = _method.getExceptions();
			List<String> newExceptionList = new ArrayList<>();
			if (exceptionList == null || exceptionList.isEmpty()) {
				newExceptionList.add("Exception");
			}
			for (String _exception : exceptionList) {
				String exName = getJavaNameAndImport(_exception, javaNameMap, importList);
				newExceptionList.add(exName);
				
				contentList.add(" * @throws " + exName + " 添加说明");
			}
			_methodMeta.setExceptionList(newExceptionList);
			
			contentList.add(" * ");
			contentList.add(" */");
			_methodMeta.setContentList(contentList);
			
			// bodyList
			// null
			
			methodList.add(_methodMeta);
		}
	}
	
	private String dealParam(Param param, Method method, Facade facade, List<String> importList, Map<String, String> javaNameMap) throws Exception {
		String paramResult = "";
		
		// requestType
		String requestType = param.getRequestType();
		if (isEmpty(requestType)) {
			requestType = "RequestParam";
		}
		String javaType = "org.springframework.web.bind.annotation." + requestType;
		getJavaNameAndImport(javaType, javaNameMap, importList);
		paramResult += "@" + requestType + "(";
		// 1,required
		Boolean nullable = param.getNullable();
		paramResult += "required = " + (!nullable.booleanValue());
		// 2,value
		String name = param.getName();
		if (!isEmpty(name) && !"RequestBody".equals(requestType)) { //RequestBody无value
			paramResult += ", value = \"" + name + "\"";
		}
		// 3,defaultValue
		String defaultValue = param.getDefaultValue();
		// PathVariable和RequestBody无defaultValue
		if (!isEmpty(defaultValue) && (!"PathVariable".equals(requestType) && !"RequestBody".equals(requestType))) {
			paramResult += ", defaultValue = \"" + defaultValue + "\"";
		}
		paramResult += ") ";
		// annotations
		List<String> annotations = param.getAnnotations();
		if (annotations != null && !annotations.isEmpty()) {
			for (String annotation : annotations) {
				String annotationName = getJavaNameAndImport(annotation, javaNameMap, importList);
				
				paramResult += "@" + annotationName + " ";
			}
		}
		String type = param.getType();
		String typeName = getJavaNameAndImport(type, javaNameMap, importList);
		paramResult += typeName + " " + name;
		
		return paramResult;
	}
	
	// 添加@RequestMapping到注解列表中
	private void dealRequestMapping(RequestMapping requestMapping, List<String> annotationList, 
			List<String> importList, Map<String, String> javaNameMap) throws Exception {
		if (requestMapping == null) {
			return;
		}
		
		String annotation = "org.springframework.web.bind.annotation.RequestMapping";
		String _annotationName = getJavaNameAndImport(annotation, javaNameMap, importList);
		String annoString = "@" + _annotationName + "(";
		// requestPaths
		List<String> requestPaths = requestMapping.getRequestPaths();
		if (requestPaths == null || requestPaths.isEmpty()) {
			annoString += "value = {\"\"}";
		} else {
			String _pathStr = "";
			for (String _path : requestPaths) {
				if (!_path.startsWith("/")) {
					_path = "/" + _path;
				}
				if (_path.endsWith("/")) {
					_path = _path.substring(0, _path.length() - 1);
				}
				_pathStr += "\"" + _path + "\", ";
			}
			if (_pathStr.endsWith(", ")) {
				_pathStr = _pathStr.substring(0, _pathStr.length() - 2);
			}
			annoString += "value = {" + _pathStr + "}";
		}
		// requestMethods
		List<String> requestMethods = requestMapping.getRequestMethods();
		if (requestMethods != null && !requestMethods.isEmpty()) {
			String _methods = "";
			String _methodPacakage = "org.springframework.web.bind.annotation.RequestMethod";
			getJavaNameAndImport(_methodPacakage, javaNameMap, importList);
			for (String _method : requestMethods) {
				if (isEmpty(_method)) {
					continue;
				}
				_methods += "RequestMethod." + _method + ", ";
			}
			if (_methods.endsWith(", ")) {
				_methods = _methods.substring(0, _methods.length() - 2);
			}
			if (!isEmpty(_methods)) {
				annoString += ", method = {" + _methods + "}";
			}
		}
		// requestConsumes
		List<String> requestConsumes = requestMapping.getRequestConsumes();
		if (requestConsumes != null && !requestConsumes.isEmpty()) {
			String _requestConsumes = "";
			for (String _requestConsume : requestConsumes) {
				if (isEmpty(_requestConsume)) {
					continue;
				}
				_requestConsumes += "\"" + _requestConsume + "\", ";
			}
			if (_requestConsumes.endsWith(", ")) {
				_requestConsumes = _requestConsumes.substring(0, _requestConsumes.length() - 2);
			}
			
			if (!isEmpty(_requestConsumes)) {
				annoString += ", consumes = {" + _requestConsumes + "}";
			}
		}
		// requestProduces
		List<String> requestProduces = requestMapping.getRequestProduces();
		if (requestProduces != null && !requestProduces.isEmpty()) {
			String _requestProduces = "";
			for (String _requestProduce : requestProduces) {
				if (isEmpty(_requestProduce)) {
					continue;
				}
				_requestProduces += "\"" + _requestProduce + "\", ";
			}
			if (_requestProduces.endsWith(", ")) {
				_requestProduces = _requestProduces.substring(0, _requestProduces.length() - 2);
			}
			
			if (!isEmpty(_requestProduces)) {
				annoString += ", produces = {" + _requestProduces + "}";
			}
		}
		// requestHeaders
		List<String> requestHeaders = requestMapping.getRequestHeaders();
		if (requestHeaders != null && !requestHeaders.isEmpty()) {
			String _requestHeaders = "";
			for (String _requestHeader : requestHeaders) {
				if (isEmpty(_requestHeader)) {
					continue;
				}
				_requestHeaders += "\"" + _requestHeader + "\", ";
			}
			if (_requestHeaders.endsWith(", ")) {
				_requestHeaders = _requestHeaders.substring(0, _requestHeaders.length() - 2);
			}
			
			if (!isEmpty(_requestHeaders)) {
				annoString += ", headers = {" + _requestHeaders + "}";
			}
		}
		// requestParams
		List<String> requestParams = requestMapping.getRequestParams();
		if (requestParams != null && !requestParams.isEmpty()) {
			String _requestParams = "";
			for (String _requestParam : requestParams) {
				if (isEmpty(_requestParam)) {
					continue;
				}
				_requestParams += "\"" + _requestParam + "\", ";
			}
			if (_requestParams.endsWith(", ")) {
				_requestParams = _requestParams.substring(0, _requestParams.length() - 2);
			}
			
			if (!isEmpty(_requestParams)) {
				annoString += ", params = {" + _requestParams + "}";
			}
		}
		annoString += ")";
		if (!annotationList.contains(annoString)) {
			annotationList.add(annoString);
		}
	}

}
