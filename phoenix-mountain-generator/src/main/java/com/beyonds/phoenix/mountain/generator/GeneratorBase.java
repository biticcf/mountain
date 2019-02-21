/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月13日
 * @Time:   上午11:49:48
 *
 */
abstract class GeneratorBase {
	// 组件基础路径
	public static final String PLUGIN_BASE_DIR = "com.beyonds.phoenix.mountain";
	
	// maven model(Maven模块)
	public static final String MAVEN_MODEL_API = "api";
	public static final String MAVEN_MODEL_SERVER = "server";
	
	// project model(项目业务模块)
	public static final String PROJECT_MODEL_MODEL = "model";
	public static final String PROJECT_MODEL_FACADE = "facade";
	public static final String PROJECT_MODEL_CONTROLLER = "controller";
	public static final String PROJECT_MODEL_SERVICE = "service";
	public static final String PROJECT_MODEL_DOMAIN = "domain";
	
	// Method定义
	public static final String[] SUPPORT_METHODS = new String[] {
			"GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS", "TRACE"
	};
	// ResuestType定义
	public static final String[] SUPPORT_REQUEST_TYPE = new String[] {
			"RequestParam", "PathVariable", "RequestBody", "RequestHeader"
	};
	
	// 相对路径
	public static final Map<String, String> MODEL_DIR_MAP = new HashMap<>();
	// 包名
	public static final Map<String, String> MODEL_PACKAGE_MAP = new HashMap<>();
	// 完整路径
	public static final Map<String, String> MODEL_ALL_DIR_MAP = new HashMap<>();
	
	protected List<String> generatorClassContent(String message, String defaultMsg){
		List<String> classContentList = new ArrayList<>();
		
		String _msg = message;
		if (isEmpty(_msg)) {
			_msg = defaultMsg;
		}
		
		DateFormat dateD = new SimpleDateFormat("yyyy-MM-dd");
		DateFormat dateT = new SimpleDateFormat("HH:mm:ss");
		Date date = new Date();
		classContentList.add("/**");
		classContentList.add(" * @Author: DanielCao");
		classContentList.add(" * @Date:   " + dateD.format(date));
		classContentList.add(" * @Time:   " + dateT.format(date));
		classContentList.add(" * +" + _msg);
		classContentList.add(" */");
		
		return classContentList;
	}
	
	protected String getJavaNameAndImport(String javaType, Map<String, String> javaNameMap, List<String> importList) {
		if (isEmpty(javaType)) {
			return null;
		}
		// 无泛型
		if (javaType.indexOf("<") < 0) {
			return getJavaNameAndImportSimple(javaType, javaNameMap, importList);
		}
		
		// 有泛型
		String[] ctx = javaType.split("[<>,]");
		List<String> tmpList = new ArrayList<>();
		int startIndex = 0, endIndex = 0, count = 0;
		for (String _s : ctx) {
			startIndex = endIndex + count;
			endIndex = startIndex + _s.length();
			tmpList.add(_s);
			int _idx = endIndex;
			String tmpS = Character.toString(javaType.charAt(_idx));
			count = 0;
			while (tmpS.equals("<") || tmpS.equals(">") || tmpS.equals(",")) {
				tmpList.add(tmpS);
				
				count ++;
				_idx ++;
				if (_idx >= javaType.length()) {
					break;
				}
				tmpS = Character.toString(javaType.charAt(_idx));
			}
		}
		String result = "";
		for (String _s : tmpList) {
			if (_s.equals("<") || _s.equals(">") || _s.equals(",")) {
				result += _s;
				if (_s.equals(",")) {
					result += " ";
				}
				continue;
			}
			
			result += getJavaNameAndImportSimple(_s.trim(), javaNameMap, importList);
		}
		
		
		return result;
	}
	private String getJavaNameAndImportSimple(String javaType, Map<String, String> javaNameMap, List<String> importList) {
		int index = javaType.lastIndexOf(".");
		if (index < 0) {
			addToImport(javaType, importList);
			
			return javaType;
		}
		
		String javaName = javaType.substring(index + 1);
		String javaPackage = javaType.substring(0, index);
		if (!javaNameMap.containsKey(javaName)) {
			javaNameMap.put(javaName, javaPackage);
			addToImport(javaType, importList);
			
			return javaName;
		}
		
		String _oldJavaPackage = javaNameMap.get(javaName);
		if (javaPackage.equals(_oldJavaPackage)) {
			addToImport(javaType, importList);
			
			return javaName;
		}
		
		// 如果是全路径限定,不需要加入importList
		return javaType;
	}
	
	private void addToImport(String javaType, List<String> importList) {
		if (importList.contains(javaType) || javaType.startsWith("java.lang")) {
			return;
		}
		
		importList.add(javaType);
	}
	
	protected String makePropertyName(String srcName) { //更换第一个字母的大小写
		if (isEmpty(srcName)) {
			return srcName;
		}
		
		char firstChar = srcName.charAt(0);
		char secondChar = firstChar;
		if (Character.isLowerCase(firstChar)) {
			secondChar = Character.toUpperCase(firstChar);
		} else if (Character.isUpperCase(firstChar)) {
			secondChar = Character.toLowerCase(firstChar);
		}
		
		return Character.toString(secondChar) + srcName.substring(1);
	}
	
	protected void sortImportList(List<String> importList) {
		List<String> javaList = new ArrayList<>(); // java.开头的import
		List<String> javaxList = new ArrayList<>(); // javax.开头的import
		List<String> springList = new ArrayList<>(); // org.springframework.开头的import
		List<String> orgList = new ArrayList<>(); // 其他org.开头的import
		List<String> comList = new ArrayList<>(); // 其他com.开头的import
		List<String> ioList = new ArrayList<>(); // 其他io.开头的import
		List<String> otherList = new ArrayList<>(); // 其他开头的import
		
		for (String _s : importList) {
			if (_s.startsWith("java.")) {
				javaList.add(_s);
			} else if (_s.startsWith("javax.")) {
				javaxList.add(_s);
			} else if (_s.startsWith("org.springframework.")) {
				springList.add(_s);
			} else if (_s.startsWith("org.")) {
				orgList.add(_s);
			} else if (_s.startsWith("com.")) {
				comList.add(_s);
			} else if (_s.startsWith("io.")) {
				ioList.add(_s);
			} else {
				otherList.add(_s);
			}
		}
		
		importList.clear();
		if (!javaList.isEmpty()) {
			Collections.sort(javaList);
			importList.addAll(javaList);
		}
		if (!javaxList.isEmpty()) {
			Collections.sort(javaxList);
			importList.add("");
			importList.addAll(javaxList);
		}
		if (!springList.isEmpty()) {
			Collections.sort(springList);
			importList.add("");
			importList.addAll(springList);
		}
		if (!orgList.isEmpty()) {
			Collections.sort(orgList);
			importList.add("");
			importList.addAll(orgList);
		}
		if (!comList.isEmpty()) {
			Collections.sort(comList);
			importList.add("");
			importList.addAll(comList);
		}
		if (!ioList.isEmpty()) {
			Collections.sort(ioList);
			importList.add("");
			importList.addAll(ioList);
		}
		if (!otherList.isEmpty()) {
			Collections.sort(otherList);
			importList.add("");
			importList.addAll(otherList);
		}
	}
	
	protected boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		
		return "".equals(str.trim());
	}
}
