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
 * @Date:   2019年3月13日
 * @Time:   上午10:53:08
 *
 */
class DomainRepositoryMetaGenerator extends GeneratorBase implements Generator {
	
	@Override
	public List<FileMeta> generatorFileMeta(Project project, List<FileMeta> metaList, Integer type) throws Exception {
		List<FileMeta> fileMetaList = new ArrayList<>();
		
		for (String facadeName : PO_ALL_NAME_MAP.keySet()) {
			FileMeta fileMeta = new FileMeta();
			// importList
			List<String> importList = new ArrayList<>();
			// <javaName, javaPacakge>
			Map<String, String> javaNameMap = new HashMap<>();
			
			// PoClass
			Class<?> poClz = PO_ALL_NAME_MAP.get(facadeName);
			
			fileMeta.setClassName(facadeName + "DomainRepository");
			fileMeta.setPreffix(facadeName);
			fileMeta.setClassType(2);
			
			String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".repository";
			fileMeta.setPackageName(packageName);
			
			// null
			fileMeta.setGenericName(null);
			fileMeta.setParentClass(null);
			fileMeta.setSuperInterfaceList(null);
			// null end
			
			// memberList
			List<String> memberList = new ArrayList<>();
			String daoClass = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".dao." + facadeName + "DAO";
			String daoName = this.getJavaNameAndImport(daoClass, javaNameMap, importList);
			String daoPropUp = facadeName + "DAO"; // 首字母大写
			String daoPropLow = makePropertyName(daoPropUp); // 首字母小写
			
			String autowired = "org.springframework.beans.factory.annotation.Autowired";
			String autowiredName = getJavaNameAndImport(autowired, javaNameMap, importList);
			memberList.add("@" + autowiredName);
			memberList.add("private " + daoName + " " + daoPropLow);
			fileMeta.setMemberList(memberList);
			
			// methodList(增删改查)
			List<MethodMeta> methodList = new ArrayList<>();
			String poName = poClz.getName();
			String modelName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_MODEL) + "." + facadeName + "Model";
			makeCURDs(modelName, poName, facadeName, daoPropLow, methodList, javaNameMap, importList);
			fileMeta.setMethodList(methodList);
			
			
			// headerContentList
			List<String> headerContentList = new ArrayList<>();
			headerContentList.add("/**");
			headerContentList.add(" * " + facadeName + "DomainRepository.java");
			headerContentList.add(" */");
			fileMeta.setHeaderContentList(headerContentList);
			
			// classContentList
			List<String> classContentList = generatorClassContent(null, facadeName + "DomainRepository");
			fileMeta.setClassContentList(classContentList);
			
			// headerAnnotationList
			List<String> headerAnnotationList = new ArrayList<>();
			String repositoryType = getJavaNameAndImport("org.springframework.stereotype.Repository", javaNameMap, importList);
			String repositoryName = this.makePropertyName(facadeName + "DomainRepository");
			headerAnnotationList.add("@" + repositoryType + "(\"" + repositoryName + "\")");
			fileMeta.setHeaderAnnotationList(headerAnnotationList);
			
			// importList
			sortImportList(importList);
			fileMeta.setImportList(importList);
			
			fileMetaList.add(fileMeta);
		}
		
		return fileMetaList;
	}
	
	/**
	 * +自动生成增删改查方法
	 * @param modelName model全路径名
	 * @param poName Dpo全路径名
	 * @param prefix 前缀名
	 * @param daoPropLow DAO小写变量名
	 * @param methodList 方法列表
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 */
	private void makeCURDs(String modelName, String poName, String prefix, String daoPropLow, 
			List<MethodMeta> methodList, Map<String, String> javaNameMap, List<String> importList) {
		// 增
		MethodMeta createMethod = makeMethod(modelName, poName, prefix, daoPropLow, 1, javaNameMap, importList);
		if (createMethod != null) {
			methodList.add(createMethod);
		}
		
		// 删
		MethodMeta deleteMethod = makeMethod(modelName, poName, prefix, daoPropLow, 2, javaNameMap, importList);
		if (deleteMethod != null) {
			methodList.add(deleteMethod);
		}
		
		// 更新
		MethodMeta updateMethod = makeMethod(modelName, poName, prefix, daoPropLow, 3, javaNameMap, importList);
		if (updateMethod != null) {
			methodList.add(updateMethod);
		}
		
		// 根据id查询
		MethodMeta queryByIdMethod = makeMethod(modelName, poName, prefix, daoPropLow, 4, javaNameMap, importList);
		if (queryByIdMethod != null) {
			methodList.add(queryByIdMethod);
		}
		
		// 根据条件分页查询
		MethodMeta queryByPageMethod = makeMethod(modelName, poName, prefix, daoPropLow, 5, javaNameMap, importList);
		if (queryByPageMethod != null) {
			methodList.add(queryByPageMethod);
		}
		
		// 批量添加
		MethodMeta batchAddMethod = makeMethod(modelName, poName, prefix, daoPropLow, 6, javaNameMap, importList);
		if (batchAddMethod != null) {
			methodList.add(batchAddMethod);
		}
	}
	
	/**
	 * +自动生成一个方法
	 * @param modelName model类型全路径名
	 * @param poName po类型全路径名
	 * @param prefix 前缀名
	 * @param daoPropLow 类小写变量名
	 * @param type 1-增,2-删,3-更新,4-根据id查询,5-分页查询列表,6-批量添加
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @return MethodMeta
	 */
	private MethodMeta makeMethod(String modelName, String poName, String prefix, String daoPropLow, int type, 
			Map<String, String> javaNameMap, List<String> importList) {
		if (type < 1 || type > 6) {
			return null;
		}
		MethodMeta method = new MethodMeta();
		
		//modelName
		String modelNameTemp = getJavaNameAndImport(modelName, javaNameMap, importList);
		String listType = getJavaNameAndImport("java.util.List", javaNameMap, importList);
		int _idx = modelNameTemp.lastIndexOf(".");
		String modelNameTempShort = _idx < 0 ? modelNameTemp : modelNameTemp.substring(_idx + 1);
		modelNameTempShort = this.makePropertyName(modelNameTempShort);
		// poName
		String poNameTmp  = getJavaNameAndImport(poName, javaNameMap, importList);
		int _idx1 = poNameTmp.lastIndexOf(".");
		String poNameTmpShort  = _idx1 < 0 ? poNameTmp : poNameTmp.substring(_idx1 + 1);
		poNameTmpShort = this.makePropertyName(poNameTmpShort);
		// ClazzConverter
		String convertName = "com.beyonds.phoenix.mountain.core.common.util.ClazzConverter";
		convertName = getJavaNameAndImport(convertName, javaNameMap, importList);
		
		String methodName = "";
		String returnName = "";
		String genericReturnType = null;
		List<String> parameterList = new ArrayList<>();
		List<String> contentList = new ArrayList<>();
		List<String> bodyList = new ArrayList<>();
		contentList.add("/**");
		if (type == 1) {
			methodName = "save" + prefix;
			returnName = "int";
			parameterList.add("final " + modelNameTemp + " " + modelNameTempShort);
			contentList.add(" * +添加一条记录");
			contentList.add(" * @param " + modelNameTempShort + " " + modelNameTempShort);
			
			// bodyList
			bodyList.add(poNameTmp + " " + poNameTmpShort + " = " + convertName + ".converterClass(" + modelNameTempShort + ", " + poNameTmp + ".class);");
			bodyList.add("if (" + poNameTmpShort + " == null) {");
			bodyList.add("    return 0;");
			bodyList.add("}");
			bodyList.add("");
			bodyList.add("int rt = " + daoPropLow + ".insert(" + poNameTmpShort + ");");
			bodyList.add("if (rt > 0) {");
			bodyList.add("    " + modelNameTempShort + ".setId(" + poNameTmpShort + ".getId());");
			bodyList.add("}");
			bodyList.add("");
			bodyList.add("return rt;");
		} else if (type == 2) {
			methodName = "delete" + prefix;
			returnName = "int";
			parameterList.add("final Long id");
			contentList.add(" * +删除一条记录");
			contentList.add(" * @param id id");
			
			// bodyList
			bodyList.add("if (id == null || id.longValue() <= 0L) {");
			bodyList.add("    return 0;");
			bodyList.add("}");
			bodyList.add("");
			bodyList.add("return " + daoPropLow + ".delete(id);");
		} else if (type == 3) {
			methodName = "update" + prefix;
			returnName = "int";
			parameterList.add("final " + modelNameTemp + " " + modelNameTempShort);
			contentList.add(" * +更新一条记录");
			contentList.add(" * @param " + modelNameTempShort + " " + modelNameTempShort);
			
			// bodyList
			bodyList.add(poNameTmp + " " + poNameTmpShort + " = " + convertName + ".converterClass(" + modelNameTempShort + ", " + poNameTmp + ".class);");
			bodyList.add("if (" + poNameTmpShort + " == null) {");
			bodyList.add("    return 0;");
			bodyList.add("}");
			bodyList.add("");
			bodyList.add("if (" + poNameTmpShort + ".getId() == null) {");
			bodyList.add("    return 0;");
			bodyList.add("}");
			bodyList.add("");
			bodyList.add("return " + daoPropLow + ".update(" + poNameTmpShort + ");");
		} else if (type == 4) {
			methodName = "query" + prefix + "ById";
			returnName = modelNameTemp;
			parameterList.add("final Long id");
			contentList.add(" * +根据id查询记录");
			contentList.add(" * @param id id");
			
			// bodyList
			bodyList.add("if (id == null || id.longValue() <= 0L) {");
			bodyList.add("    return null;");
			bodyList.add("}");
			bodyList.add("");
			bodyList.add(poNameTmp + " " + poNameTmpShort + " = " + daoPropLow + ".queryById(id);");
			bodyList.add("return " + convertName + ".converterClass(" + poNameTmpShort + ", " + modelNameTemp + ".class);");
		} else if (type == 5) {
			methodName = "query" + prefix + "sByPage";
			String paginationType = getJavaNameAndImport("com.beyonds.phoenix.mountain.core.common.util.PaginationSupport", javaNameMap, importList);
			returnName = paginationType + "<" + modelNameTemp + ">";
			genericReturnType = modelNameTemp;
			parameterList.add("final " + modelNameTemp + " " + modelNameTempShort);
			parameterList.add("final int page");
			parameterList.add("final int pageSize");
			contentList.add(" * +根据条件分页查询记录");
			contentList.add(" * @param " + modelNameTempShort + " " + modelNameTempShort);
			contentList.add(" * @param page page");
			contentList.add(" * @param pageSize pageSize");
			
			// bodyList
			bodyList.add("int p = page, ps = pageSize;");
			bodyList.add("if (pageSize <= 0) {");
			bodyList.add("    ps = " + paginationType + ".DEFAULT_PAGESIZE;");
			bodyList.add("} else if (pageSize > " + paginationType + ".DEFAULT_MAX_PAGESIZE) {");
			bodyList.add("    ps = PaginationSupport.DEFAULT_MAX_PAGESIZE;");
			bodyList.add("}");
			bodyList.add("if (page <= 0) {");
			bodyList.add("    p = 1;");
			bodyList.add("}");
			bodyList.add("");
			String pageType = getJavaNameAndImport("com.github.pagehelper.Page", javaNameMap, importList);
			String pageHelperType = getJavaNameAndImport("com.github.pagehelper.PageHelper", javaNameMap, importList);
			bodyList.add(pageType + "<" + modelNameTemp + "> pageInfo = " + pageHelperType + ".startPage(p, ps);");
			bodyList.add("");
			bodyList.add(poNameTmp + " " + poNameTmpShort + " = " + convertName + ".converterClass(" + modelNameTempShort + ", " + poNameTmp + ".class);");
			bodyList.add(listType + "<" + poNameTmp + "> poList = " + daoPropLow + ".queryList(" + poNameTmpShort + ");");
			bodyList.add("");
			bodyList.add(listType + "<" + modelNameTemp + "> list = (" + listType + "<" + modelNameTemp + ">) " + convertName + ".converterClass(poList, " + modelNameTemp + ".class);");
			bodyList.add("if (list == null) {");
			String arrayListType = getJavaNameAndImport("java.util.ArrayList", javaNameMap, importList);
			bodyList.add("    list = new " + arrayListType + "<>();");
			bodyList.add("}");
			bodyList.add("");
			bodyList.add("return new " + paginationType + "<>(list, (int) pageInfo.getTotal(), pageInfo.getPageSize(), pageInfo.getPageNum());");
		} else if (type == 6) {
			methodName = "batchSave" + prefix + "s";
			returnName = "int";
			String _paramName = modelNameTempShort + "List";
			parameterList.add("final " + listType + "<" + modelNameTemp + "> " + _paramName);
			contentList.add(" * +批量添加记录");
			contentList.add(" * @param " + _paramName + " " + _paramName);
			
			// bodyList
			bodyList.add("if (" + _paramName + " == null || " + _paramName + ".isEmpty()) {");
			bodyList.add("    return 0;");
			bodyList.add("}");
			bodyList.add(listType + "<" + poNameTmp + "> list = (" + listType + "<" + poNameTmp + ">) " + convertName + ".converterClass(" + _paramName + ", " + poNameTmp + ".class);");
			bodyList.add("if (list == null || list.size() != " + _paramName + ".size()) {");
			bodyList.add("    return 0;");
			bodyList.add("}");
			bodyList.add("");
			bodyList.add("return " + daoPropLow + ".batchInserts(list);");
		}
		contentList.add(" * @return returnResult");
		contentList.add(" */");
		method.setMethodName(methodName);
		method.setReturnType(returnName);
		if (genericReturnType != null && !genericReturnType.trim().equals("")) {
			method.setGenericReturnType(genericReturnType.trim());
		}
		if (!parameterList.isEmpty()) {
			method.setParameterList(parameterList);
		}
		if (!contentList.isEmpty()) {
			method.setContentList(contentList);
		}
		if (!bodyList.isEmpty()) {
			method.setBodyList(bodyList);
		}
		
		// null
		method.setAnnotationList(null);
		method.setExceptionList(null);
		// null end
		
		return method;
	}
}
