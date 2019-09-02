/**
 * 
 */
package com.github.biticcf.mountain.generator;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;

/**
 * author: Daniel.Cao
 * date:   2019年3月13日
 * time:   上午10:53:08
 *
 */
class ContextMetaGenerator extends GeneratorBase implements Generator {
	private String baseDir;
	
	public ContextMetaGenerator(String baseDir) {
		this.baseDir = baseDir;
	}
	
	@Override
	public List<FileMeta> generatorFileMeta(Project project, List<FileMeta> metaList, Integer type) throws Exception {
		List<FileMeta> fileMetaList = new ArrayList<>();
		
		FileMeta fileMeta = new FileMeta();
		// importList
		List<String> importList = new ArrayList<>();
		// <javaName, javaPacakge>
		Map<String, String> javaNameMap = new HashMap<>();
		
		fileMeta.setClassName("ConstantContext");
		fileMeta.setPreffix(null);
		fileMeta.setClassType(2);
		
		// superInterfaceList
		List<String> superInterfaceList = new ArrayList<>();
		String referContextName = "com.github.biticcf.mountain.core.common.service.ReferContext";
		String referContextType = getJavaNameAndImport(referContextName, javaNameMap, importList);
		superInterfaceList.add(referContextType);
		fileMeta.setSuperInterfaceList(superInterfaceList);
		
		String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".support";
		fileMeta.setPackageName(packageName);
		
		// null
		fileMeta.setGenericName(null);
		fileMeta.setParentClass(null);
		// null end
		
		// 检查已经存在的文件,取得原有成员变量
		List<Field> oldFiledList = new ArrayList<>();
		String baseService = baseDir.substring(0, baseDir.length() - MAVEN_MODEL_SERVER.length()) + MAVEN_MODEL_SERVER;
		String contextPath = baseService + "/target/classes/"+ MODEL_DIR_MAP.get(PROJECT_MODEL_DOMAIN) + "/support/ConstantContext.class";
		Class<?> oldContextClass = this.getClassUnsafe(contextPath);
		if (oldContextClass != null) {
			Field[] fields = oldContextClass.getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field field : fields) {
					if (field == null) {
						continue;
					}
					oldFiledList.add(field);
				}
			}
		}
		String autowiredType = getJavaNameAndImport("org.springframework.beans.factory.annotation.Autowired", javaNameMap, importList);
		// memberList 和 methodList
		List<String> memberList = new ArrayList<>();
		List<MethodMeta> methodList = new ArrayList<>();
		for (String facadeName : PO_ALL_NAME_MAP.keySet()) {
			String repositoryName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_DOMAIN ) + ".repository." + facadeName + "DomainRepository";
			String repositoryType = getJavaNameAndImport(repositoryName, javaNameMap, importList);
			int _idx = repositoryType.lastIndexOf(".");
			String repositoryNameShort = _idx < 0 ? repositoryType : repositoryType.substring(_idx + 1);
			repositoryNameShort = this.makePropertyName(repositoryNameShort);
			
			memberList.add("@" + autowiredType);
			memberList.add("private " + repositoryType + " " + repositoryNameShort);
			
			// methodList
			MethodMeta methodMeta = makeGetMethod(repositoryType, repositoryNameShort, javaNameMap, importList);
			methodList.add(methodMeta);
		}
		// 检查原有成员变量
		
		for (Field oldField : oldFiledList) {
			Type returnType = oldField.getGenericType();
			String memberType = getJavaNameAndImport(returnType.getTypeName(), javaNameMap, importList);
			String memberNameShort = oldField.getName();
			String _tmp = "private " + memberType + " " + memberNameShort;
			// 重复数据
			if (memberList.contains(_tmp)) {
				continue;
			}
			
			Autowired[] autowireds = oldField.getAnnotationsByType(Autowired.class);
			if (autowireds == null || autowireds.length <= 0) {
				// Value注解
				String valueValue = null;
				Value[] values = oldField.getAnnotationsByType(Value.class);
				if (values != null && values.length > 0) {
					Value value = values[0];
					if (value != null) {
						valueValue = value.value();
					}
				}
				if (valueValue != null && !valueValue.trim().equals("")) {
					String valueType = getJavaNameAndImport("org.springframework.beans.factory.annotation.Value", javaNameMap, importList);
					memberList.add("@" + valueType + "(\"" + valueValue.trim() + "\")");
				}
				memberList.add(_tmp);
			} else {
				// Qualifier注解
				String qualifierName = null;
				Qualifier[] qualifiers = oldField.getAnnotationsByType(Qualifier.class);
				if (qualifiers != null && qualifiers.length > 0) {
					Qualifier qualifier = qualifiers[0];
					if (qualifier != null) {
						qualifierName = qualifier.value();
					}
				}
				if (qualifierName != null && !qualifierName.trim().equals("")) {
					String qualifierType = getJavaNameAndImport("org.springframework.beans.factory.annotation.Qualifier", javaNameMap, importList);
					memberList.add("@" + autowiredType + "@" + qualifierType + "(\"" + qualifierName.trim() + "\")");
				} else {
					memberList.add("@" + autowiredType);
				}
				memberList.add(_tmp);
			}
			
			// methodList
			MethodMeta methodMeta = makeGetMethod(memberType, memberNameShort, javaNameMap, importList);
			methodList.add(methodMeta);
		}
		fileMeta.setMemberList(memberList);
		fileMeta.setMethodList(methodList);
		
		// headerContentList
		List<String> headerContentList = new ArrayList<>();
		headerContentList.add("/**");
		headerContentList.add(" * ConstantContext.java");
		headerContentList.add(" */");
		fileMeta.setHeaderContentList(headerContentList);
		
		// classContentList
		List<String> classContentList = generatorClassContent(null, "ConstantContext");
		fileMeta.setClassContentList(classContentList);
		
		// headerAnnotationList
		List<String> headerAnnotationList = new ArrayList<>();
		String serviceType = getJavaNameAndImport("org.springframework.stereotype.Service", javaNameMap, importList);
		headerAnnotationList.add("@" + serviceType + "(\"constantContext\")");
		fileMeta.setHeaderAnnotationList(headerAnnotationList);
		
		// importList
		sortImportList(importList);
		fileMeta.setImportList(importList);
		
		fileMetaList.add(fileMeta);
		
		return fileMetaList;
	}
	
	/**
	 * +自动生成Getter方法
	 * @param memberType memberType
	 * @param memberNameShort memberNameShort
	 * @param javaNameMap javaNameMap
	 * @param importList importList
	 * @return MethodMeta
	 */
	private MethodMeta makeGetMethod(String memberType, String memberNameShort, Map<String, String> javaNameMap, 
			List<String> importList) {
		MethodMeta method = new MethodMeta();
		
		String memberName = this.makePropertyName(memberNameShort);
		String methodName = "get" + memberName;
		String returnName = memberType;
		method.setMethodName(methodName);
		method.setReturnType(returnName);
		
		List<String> contentList = new ArrayList<>();
		List<String> bodyList = new ArrayList<>();
		contentList.add("/**");
		contentList.add(" * +获取对象");
		contentList.add(" * @return " + memberNameShort);
		contentList.add(" */");
		method.setContentList(contentList);
		
		// bodyList
		bodyList.add("return this." + memberNameShort + ";");
		method.setBodyList(bodyList);
		
		// null
		method.setParameterList(null);
		method.setAnnotationList(null);
		method.setExceptionList(null);
		method.setGenericReturnType(null);
		// null end
		
		return method;
	}
}
