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

import com.github.biticcf.mountain.generator.annotation.ColumnConfig;
import com.github.biticcf.mountain.generator.annotation.EnuFieldType;
import com.github.biticcf.mountain.generator.annotation.TableConfig;

/**
 * author: Daniel.Cao
 * date:   2019年3月13日
 * time:   上午10:53:08
 *
 */
class DaoMetaGenerator extends GeneratorBase implements Generator {
	
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
			
			fileMeta.setClassName(facadeName + "DAO");
			fileMeta.setPreffix(facadeName);
			fileMeta.setClassType(1);
			
			String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".dao";
			fileMeta.setPackageName(packageName);
			
			// null
			fileMeta.setGenericName(null);
			fileMeta.setParentClass(null);
			fileMeta.setSuperInterfaceList(null);
			fileMeta.setMemberList(null);
			// null end
			
			// methodList(增删改查)
			List<MethodMeta> methodList = new ArrayList<>();
			String poName = poClz.getName();
			makeCURDs(poName, methodList, javaNameMap, importList, poClz, facadeName);
			fileMeta.setMethodList(methodList);
			
			
			// headerContentList
			List<String> headerContentList = new ArrayList<>();
			headerContentList.add("/**");
			headerContentList.add(" * " + facadeName + "DAO.java");
			headerContentList.add(" */");
			fileMeta.setHeaderContentList(headerContentList);
			
			// classContentList
			List<String> classContentList = generatorClassContent(null, facadeName + "DAO");
			fileMeta.setClassContentList(classContentList);
			
			// headerAnnotationList
			List<String> headerAnnotationList = new ArrayList<>();
			// @CacheNamespace
			String lruCacheType = getJavaNameAndImport("org.apache.ibatis.cache.decorators.LruCache", javaNameMap, importList);
			String cacheNamespaceType = getJavaNameAndImport("org.apache.ibatis.annotations.CacheNamespace", javaNameMap, importList);
			StringBuilder cacheBuilder = new StringBuilder("");
			cacheBuilder.append("@")
			            .append(cacheNamespaceType)
			            .append("(eviction = ")
			            .append(lruCacheType)
			            .append(".class, flushInterval = 60000L, size = 1024, readWrite = true)");
			headerAnnotationList.add(cacheBuilder.toString());
			// @Mapper
			String mapperType = getJavaNameAndImport("org.apache.ibatis.annotations.Mapper", javaNameMap, importList);
			headerAnnotationList.add("@" + mapperType);
			
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
	 * @param poName Dpo全路径名
	 * @param methodList 方法列表
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param poClz poClz
	 * @param prefix prefix
	 * @throws Exception Exception
	 */
	private void makeCURDs(String poName, List<MethodMeta> methodList, Map<String, String> javaNameMap, 
			List<String> importList, Class<?> poClz, String prefix) throws Exception {
		// 增
		MethodMeta createMethod = makeMethodDefine(poName, 1, javaNameMap, importList, poClz, prefix);
		if (createMethod != null) {
			methodList.add(createMethod);
		}
		
		// 删
		MethodMeta deleteMethod = makeMethodDefine(poName, 2, javaNameMap, importList, poClz, prefix);
		if (deleteMethod != null) {
			methodList.add(deleteMethod);
		}
		
		// 更新
		MethodMeta updateMethod = makeMethodDefine(poName, 3, javaNameMap, importList, poClz, prefix);
		if (updateMethod != null) {
			methodList.add(updateMethod);
		}
		
		// 根据id查询
		MethodMeta queryByIdMethod = makeMethodDefine(poName, 4, javaNameMap, importList, poClz, prefix);
		if (queryByIdMethod != null) {
			methodList.add(queryByIdMethod);
		}
		
		// 根据条件分页查询
		MethodMeta queryByPageMethod = makeMethodDefine(poName, 5, javaNameMap, importList, poClz, prefix);
		if (queryByPageMethod != null) {
			methodList.add(queryByPageMethod);
		}
		
		// 批量添加
		MethodMeta batchAddMethod = makeMethodDefine(poName, 6, javaNameMap, importList, poClz, prefix);
		if (batchAddMethod != null) {
			methodList.add(batchAddMethod);
		}
	}
	
	/**
	 * +自动生成一个方法
	 * @param poName po类型全路径名
	 * @param type 1-增,2-删,3-更新,4-根据id查询,5-分页查询列表,6-批量添加
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param poClz poClz
	 * @param prefix prefix
	 * @return MethodMeta
	 * @throws Exception Exception
	 */
	private MethodMeta makeMethodDefine(String poName, int type, Map<String, String> javaNameMap, 
			List<String> importList, Class<?> poClz, String prefix) throws Exception {
		if (type < 1 || type > 6) {
			return null;
		}
		MethodMeta method = new MethodMeta();
		
		String listType = getJavaNameAndImport("java.util.List", javaNameMap, importList);
		// poName
		String poNameTmp  = getJavaNameAndImport(poName, javaNameMap, importList);
		int _idx = poNameTmp.lastIndexOf(".");
		String poNameTmpShort  = _idx < 0 ? poNameTmp : poNameTmp.substring(_idx + 1);
		poNameTmpShort = this.makePropertyName(poNameTmpShort);
		
		TableConfig[] tableConfigs = poClz.getAnnotationsByType(TableConfig.class);
		if (tableConfigs == null || tableConfigs.length == 0) {
			throw new Exception("[" + poClz.getName() + "]Can Not Found TableConfig Annotations!");
		}
		if (tableConfigs.length > 1) {
			throw new Exception("[" + poClz.getName() + "]Found More than One TableConfig Annotations!");
		}
		TableConfig tableConfig = tableConfigs[0];
		String tableName = tableConfig.tableName();
		if (tableName == null || tableName.trim().equals("")) {
			throw new Exception("[" + poClz.getName() + "]TableName Cannot Null On TableConfig Annotations!");
		}
		
		String methodName = "";
		String returnName = "";
		String resultMapName = this.makePropertyName(prefix + "ResultMap");
		String genericReturnType = null;
		List<String> parameterList = new ArrayList<>();
		List<String> contentList = new ArrayList<>();
		List<String> annotationList = new ArrayList<>();
		contentList.add("/**");
		if (type == 1) {
			methodName = "insert";
			returnName = "int";
			parameterList.add("final " + poNameTmp + " " + poNameTmpShort);
			contentList.add(" * +添加一条记录");
			contentList.add(" * @param " + poNameTmpShort + " " + poNameTmpShort);
			
			// annotationList
			makeInsertAnnotation(annotationList, javaNameMap, importList, prefix);
		} else if (type == 2) {
			methodName = "delete";
			returnName = "int";
			parameterList.add("final Long id");
			contentList.add(" * +删除一条记录");
			contentList.add(" * @param id id");
			
			// annotationList
			makeDeleteAnnotation(annotationList, javaNameMap, importList, tableConfig);
		} else if (type == 3) {
			methodName = "update";
			returnName = "int";
			parameterList.add("final " + poNameTmp + " " + poNameTmpShort);
			contentList.add(" * +更新一条记录");
			contentList.add(" * @param " + poNameTmpShort + " " + poNameTmpShort);
			
			// annotationList
			makeUpdateAnnotation(annotationList, javaNameMap, importList, prefix);
		} else if (type == 4) {
			methodName = "queryById";
			returnName = poNameTmp;
			parameterList.add("final Long id");
			contentList.add(" * +根据id查询记录");
			contentList.add(" * @param id id");
			
			// annotationList
			makeQueryByIdAnnotation(annotationList, javaNameMap, importList, tableConfig, resultMapName, poClz);
		} else if (type == 5) {
			methodName = "queryList";
			returnName = listType + "<" + poNameTmp + ">";
			genericReturnType = poNameTmp;
			parameterList.add("final " + poNameTmp + " " + poNameTmpShort);
			contentList.add(" * +根据条件分页查询记录");
			contentList.add(" * @param " + poNameTmp + " " + poNameTmpShort);
			
			// annotationList
			makeQueryListAnnotation(annotationList, javaNameMap, importList, prefix, resultMapName);
		} else if (type == 6) {
			methodName = "batchInserts";
			returnName = "int";
			String _paramName = poNameTmpShort + "List";
			String paramType = getJavaNameAndImport("org.apache.ibatis.annotations.Param", javaNameMap, importList);
			parameterList.add("@" + paramType + "(\"list\") final " + listType + "<" + poNameTmp + "> " + _paramName);
			contentList.add(" * +批量添加记录");
			contentList.add(" * @param " + _paramName + " " + _paramName);
			
			// annotationList
			makeInsertsAnnotation(annotationList, javaNameMap, importList, prefix);
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
		if (!annotationList.isEmpty()) {
			method.setAnnotationList(annotationList);
		}
		
		// null
		method.setBodyList(null);
		method.setExceptionList(null);
		// null end
		
		return method;
	}
	
	/**
	 * 设置缓存策略注解
	 * @param useCache useCache
	 * @param cachePolicy cachePolicy
	 * @param withKey withKey
	 * @param optionType optionType
	 * @param withTimeout withTimeout
	 * @return Options
	 */
	private String makeOptionAnnotation(boolean useCache, boolean cachePolicy, boolean withKey, 
			String optionType, boolean withTimeout) {
		StringBuilder sb = new StringBuilder("");
		sb.append("@")
		  .append(optionType)
		  .append("(useCache = ")
		  .append(useCache)
		  .append(", flushCache = ")
		  .append(optionType)
		  .append(".FlushCachePolicy.")
		  .append(cachePolicy ? "TRUE" : "FALSE");
		if (withTimeout) {
			sb.append(", timeout = 60000");
		}
		if (withKey) {
			sb.append(", useGeneratedKeys = true, keyProperty = \"id\", keyColumn = \"id\"");
		}
		sb.append(")");
		
		return sb.toString();
	}
	
	/**
	 * +拼装insert的sql语句
	 * @param annotationList annotationList
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param prefix prefix
	 */
	private void makeInsertAnnotation(List<String> annotationList, Map<String, String> javaNameMap, 
			List<String> importList, String prefix) {
		// 1.options
		String optionType =  getJavaNameAndImport("org.apache.ibatis.annotations.Options", javaNameMap, importList);
		annotationList.add(makeOptionAnnotation(true, true, true, optionType, false));
		// 2.sqlprovider
		String providerType = getJavaNameAndImport("org.apache.ibatis.annotations.InsertProvider", javaNameMap, importList);
		String sqlProvider = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".dao.sqlprovider." + prefix + "SqlProvider";
		String insertType = getJavaNameAndImport(sqlProvider, javaNameMap, importList);
		annotationList.add("@" + providerType + "(type = " + insertType + ".class, method = \"insert\")");
	}
	
	/**
	 * +拼装inserts的sql语句
	 * @param annotationList annotationList
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param prefix prefix
	 */
	private void makeInsertsAnnotation(List<String> annotationList, Map<String, String> javaNameMap, 
			List<String> importList, String prefix) {
		// 1.options
		String optionType =  getJavaNameAndImport("org.apache.ibatis.annotations.Options", javaNameMap, importList);
		annotationList.add(makeOptionAnnotation(true, true, true, optionType, false));
		// 2.sqlprovider
		String providerType = getJavaNameAndImport("org.apache.ibatis.annotations.InsertProvider", javaNameMap, importList);
		String sqlProvider = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".dao.sqlprovider." + prefix + "SqlProvider";
		String insertType = getJavaNameAndImport(sqlProvider, javaNameMap, importList);
		annotationList.add("@" + providerType + "(type = " + insertType + ".class, method = \"inserts\")");
	}
	
	/**
	 * +拼装delete的sql语句
	 * @param annotationList annotationList
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param tableConfig tableConfig
	 */
	private void makeDeleteAnnotation(List<String> annotationList, Map<String, String> javaNameMap, 
			List<String> importList, TableConfig tableConfig) {
		// 1.options
		String optionType =  getJavaNameAndImport("org.apache.ibatis.annotations.Options", javaNameMap, importList);
		annotationList.add(makeOptionAnnotation(true, true, false, optionType, false));
		// 2.sqlprovider
		String deleteType = getJavaNameAndImport("org.apache.ibatis.annotations.Delete", javaNameMap, importList);
	
		String tableName = tableConfig.tableName();
		
		annotationList.add("@" + deleteType + "(\"DELETE FROM `" + tableName + "` WHERE `id` = #{id}\")");
	}
	
	/**
	 * +拼装update的sql语句
	 * @param annotationList annotationList
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param prefix prefix
	 */
	private void makeUpdateAnnotation(List<String> annotationList, Map<String, String> javaNameMap, 
			List<String> importList, String prefix) {
		// 1.options
		String optionType =  getJavaNameAndImport("org.apache.ibatis.annotations.Options", javaNameMap, importList);
		annotationList.add(makeOptionAnnotation(true, true, false, optionType, false));
		// 2.sqlprovider
		String providerType = getJavaNameAndImport("org.apache.ibatis.annotations.UpdateProvider", javaNameMap, importList);
		String sqlProvider = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".dao.sqlprovider." + prefix + "SqlProvider";
		String updateType = getJavaNameAndImport(sqlProvider, javaNameMap, importList);
		annotationList.add("@" + providerType + "(type = " + updateType + ".class, method = \"update\")");
	}
	/**
	 * +拼装select的sql语句
	 * @param annotationList annotationList
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param tableConfig tableConfig
	 * @param resultMapName resultMapName
	 * @param poClz poClz
	 * @exception Exception
	 */
	private void makeQueryByIdAnnotation(List<String> annotationList, Map<String, String> javaNameMap, 
			List<String> importList, TableConfig tableConfig, String resultMapName, Class<?> poClz) throws Exception {
		// 1.options
		String optionType =  getJavaNameAndImport("org.apache.ibatis.annotations.Options", javaNameMap, importList);
		annotationList.add(makeOptionAnnotation(true, false, false, optionType, true));
		// 2.处理ResultMap定义
		String resultsType = getJavaNameAndImport("org.apache.ibatis.annotations.Results", javaNameMap, importList);
		annotationList.add("@" + resultsType + "(id = \"" + resultMapName + "\", value = {");
		String resultType = getJavaNameAndImport("org.apache.ibatis.annotations.Result", javaNameMap, importList);
		String jdbcType = getJavaNameAndImport("org.apache.ibatis.type.JdbcType", javaNameMap, importList);
		Field[] fields = poClz.getDeclaredFields();
		if (fields == null || fields.length <= 0) {
			throw new Exception("[" + poClz.getName() + "] Can Not Found Fields!");
		}
		int length = fields.length;
		for (int i = 0; i < length; i ++) {
			Field _field = fields[i];
			if (_field == null) {
				continue;
			}
			int modifiers = _field.getModifiers();
			if (Modifier.isStatic(modifiers) || Modifier.isTransient(modifiers) || Modifier.isFinal(modifiers)) {
				continue;
			}
			ColumnConfig[] columnConfigs = _field.getAnnotationsByType(ColumnConfig.class);
			if (columnConfigs == null || columnConfigs.length == 0) {
				throw new Exception("[" + poClz.getName() + "." + _field.getName() + "] Can Not Found @ColumnConfig!");
			}
			if (columnConfigs.length > 1) {
				throw new Exception("[" + poClz.getName() + "." + _field.getName() + "] Found More Than One @ColumnConfig!");
			}
			ColumnConfig columnConfig = columnConfigs[0];
			String propertyName = columnConfig.propertyName();
			String columnName = columnConfig.columnName();
			EnuFieldType columnType = columnConfig.columnType();
			if (propertyName == null || propertyName.trim().equals("")) {
				throw new Exception("[" + poClz.getName() + "." + _field.getName() + "] ColumnConfig.propertyName Error!");
			}
			if (columnName == null || columnName.trim().equals("")) {
				throw new Exception("[" + poClz.getName() + "." + _field.getName() + "] ColumnConfig.columnName Error!");
			}
			if (columnType == null) {
				throw new Exception("[" + poClz.getName() + "." + _field.getName() + "] ColumnConfig.columnType Error!");
			}
			String javaType = getJavaNameAndImport(columnType.getJavaType(), javaNameMap, importList);
			String tmp = "";
			if (columnConfig.primaryKeyFlag()) {
				tmp = "    @" + resultType + "(property = \"" + propertyName.trim() + "\", column = \"" + columnName.trim() + "\", id = true, javaType = " + javaType + ".class, jdbcType = " + jdbcType + "." + columnType.getMybatisType() + ")";
			} else {
				tmp = "    @" + resultType + "(property = \"" + propertyName.trim() + "\", column = \"" + columnName.trim() + "\", javaType = " + javaType + ".class, jdbcType = " + jdbcType + "." + columnType.getMybatisType() + ")";
			}
			
			if (i < length - 1) {
				tmp += ",";
			}
			annotationList.add(tmp);
		}
		annotationList.add("})");
		
		// 3.sqlprovider
		String selectType = getJavaNameAndImport("org.apache.ibatis.annotations.Select", javaNameMap, importList);
		String tableName = tableConfig.tableName();
		annotationList.add("@" + selectType + "(\"SELECT * FROM `" + tableName + "` WHERE `id` = #{id}\")");
	}
	
	/**
	 * +拼装查询列表的sql语句
	 * @param annotationList annotationList
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @param prefix prefix
	 * @param resultMapName resultMapName
	 */
	private void makeQueryListAnnotation(List<String> annotationList, Map<String, String> javaNameMap, 
			List<String> importList, String prefix, String resultMapName) {
		// 1.options
		String optionType =  getJavaNameAndImport("org.apache.ibatis.annotations.Options", javaNameMap, importList);
		annotationList.add(makeOptionAnnotation(true, false, false, optionType, true));
		// 2.sqlprovider
		String providerType = getJavaNameAndImport("org.apache.ibatis.annotations.SelectProvider", javaNameMap, importList);
		String sqlProvider = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".dao.sqlprovider." + prefix + "SqlProvider";
		String selectType = getJavaNameAndImport(sqlProvider, javaNameMap, importList);
		annotationList.add("@" + providerType + "(type = " + selectType + ".class, method = \"queryList\")");
		// 3.ResultMap
		String resultMapType =  getJavaNameAndImport("org.apache.ibatis.annotations.ResultMap", javaNameMap, importList);
		annotationList.add("@" + resultMapType + "(value = {\"" + resultMapName + "\"})");
	}
}
