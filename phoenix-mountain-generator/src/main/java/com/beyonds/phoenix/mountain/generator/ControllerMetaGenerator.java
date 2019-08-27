/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月13日
 * @Time:   上午11:45:05
 * +解析生成Facade对应的Controller实现文件
 */
class ControllerMetaGenerator extends GeneratorBase implements Generator {
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
			
			FileMeta facadeFileMeta = null;
			for (FileMeta _fileMeta : metaList) {
				if (_facade.getName().equals(_fileMeta.getPreffix())) {
					facadeFileMeta = _fileMeta;
					break;
				}
			}
			
			if (facadeFileMeta == null) {
				throw new Exception("Facade[" + _facade.getName() + "] Metadata Not Found Error!");
			}
			
			FileMeta fileMeta = new FileMeta();
			// importList
			List<String> importList = new ArrayList<>();
			// <javaName, javaPacakge>
			Map<String, String> javaNameMap = new HashMap<>();
			
			String facadeName = facadeFileMeta.getClassName();
			fileMeta.setClassType(2); //class
			fileMeta.setParentClass("BaseController");
			List<String> superInteefaceList = new ArrayList<String>();
			facadeName = getJavaNameAndImport(facadeFileMeta.getPackageName() + "." + facadeName, javaNameMap, importList);
			superInteefaceList.add(facadeName);
			fileMeta.setSuperInterfaceList(superInteefaceList);
			
			// className
			String className = _facade.getName() + "Controller";
			fileMeta.setClassName(className);
			
			// preffix
			String preffix = _facade.getName();
			fileMeta.setPreffix(preffix);
			
			// packageName
			String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_CONTROLLER);
			fileMeta.setPackageName(packageName);
			
			// genericName
			// null
			
			// headerAnnotationList
			List<String> headerAnnotationList = new ArrayList<>();
			// swagger
			Boolean useSwagger = _facade.getUseSwagger();
			if (useSwagger.booleanValue()) {
				dealSwaggerHeader(_facade, headerAnnotationList, importList, javaNameMap);
			}
			List<String> annotations = _facade.getAnnotations();
			if (annotations != null && !annotations.isEmpty()) {
				for (String _annotation : annotations) {
					String _annotationName = getJavaNameAndImport(_annotation, javaNameMap, importList);
					headerAnnotationList.add("@" + _annotationName);
				}
			}
			String restController = "org.springframework.web.bind.annotation.RestController";
			String restControllerName = this.getJavaNameAndImport(restController, javaNameMap, importList);
			headerAnnotationList.add("@" + restControllerName);
			fileMeta.setHeaderAnnotationList(headerAnnotationList);
			
			// memberList
			List<String> memberList = new ArrayList<>();
			String serviceName = fileMeta.getPreffix() + "Service";
			String servicePackage = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_SERVICE);
			String serviceName1 = getJavaNameAndImport(servicePackage + "." + serviceName, javaNameMap, importList);
			String autowired = "org.springframework.beans.factory.annotation.Autowired";
			String autowiredName = getJavaNameAndImport(autowired, javaNameMap, importList);
			String member = serviceName1 + " " + makePropertyName(serviceName);
			memberList.add("@" + autowiredName);
			memberList.add(member);
			fileMeta.setMemberList(memberList);
			
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
					project.getProperties(), _facade, javaNameMap, facadeFileMeta, type);
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
			FileMeta facadeFileMeta, Integer type) throws Exception {
		for (Method _method : defineMethodList) {
			// 忽略生成(该功能暂未实现)
			Boolean execGenerator = _method.getExecGenerator();
			execGenerator = true;
			if (!execGenerator.booleanValue()) {
				continue;
			}
			
			MethodMeta _facadeMethodMeta = null;
			for (MethodMeta _mm : facadeFileMeta.getMethodList()) {
				if (_method.getName().equals(_mm.getMethodName())) {
					_facadeMethodMeta = _mm;
					break;
				}
			}
			if (_facadeMethodMeta == null) {
				throw new Exception("Facade[" + facadeFileMeta.getClassName() + "." + _method.getName() + "] Method Metadata Not Found Error!");
			}
			
			MethodMeta _methodMeta = new MethodMeta();
			
			// methodName
			String methodName = _method.getName();
			_methodMeta.setMethodName(methodName);
			
			// returnType(涉及到import)
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
			_methodMeta.setWithTransaction(_facadeMethodMeta.isWithTransaction());
			
			// listType返回结果集合类型,0-非集合,1-list集合,2-PaginationSupport集合
			_methodMeta.setListType(_facadeMethodMeta.getListType());
			
			// annotationList
			List<String> annotationList = new ArrayList<>();
			// swagger
			Boolean useSwagger = facade.getUseSwagger();
			if (useSwagger.booleanValue()) {
				dealSwaggerMethod(_method, facade, annotationList, importList, javaNameMap);
			}
			//String override = "java.lang.Override";
			annotationList.add("@Override");
			_methodMeta.setAnnotationList(annotationList);
			
			// contentList
			// null
			
			// parameterList
			List<String> parameterList = new ArrayList<>();
			List<Param> params = _method.getParams();
			if (params != null && !params.isEmpty()) {
				for (int i = 0; i < params.size(); i ++) {
					Param param = params.get(i);
					
					String paramString = dealParam(param, _method, facade, importList, javaNameMap);
					parameterList.add(paramString);
				}
			}
			_methodMeta.setParameterList(parameterList);
			
			// exceptionList
			List<String> exceptionList = _method.getExceptions();
			List<String> newExceptionList = new ArrayList<>();
			if (exceptionList == null || exceptionList.isEmpty()) {
				newExceptionList.add("Exception");
			}
			for (String _exception : exceptionList) {
				String exName = getJavaNameAndImport(_exception, javaNameMap, importList);
				newExceptionList.add(exName);
			}
			_methodMeta.setExceptionList(newExceptionList);
			
			// bodyList
			List<String> bodyList = dealBodyList(_method, properties, facade, importList, javaNameMap, type);
			_methodMeta.setBodyList(bodyList);
			
			methodList.add(_methodMeta);
		}
	}
	
	private List<String> dealBodyList(Method method, Properties properties, Facade facade, 
			List<String> importList, Map<String, String> javaNameMap, Integer type) throws Exception {
		List<String> bodyList = new ArrayList<>();
		
		String mapType = "java.util.Map";
		String mapTypeName = getJavaNameAndImport(mapType, javaNameMap, importList);
		String mapHashType = "java.util.HashMap";
		String mapHashTypeName = getJavaNameAndImport(mapHashType, javaNameMap, importList);
		bodyList.add(mapTypeName + "<String, Object> _paramValueMap = new " + mapHashTypeName + "<>();");
		List<Param> paramList = method.getParams();
		String paramStr = "";
		if (paramList != null && !paramList.isEmpty()) {
			for (Param _param : paramList) {
				bodyList.add("_paramValueMap.put(\"" + _param.getName() + "\", " + _param.getName() + ");");
				
				paramStr += _param.getName() + ", ";
			}
			if (paramStr.endsWith(", ")) {
				paramStr = paramStr.substring(0, paramStr.length() - 2);
			}
		}
		bodyList.add("");
		
		String execType = "";
		Boolean listResultFlag = method.getListResultFlag();
		Boolean pagination = method.getPagination();
		execType = PLUGIN_BASE_DIR + ".core.common.result.";
		if (listResultFlag.booleanValue()) {
			if (pagination.booleanValue()) { // 分页查询
				execType += "ResultPaginationExecutor";
			} else {
				execType += "ResultListExecutor";
			}
		} else {
			execType += "ResultExecutor";
		}
		
		String execTypeName = getJavaNameAndImport(execType, javaNameMap, importList);
		String returnRealType = method.getReturnRealType();
		String returnRealTypeName = getJavaNameAndImport(returnRealType, javaNameMap, importList);
		String returnRealTypeName1 = returnRealTypeName;
		if (returnRealTypeName.startsWith("java.util.List<") || returnRealTypeName.startsWith("List<")) {
			int index1 = returnRealTypeName.indexOf("<");
			int index2 = returnRealTypeName.lastIndexOf(">");
			
			returnRealTypeName1 = returnRealTypeName.substring(index1 + 1, index2);
		}
		String callResultType = PLUGIN_BASE_DIR + ".core.common.result.CallResult";
		String callResultTypeName = getJavaNameAndImport(callResultType, javaNameMap, importList);
		if (listResultFlag.booleanValue()) {
			bodyList.add("return new " + execTypeName + "<" + returnRealTypeName1 + ", " + returnRealTypeName1 + ">() {");
			bodyList.add("    @Override");
			
			if (pagination.booleanValue()) {
				String paginationType = PLUGIN_BASE_DIR + ".core.common.util.PaginationSupport";
				String paginationTypeName = getJavaNameAndImport(paginationType, javaNameMap, importList);
				bodyList.add("    public " + callResultTypeName + "<" + paginationTypeName + "<" + returnRealTypeName1 + ">> execute() {");
			} else {
				bodyList.add("    public " + callResultTypeName + "<" + returnRealTypeName + "> execute() {");
			}
		} else {
			bodyList.add("return new " + execTypeName + "<" + returnRealTypeName + ", " + returnRealTypeName + ">() {");
			bodyList.add("    @Override");
			
			bodyList.add("    public " + callResultTypeName + "<" + returnRealTypeName + "> execute() {");
		}
		String serviceName = makePropertyName(facade.getName() + "Service");
		bodyList.add("        return " + serviceName + "." + method.getName() + "(" + paramStr + ");");
		bodyList.add("    }");
		String methodName = "GET";
		RequestMapping getRequestMapping = method.getRequestMapping();
		List<String> requestMethods = getRequestMapping.getRequestMethods();
		if (requestMethods != null && !requestMethods.isEmpty()) {
			methodName = requestMethods.get(0);
		}
		
		if (type != null && type.intValue() == 2) { //WebFlux
			bodyList.add("}.processReactorResult(\"" + facade.getName() + "Controller." + method.getName() + "\", \"" + methodName + "\", _paramValueMap, " + returnRealTypeName1 + ".class);");
		} else {
			bodyList.add("}.processResult(\"" + facade.getName() + "Controller." + method.getName() + "\", \"" + methodName + "\", _paramValueMap, " + returnRealTypeName1 + ".class);");
		}
		
		return bodyList;
	}
	
	private String dealParam(Param param, Method method, Facade facade, List<String> importList, Map<String, String> javaNameMap) throws Exception {
		String paramResult = "";
		
		// swagger
		Boolean useSwagger = facade.getUseSwagger();
		if (useSwagger.booleanValue()) {
			String strSwagger = dealSwaggerParam(param, method, facade, importList, javaNameMap);
			if (!isEmpty(strSwagger)) {
				paramResult += strSwagger;
			}
		}
		String name = param.getName();
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
	
	private void dealSwaggerHeader(Facade facade, List<String> annotationList, List<String> importList, Map<String, String> javaNameMap) throws Exception {
		String swaggerImport = "io.swagger.annotations.Api";
		
		String swaggerImportName = getJavaNameAndImport(swaggerImport, javaNameMap, importList);
		String value = facade.getDescription();
		value = isEmpty(value) ? facade.getName() + "Facade.java" : value;
		String swaggerAnnotation = "@" + swaggerImportName + "(value = \"" + value + "\", description = \"" + value + "\")";
		annotationList.add(swaggerAnnotation);
	}
	private void dealSwaggerMethod(Method method, Facade facade, List<String> annotationList, List<String> importList, Map<String, String> javaNameMap) throws Exception {
		String apiOperation = "io.swagger.annotations.ApiOperation";
		String apiOperationName = getJavaNameAndImport(apiOperation, javaNameMap, importList);
		String annoApiOperation = "@" + apiOperationName + "(value = \"" + method.getDescription() + "\"";
		RequestMapping requestMapping = method.getRequestMapping();
		List<String> requestMethods = requestMapping.getRequestMethods();
		if (requestMethods != null && !requestMethods.isEmpty()) {
			annoApiOperation += ", httpMethod = \"" + requestMethods.get(0) + "\"";
		}
		List<String> requestConsumes = requestMapping.getRequestConsumes();
		if (requestConsumes != null && !requestConsumes.isEmpty()) {
			annoApiOperation += ", consumes = \"" + requestConsumes.get(0) + "\"";
		}
		List<String> requestProduces = requestMapping.getRequestProduces();
		if (requestProduces != null && !requestProduces.isEmpty()) {
			annoApiOperation += ", produces = \"" + requestProduces.get(0) + "\"";
		}
		annoApiOperation += ", notes = \"" + method.getDescription() + "\")";
		annotationList.add(annoApiOperation);
		
		List<Param> paramList = method.getParams();
		if (paramList == null || paramList.isEmpty()) {
			return;
		}
		boolean postFlag = false;
		if (requestMethods != null && !requestMethods.isEmpty()) {
			for (String _method : requestMethods) {
				if ("POST".equals(_method)) {
					postFlag = true;
					break;
				}
			}
		}
		
		String apiImplicitParams = "io.swagger.annotations.ApiImplicitParams";
		String apiImplicitParamsName = getJavaNameAndImport(apiImplicitParams, javaNameMap, importList);
		String apiImplicitParam = "io.swagger.annotations.ApiImplicitParam";
		String apiImplicitParamName = getJavaNameAndImport(apiImplicitParam, javaNameMap, importList);
		List<String> apiImplicitParamList = new ArrayList<>();
		for (Param _param : paramList) {
			if (_param == null) {
				continue;
			}
			// 文件流类型忽略swagger
			try {
				Class<?> multipartFile = Class.forName(_param.getType());
				if (multipartFile != null && MultipartFile.class.isAssignableFrom(multipartFile)) {
					continue;
				}
			} catch (Exception e) {
			}
			
			// RequestParam,PathVariable,RequestBody,RequestHeader
			String requestType = _param.getRequestType();
			String swaggerType = "";
			if ("RequestParam".equals(requestType)) {
				swaggerType = "query";
				if (postFlag) {
					swaggerType = "form";
				}
			} else if ("PathVariable".equals(requestType)) {
				swaggerType = "path";
			} else if ("RequestBody".equals(requestType)) {
				swaggerType = "body";
			} else if ("RequestHeader".equals(requestType)) {
				swaggerType = "header";
			}
			String annoApiImplicitParam = "@" + apiImplicitParamName + "(name = \"" + _param.getName() + "\"";
			if (!isEmpty(swaggerType)) {
				annoApiImplicitParam += ", paramType = \"" + swaggerType + "\"";
			}
			String _value = _param.getDescription();
			if (isEmpty(_value)) {
				_value = _param.getName();
			}
			annoApiImplicitParam += ", value = \"" + _value + "\"";
			Boolean nullable = _param.getNullable();
			annoApiImplicitParam += ", required = " + (!nullable.booleanValue());
			String defaultValue = _param.getDefaultValue();
			if (!isEmpty(defaultValue)) {
				annoApiImplicitParam += ", defaultValue = \"" + defaultValue + "\"";
			}
			annoApiImplicitParam += ", dataType = \"" + getJavaNameAndImport(_param.getType(), javaNameMap, importList) + "\")";
			
			apiImplicitParamList.add(annoApiImplicitParam);
		}
		if (apiImplicitParamList.isEmpty()) {
			return;
		}
		String annoApiImplicitParams = "@" + apiImplicitParamsName + "(value = {";
		annotationList.add(annoApiImplicitParams);
		for (int i = 0; i < apiImplicitParamList.size() - 1; i ++) {
			annotationList.add("    " + apiImplicitParamList.get(i) + ",");
		}
		annotationList.add("    " + apiImplicitParamList.get(apiImplicitParamList.size() - 1));
		annotationList.add("})");
	}
	private String dealSwaggerParam(Param param, Method method, Facade facade, List<String> importList, Map<String, String> javaNameMap) throws Exception {
		// TODO
		return null;
	}
}
