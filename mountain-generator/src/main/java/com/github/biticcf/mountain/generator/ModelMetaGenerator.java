/**
 * 
 */
package com.github.biticcf.mountain.generator;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.github.biticcf.mountain.generator.annotation.TableConfig;

/**
 * author: Daniel.Cao
 * date:   2019年3月13日
 * time:   上午10:53:08
 *
 */
class ModelMetaGenerator extends GeneratorBase implements Generator {
	
	@Override
	public List<FileMeta> generatorFileMeta(Project project, List<FileMeta> metaList, Integer type) throws Exception {
		List<FileMeta> fileMetaList = new ArrayList<>();
		
		for (String facadeName : PO_ALL_NAME_MAP.keySet()) {
			Class<?> poClz = PO_ALL_NAME_MAP.get(facadeName);
			TableConfig[] tableConfigs = poClz.getAnnotationsByType(TableConfig.class);
			TableConfig tableConfig = null;
			if (tableConfigs != null && tableConfigs.length > 0) {
				tableConfig = tableConfigs[0];
			}
			if (tableConfig == null || !tableConfig.reGeneratorModel()) {
				continue;
			}
			
			FileMeta fileMeta = new FileMeta();
			// importList
			List<String> importList = new ArrayList<>();
			// <javaName, javaPacakge>
			Map<String, String> javaNameMap = new HashMap<>();
			
			fileMeta.setClassName(facadeName + "Model");
			fileMeta.setPreffix(facadeName);
			fileMeta.setClassType(2);
			
			String packageName = MODEL_PACKAGE_MAP.get(PROJECT_MODEL_MODEL);
			fileMeta.setPackageName(packageName);
			
			Class<?> supperClz = poClz.getSuperclass();
			if (supperClz != null) {
				if (!"java.lang.Object".equals(supperClz.getName())) {
					String parentClass = this.getJavaNameAndImport(supperClz.getName(), javaNameMap, importList);
					fileMeta.setParentClass(parentClass);
				}	
			}
			
			List<String> memberList = new ArrayList<>();
			List<MethodMeta> methodList = new ArrayList<>();
			Field[] fields = poClz.getDeclaredFields();
			if (fields != null && fields.length > 0) {
				for (Field _fd : fields) {
					String fdName = _fd.getName();
					Class<?> fdType = _fd.getType();
					if (fdName.equals("serialVersionUID")) {
						memberList.add("private static final long serialVersionUID = " + new Random().nextLong() + "L");
						memberList.add("");
						continue;
					}
					
					String fdTypeName = this.getJavaNameAndImport(fdType.getName(), javaNameMap, importList);
					memberList.add("private " + fdTypeName + " " + fdName);
					
					makeGetterSetter(fdTypeName, fdName, methodList);
				}
				fileMeta.setMethodList(methodList);
				fileMeta.setMemberList(memberList);
			}
			
			// headerContentList
			List<String> headerContentList = new ArrayList<>();
			headerContentList.add("/**");
			headerContentList.add(" * " + facadeName + "Model.java");
			headerContentList.add(" */");
			fileMeta.setHeaderContentList(headerContentList);
			
			// classContentList
			List<String> classContentList = generatorClassContent(null, facadeName + "Model");
			fileMeta.setClassContentList(classContentList);
			
			sortImportList(importList);
			fileMeta.setImportList(importList);
			
			fileMetaList.add(fileMeta);
		}
		
		return fileMetaList;
	}
	
	/**
	 * 生成getter、setter方法
	 * @param fdTypeName
	 * @param fdName
	 * @param methodList
	 */
	private void makeGetterSetter(String fdTypeName, String fdName, List<MethodMeta> methodList) {
		// GET
		MethodMeta getterMethod = new MethodMeta();
		String tmpName = Character.toUpperCase(fdName.charAt(0)) + fdName.substring(1);
		if (fdTypeName.equals("boolean") || fdTypeName.equals("java.lang.Boolean")) {
			getterMethod.setMethodName("is" + tmpName);
		} else {
			getterMethod.setMethodName("get" + tmpName);
		}
		getterMethod.setReturnType(fdTypeName);
		
		List<String> contentList = new ArrayList<>();
		contentList.add("/**");
		contentList.add(" * @return the " + fdName);
		contentList.add(" */");
		getterMethod.setContentList(contentList);
		
		List<String> bodyList = new ArrayList<>();
		bodyList.add("return this." + fdName + ";");
		getterMethod.setBodyList(bodyList);
		methodList.add(getterMethod);
		
		// SET
		MethodMeta setterMethod = new MethodMeta();
		setterMethod.setMethodName("set" + tmpName);
		
		List<String> parameterList = new ArrayList<>();
		parameterList.add(fdTypeName + " " + fdName);
		setterMethod.setParameterList(parameterList);
		
		List<String> contentList1 = new ArrayList<>();
		contentList1.add("/**");
		contentList1.add(" * @param " + fdName + " the " + fdName + " to set");
		contentList1.add(" */");
		setterMethod.setContentList(contentList1);
		setterMethod.setReturnType("void");
		
		List<String> bodyList1 = new ArrayList<>();
		bodyList1.add("this." + fdName + " = " + fdName + ";");
		setterMethod.setBodyList(bodyList1);
		methodList.add(setterMethod);
	}
}
