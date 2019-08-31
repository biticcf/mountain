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
 * +解析生成Service接口定义文件
 */
class ServiceMetaGenerator extends GeneratorBase implements Generator {
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
			String className = _facade.getName() + "Service";
			fileMeta.setClassName(className);
			
			// preffix
			String preffix = _facade.getName();
			fileMeta.setPreffix(preffix);
			
			// packageName
			String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_SERVICE);
			fileMeta.setPackageName(packageName);
			
			// genericName
			// null
			
			// importList
			List<String> importList = new ArrayList<>();
			// <javaName, javaPacakge>
			Map<String, String> javaNameMap = new HashMap<>();
			
			// headerAnnotationList
			List<String> headerAnnotationList = new ArrayList<>();
			String shackleTemplateType = PLUGIN_BASE_DIR + ".shackle.ShackleTemplate";
			String shackleTemplateTypeName = getJavaNameAndImport(shackleTemplateType, javaNameMap, importList);
			String annoShackleTemplate = "@" + shackleTemplateTypeName + "(value = \"" + makePropertyName(className) + "\")";
			headerAnnotationList.add(annoShackleTemplate);
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
			generatorFacadeMethodsFileMeta(methodList, defineMethodList, importList, project.getProperties(), _facade, javaNameMap);
			fileMeta.setMethodList(methodList);
			
			sortImportList(importList);
			fileMeta.setImportList(importList);
			fileMetaList.add(fileMeta);
		}
		
		return fileMetaList;
	}
	
	//生成Facade MethodList元数据文件
	private void generatorFacadeMethodsFileMeta(List<MethodMeta> methodList, List<Method> defineMethodList,
			List<String> importList, Properties properties, Facade facade, Map<String, String> javaNameMap) throws Exception {
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
			String returnType = PLUGIN_BASE_DIR + ".core.common.result.CallResult";
			String returnTypeName = getJavaNameAndImport(returnType, javaNameMap, importList);
			// genericReturnType
			String  returnRealType = _method.getReturnRealType();
			String genericReturnType = getJavaNameAndImport(returnRealType, javaNameMap, importList);
			String returnRealTypeName1 = genericReturnType;
			// 分页
			if (genericReturnType.startsWith("java.util.List<") || genericReturnType.startsWith("List<")) {
				int index1 = genericReturnType.indexOf("<");
				int index2 = genericReturnType.lastIndexOf(">");
				
				returnRealTypeName1 = genericReturnType.substring(index1 + 1, index2);
			}
			if (_method.getListResultFlag().booleanValue() && _method.getPagination().booleanValue()) {
				String paginationType = PLUGIN_BASE_DIR + ".core.common.util.PaginationSupport";
				String paginationTypeName = getJavaNameAndImport(paginationType, javaNameMap, importList);
				
				genericReturnType = paginationTypeName + "<" + returnRealTypeName1 + ">";
			}
			if (isEmpty(genericReturnType)) {
				_methodMeta.setReturnType(returnTypeName);
			} else {
				_methodMeta.setReturnType(returnTypeName + "<" + genericReturnType + ">");
			}
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
			List<String> annotationList = new ArrayList<>();
			// @ProductCode
			String productCode = PLUGIN_BASE_DIR + ".core.common.annotation.ProductCode";
			String productCodeName = getJavaNameAndImport(productCode, javaNameMap, importList);
			String productLogLevelEnum = PLUGIN_BASE_DIR + ".core.common.annotation.ProductLogLevelEnum";
			String productLogLevelEnumName = getJavaNameAndImport(productLogLevelEnum, javaNameMap, importList);
			String annoProductCode = "@" + productCodeName + "(code = \"00000\", version = \"1.0\", logLevel = " + productLogLevelEnumName + ".INFO)";
			annotationList.add(annoProductCode);
			// @ShackleDomain
			String shackleDomain = PLUGIN_BASE_DIR + ".shackle.ShackleDomain";
			String shackleDomainName = getJavaNameAndImport(shackleDomain, javaNameMap, importList);
			String domain = facade.getName() + makePropertyName(_method.getName()) + "Domain";
			String domainType = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN) + "." + domain;
			String domainName = getJavaNameAndImport(domainType, javaNameMap, importList);
			String domainBeanName = Character.toLowerCase(domainName.charAt(0)) + domainName.substring(1);
			String annoDomain = "@" + shackleDomainName + "(value = \"" + _method.getName() + "\", domain = " + domainName + ".class, domainName = \"" + domainBeanName + "\", withTrans = " + (_method.getWithTransaction().booleanValue()) + ")";
			annotationList.add(annoDomain);
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
					
					String paramString = dealParam(param, importList, javaNameMap);
					parameterList.add(paramString);
				}
			}
			_methodMeta.setParameterList(parameterList);
			
			contentList.add(" * ");
			contentList.add(" * @return 添加说明");
			
			// exceptionList
			// null
			
			contentList.add(" * ");
			contentList.add(" */");
			_methodMeta.setContentList(contentList);
			
			// bodyList
			// null
			
			methodList.add(_methodMeta);
		}
	}
	
	private String dealParam(Param param, List<String> importList, Map<String, String> javaNameMap) throws Exception {
		String paramResult = "";
		
		String type = param.getType();
		String typeName = getJavaNameAndImport(type, javaNameMap, importList);
		paramResult += typeName + " " + param.getName();
		
		return paramResult;
	}
}
