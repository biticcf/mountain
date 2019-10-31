/**
 * 
 */
package com.github.biticcf.mountain.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * author: Daniel.Cao
 * date:   2019年3月13日
 * time:   上午10:53:08
 *
 */
class SqlProviderMetaGenerator extends GeneratorBase implements Generator {
	
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
			
			fileMeta.setClassName(facadeName + "SqlProvider");
			fileMeta.setPreffix(facadeName);
			fileMeta.setClassType(2);
			
			String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".dao.sqlprovider";
			fileMeta.setPackageName(packageName);
			
			// null
			fileMeta.setGenericName(null);
			fileMeta.setParentClass(null);
			fileMeta.setSuperInterfaceList(null);
			fileMeta.setMemberList(null);
			fileMeta.setHeaderAnnotationList(null);
			// null end
			
			// methodList
			List<MethodMeta> methodList = new ArrayList<>();
			String poName = poClz.getName();
			makeSqlProviderMethods(poName, methodList, javaNameMap, importList, poClz);
			fileMeta.setMethodList(methodList);
			
			// headerContentList
			List<String> headerContentList = new ArrayList<>();
			headerContentList.add("/**");
			headerContentList.add(" * " + facadeName + "SqlProvider.java");
			headerContentList.add(" */");
			fileMeta.setHeaderContentList(headerContentList);
			
			// classContentList
			List<String> classContentList = generatorClassContent(null, facadeName + "SqlProvider");
			fileMeta.setClassContentList(classContentList);
			
			// importList
			sortImportList(importList);
			fileMeta.setImportList(importList);
			
			fileMetaList.add(fileMeta);
		}
		
		return fileMetaList;
	}
	
	/**
	 * +自动生成SQL
	 * @param poName po全路径名
	 * @param methodList 方法列表
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param poClz poClz
	 * @throws Exception Exception
	 */
	private void makeSqlProviderMethods(String poName, List<MethodMeta> methodList, Map<String, String> javaNameMap, 
			List<String> importList, Class<?> poClz) throws Exception {
		// 增insert
		MethodMeta createMethod = makeInsertMethod(poName, javaNameMap, importList, poClz);
		if (createMethod != null) {
			methodList.add(createMethod);
		}
		
		// 批量增加inserts
		MethodMeta deleteMethod = makeInsertsMethod(poName, javaNameMap, importList, poClz);
		if (deleteMethod != null) {
			methodList.add(deleteMethod);
		}
		
		// 更新update
		MethodMeta updateMethod = makeUpdateMethod(poName, javaNameMap, importList, poClz);
		if (updateMethod != null) {
			methodList.add(updateMethod);
		}
		
		// 根据条件查询列表queryList
		MethodMeta queryByIdMethod = makeQueryListMethod(poName, javaNameMap, importList, poClz);
		if (queryByIdMethod != null) {
			methodList.add(queryByIdMethod);
		}
	}
	
	/**
	 * +自动生成一个insert方法
	 * @param poName
	 * @param javaNameMap
	 * @param importList
	 * @param poClz
	 * @return MethodMeta
	 */
	private MethodMeta makeInsertMethod(String poName, Map<String, String> javaNameMap, 
			List<String> importList, Class<?> poClz) {
		MethodMeta method = new MethodMeta();
		
		String poNameTmp  = getJavaNameAndImport(poName, javaNameMap, importList);
		int _idx1 = poNameTmp.lastIndexOf(".");
		String poNameTmpShort  = _idx1 < 0 ? poNameTmp : poNameTmp.substring(_idx1 + 1);
		poNameTmpShort = this.makePropertyName(poNameTmpShort);
		
		String methodName = "";
		String returnName = "";
		List<String> parameterList = new ArrayList<>();
		List<String> contentList = new ArrayList<>();
		List<String> bodyList = new ArrayList<>();
		contentList.add("/**");
		
		methodName = "insert";
		returnName = "String";
		parameterList.add("final " + poNameTmp + " " + poNameTmpShort);
		contentList.add(" * +生成添加一条记录的SQL");
		contentList.add(" * @param " + poNameTmpShort + " " + poNameTmpShort);
		
		// bodyList
		TableName tableNameAnn = poClz.getAnnotation(TableName.class);
		String sqlType  = getJavaNameAndImport("org.apache.ibatis.jdbc.SQL", javaNameMap, importList);
		bodyList.add("return new " + sqlType + "() {");
		bodyList.add("    {");
		bodyList.add("        INSERT_INTO(\"`" + tableNameAnn.value() + "`\");");
		Field[] fields = poClz.getDeclaredFields();
		String columns = "", values = "";
		int length = fields.length;
		for (int i = 0; i < length; i++) {
			Field field = fields[i];
			if (field == null) {
				continue;
			}
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
				continue;
			}
			TableField tableField = field.getAnnotation(TableField.class);
			TableId tableId = field.getAnnotation(TableId.class);
			if (tableId != null) {
				continue;
			}
			String propertyName = field.getName();
			String columnName = tableField.value();
			if (i < length - 1) {
				columns += "\"`" + columnName + "`\",";
				if (propertyName.equalsIgnoreCase("createTime") || propertyName.equalsIgnoreCase("updateTime")) {
					values += "\"now()\",";
				} else if (propertyName.equalsIgnoreCase("status")) {
					values += "\"0\",";
				} else if (propertyName.equalsIgnoreCase("version")) {
					values += "\"0\",";
				} else {
					values += "\"#{" + propertyName + "}\",";
				}
			} else {
				columns += "\"`" + columnName + "`\"";
				if (propertyName.equalsIgnoreCase("createTime") || propertyName.equalsIgnoreCase("updateTime")) {
					values += "\"now()\"";
				} else if (propertyName.equalsIgnoreCase("status")) {
					values += "\"0\"";
				} else if (propertyName.equalsIgnoreCase("version")) {
					values += "\"0\"";
				} else {
					values += "\"#{" + propertyName + "}\"";
				}
			}
		}
		bodyList.add("        INTO_COLUMNS(" + columns + ");");
		bodyList.add("        INTO_VALUES(" + values + ");");
		bodyList.add("    }");
		bodyList.add("}.toString();");
		
		
		contentList.add(" * @return returnResult");
		contentList.add(" */");
		method.setMethodName(methodName);
		method.setReturnType(returnName);
		
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
		method.setGenericReturnType(null);
		method.setAnnotationList(null);
		method.setExceptionList(null);
		// null end
		
		return method;
	}
	/**
	 * +自动生成一个批量inserts方法
	 * @param poName
	 * @param javaNameMap
	 * @param importList
	 * @param poClz
	 * @return
	 */
	private MethodMeta makeInsertsMethod(String poName, Map<String, String> javaNameMap, 
			List<String> importList, Class<?> poClz) {
		MethodMeta method = new MethodMeta();
		
		String poNameTmp  = getJavaNameAndImport(poName, javaNameMap, importList);
		List<String> parameterList = new ArrayList<>();
		List<String> contentList = new ArrayList<>();
		List<String> bodyList = new ArrayList<>();
		contentList.add("/**");
		
		String methodName = "inserts";
		String returnName = "String";
		String mapType = getJavaNameAndImport("java.util.Map", javaNameMap, importList);
		String listType = getJavaNameAndImport("java.util.List", javaNameMap, importList);
		parameterList.add("final " + mapType + "<String, " + listType + "<" + poNameTmp + ">> map");
		contentList.add(" * +生成批量添加记录的SQL");
		contentList.add(" * @param map map");
		
		// bodyList
		TableName tableNameAnn = poClz.getAnnotation(TableName.class);
		
		bodyList.add(listType + "<" + poNameTmp + "> tmpList = map.get(\"list\");");
		bodyList.add("if (tmpList == null || tmpList.isEmpty()) {");
		bodyList.add("    return null;");
		bodyList.add("}");
		bodyList.add("");
		bodyList.add("StringBuilder sql = new StringBuilder(\"\");");
		bodyList.add("sql.append(\"INSERT INTO `" + tableNameAnn.value() + "`\");");
		
		Field[] fields = poClz.getDeclaredFields();
		int length = fields.length;
		String columns = "", values = "";
		for (int i = 0; i < length; i ++) {
			Field field = fields[i];
			if (field == null) {
				continue;
			}
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
				continue;
			}
			TableField tableField = field.getAnnotation(TableField.class);
			TableId tableId = field.getAnnotation(TableId.class);
			if (tableId != null) {
				continue;
			}
			String propertyName = field.getName();
			String columnName = tableField.value();
			if (i < length - 1) {
				columns += "`" + columnName + "`,";
				if (propertyName.equalsIgnoreCase("createTime") || propertyName.equalsIgnoreCase("updateTime")) {
					values += "now(),";
				} else if (propertyName.equalsIgnoreCase("status")) {
					values += "0,";
				} else if (propertyName.equalsIgnoreCase("version")) {
					values += "0,";
				} else {
					values += "#{list[\" + i + \"]." + propertyName + "},";
				}
			} else {
				columns += "`" + columnName + "`";
				if (propertyName.equalsIgnoreCase("createTime") || propertyName.equalsIgnoreCase("updateTime")) {
					values += "now()";
				} else if (propertyName.equalsIgnoreCase("status")) {
					values += "0";
				} else if (propertyName.equalsIgnoreCase("version")) {
					values += "0";
				} else {
					values += "#{list[\" + i + \"]." + propertyName + "}";
				}
			}
		}
		bodyList.add("sql.append(\"(" + columns + ")\");");
		bodyList.add("sql.append(\"VALUES\");");
		bodyList.add("for (int i = 0; i < tmpList.size(); i++) {");
		bodyList.add("    sql.append(\"(" + values + "),\");");
		bodyList.add("}");
		bodyList.add("sql.deleteCharAt(sql.length() - 1);");
		bodyList.add("sql.append(\" ON DUPLICATE KEY UPDATE `update_time` = now()\");");
		bodyList.add("sql.append(\";\");");
		bodyList.add("");
		bodyList.add("return sql.toString();");
		
		contentList.add(" * @return returnResult");
		contentList.add(" */");
		method.setMethodName(methodName);
		method.setReturnType(returnName);
		
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
		method.setGenericReturnType(null);
		method.setAnnotationList(null);
		method.setExceptionList(null);
		// null end
		
		return method;
	}
	
	/**
	 * +自动生成一个update方法
	 * @param poName poName
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param poClz poClz
	 * @return MethodMeta
	 * @throws Exception Exception
	 */
	private MethodMeta makeUpdateMethod(String poName, Map<String, String> javaNameMap, 
			List<String> importList, Class<?> poClz) throws Exception {
		MethodMeta method = new MethodMeta();
		
		String poNameTmp  = getJavaNameAndImport(poName, javaNameMap, importList);
		int _idx1 = poNameTmp.lastIndexOf(".");
		String poNameTmpShort  = _idx1 < 0 ? poNameTmp : poNameTmp.substring(_idx1 + 1);
		poNameTmpShort = this.makePropertyName(poNameTmpShort);
		
		String methodName = "";
		String returnName = "";
		List<String> parameterList = new ArrayList<>();
		List<String> contentList = new ArrayList<>();
		List<String> bodyList = new ArrayList<>();
		contentList.add("/**");
		
		methodName = "update";
		returnName = "String";
		parameterList.add("final " + poNameTmp + " " + poNameTmpShort);
		contentList.add(" * +生成更新一条记录的SQL");
		contentList.add(" * @param " + poNameTmpShort + " " + poNameTmpShort);
		
		// bodyList
		TableName tableNameAnn = poClz.getAnnotation(TableName.class);
		
		bodyList.add("StringBuilder sql = new StringBuilder(\"\");");
		bodyList.add("sql.append(\"UPDATE `" + tableNameAnn.value() + "` SET `id` = #{id}\");");
		Field[] fields = poClz.getDeclaredFields();
		boolean hasVersion = false;
		for (int i = 0; i < fields.length; i ++) {
			Field field = fields[i];
			if (field == null) {
				continue;
			}
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
				continue;
			}
			
			TableField tableField = field.getAnnotation(TableField.class);
			TableId tableId = field.getAnnotation(TableId.class);
			String propertyName = field.getName();
			String columnName = tableField.value();
			if (propertyName.equalsIgnoreCase("updateTime")) {
				bodyList.add("sql.append(\", `update_time` = now()\");");
			} else if (propertyName.equalsIgnoreCase("version")) {
				hasVersion = true;
				bodyList.add("sql.append(\", `version` = `version` + 1\");");
			} else {
				if (tableId != null || propertyName.equalsIgnoreCase("createTime")) {
					continue;
				}
				bodyList.add("if (" + poNameTmpShort + ".get" + makePropertyName(propertyName) + "() != null) {");
				bodyList.add("    sql.append(\", `" + columnName + "` = #{" + propertyName + "}\");");
				bodyList.add("}");
			}
		}
		String whereSql = " WHERE `id` = #{id}";
		if (hasVersion) {
			whereSql += " AND `version` = #{version}";
		}
		bodyList.add("sql.append(\"" + whereSql + "\");");
		bodyList.add("");
		bodyList.add("return sql.toString();");
		
		contentList.add(" * @return returnResult");
		contentList.add(" */");
		method.setMethodName(methodName);
		method.setReturnType(returnName);
		
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
		method.setGenericReturnType(null);
		method.setAnnotationList(null);
		method.setExceptionList(null);
		// null end
		
		return method;
	}
	/**
	 * +自动生成一个列表查询方法
	 * @param poName poName
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param poClz poClz
	 * @return MethodMeta
	 * @throws Exception Exception
	 */
	private MethodMeta makeQueryListMethod(String poName, Map<String, String> javaNameMap, 
			List<String> importList, Class<?> poClz) throws Exception {
		MethodMeta method = new MethodMeta();
		
		String poNameTmp  = getJavaNameAndImport(poName, javaNameMap, importList);
		int _idx1 = poNameTmp.lastIndexOf(".");
		String poNameTmpShort  = _idx1 < 0 ? poNameTmp : poNameTmp.substring(_idx1 + 1);
		poNameTmpShort = this.makePropertyName(poNameTmpShort);
		
		String methodName = "";
		String returnName = "";
		List<String> parameterList = new ArrayList<>();
		List<String> contentList = new ArrayList<>();
		List<String> bodyList = new ArrayList<>();
		contentList.add("/**");
		
		methodName = "queryList";
		returnName = "String";
		parameterList.add("final " + poNameTmp + " " + poNameTmpShort);
		contentList.add(" * +生成查询列表记录的SQL");
		contentList.add(" * @param " + poNameTmpShort + " " + poNameTmpShort);
		
		// bodyList
		TableName tableNameAnn = poClz.getAnnotation(TableName.class);
		
		bodyList.add("StringBuilder sql = new StringBuilder(\"\");");
		bodyList.add("sql.append(\"SELECT * FROM `" + tableNameAnn.value() + "` WHERE 1 = 1\");");
		Field[] fields = poClz.getDeclaredFields();
		for (int i = 0; i < fields.length; i ++) {
			Field field = fields[i];
			if (field == null) {
				continue;
			}
			int modifiers = field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
				continue;
			}
			
			TableField tableField = field.getAnnotation(TableField.class);
			String propertyName = field.getName();
			String columnName = tableField.value();
			
			bodyList.add("if (" + poNameTmpShort + ".get" + makePropertyName(propertyName) + "() != null) {");
			//EnuFieldType enuFieldType = columnConfig.columnType();
			//if (enuFieldType.getJavaType().equals("java.lang.String")) {
			//	bodyList.add("    sql.append(\"  AND `" + columnName + "` LIKE CONCAT('%',#{" + propertyName + "},'%')\");");
			//} else {
			bodyList.add("    sql.append(\"  AND `" + columnName + "` = #{" + propertyName + "}\");");
			//}
			bodyList.add("}");
		}
		bodyList.add("sql.append(\"  ORDER BY `id` DESC\");");
		bodyList.add("");
		bodyList.add("return sql.toString();");
		
		contentList.add(" * @return returnResult");
		contentList.add(" */");
		method.setMethodName(methodName);
		method.setReturnType(returnName);
		
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
		method.setGenericReturnType(null);
		method.setAnnotationList(null);
		method.setExceptionList(null);
		// null end
		
		return method;
	}
}
