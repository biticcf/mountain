/**
 * 
 */
package com.github.biticcf.mountain.generator;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * author: Daniel.Cao
 * date:   2019年1月13日
 * time:   上午10:27:46
 *
 */
class FileGeneratorUtils {
	/**
	 * 
	 * @param fileMeta
	 * @param destDir
	 * @param needBck
	 * @param clzType 1-类,2-接口
	 * @throws Exception
	 */
	public static void generatorFile(FileMeta fileMeta, String destDir, boolean needBck, int clzType) throws Exception {
		if (fileMeta == null) {
			return;
		}
		String className = fileMeta.getClassName();
		
		String fileName = className + ".java";
		File destFile = new File(destDir + "/" + fileName);
		
		if (needBck) {
			if (destFile.exists()) {
				String bakFileName = destDir + "/" + fileName + "." + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
				destFile.renameTo(new File(bakFileName));
			}
		}
		
		try (OutputStreamWriter bw = new OutputStreamWriter(new FileOutputStream(destFile, false), "UTF-8")) {
			
			// 文件头注释列表,每个值一行代码
			List<String> headerContentList = fileMeta.getHeaderContentList();
			if(headerContentList != null && !headerContentList.isEmpty()) {
				for (String _s : headerContentList) {
					bw.write(_s);
					writeNewLine(bw);
				}
			}
			
			// 所属包名
			String packageName = fileMeta.getPackageName();
			bw.write("package " + packageName + ";");
			writeNewLine(bw, 3);
			
			// 导入包列表
			List<String> importList = fileMeta.getImportList();
			for (String _s : importList) {
				if (_s == null || _s.trim().equals("")) {
					writeNewLine(bw);
				} else {
					bw.write("import " + _s + ";");
					writeNewLine(bw);
				}
			}
			writeNewLine(bw);
			
			// 类注释列表,每个值一行代码
			List<String> classContentList = fileMeta.getClassContentList();
			if(classContentList != null && !classContentList.isEmpty()) {
				for (String _s : classContentList) {
					bw.write(_s);
					writeNewLine(bw);
				}
				writeNewLine(bw);
			}
			
			// 文件头注解列表
			List<String> headerAnnotationList = fileMeta.getHeaderAnnotationList();
			if(headerAnnotationList != null && !headerAnnotationList.isEmpty()) {
				for (String _s : headerAnnotationList) {
					bw.write(_s);
					writeNewLine(bw);
				}
			}
			
			// 类的开头行
			String firstLine = makeClassFirstLine(fileMeta);
			bw.write(firstLine);
			writeNewLine(bw);
			
			// body-类成员列表
			List<String> memberList = fileMeta.getMemberList();
			if(memberList != null && !memberList.isEmpty()) {
				for (String _s : memberList) {
					if (_s == null || _s.trim().equals("")) {
						bw.write("    ");
					} else if (_s.startsWith("@")) { //注解
						bw.write("    " + _s);
					} else {
						bw.write("    " + _s + ";");
					}
					writeNewLine(bw);
				}
				writeNewLine(bw, 2);
			}
			
			// body-方法列表
			List<MethodMeta> methodList = fileMeta.getMethodList();
			if(methodList != null && !methodList.isEmpty()) {
				for (MethodMeta _methodMeta : methodList) {
					String methodName = _methodMeta.getMethodName();
					String returnType = _methodMeta.getReturnType();
					
					// 注释列表,每个值一行代码
					List<String> contentList = _methodMeta.getContentList();
					if (contentList != null && !contentList.isEmpty()) {
						for (String _s : contentList) {
							bw.write("    " + _s);
							writeNewLine(bw);
						}
					}
					
					// 方法上的注解列表
					List<String> annotationList = _methodMeta.getAnnotationList();
					if (annotationList != null && !annotationList.isEmpty()) {
						for (String _s : annotationList) {
							bw.write("    " + _s);
							writeNewLine(bw);
						}
					}
					
					// 参数列表
					List<String> parameterList = _methodMeta.getParameterList();
					// 抛出异常列表
					List<String> exceptionList = _methodMeta.getExceptionList();
					// 方法体,每个值一行代码
					List<String> bodyList = _methodMeta.getBodyList();
					
					// 方法体第一行
					String methodFirstLine = "";
					if (clzType == 2) { // 接口内的方法省略public
						methodFirstLine = "    ";
					} else {
						methodFirstLine = "    public ";
					}
					if (returnType != null) {
						methodFirstLine += returnType + " ";
					}
					methodFirstLine += methodName;
					methodFirstLine += "(";
					String paramString = "";
					if (parameterList != null && !parameterList.isEmpty()) {
						for (String _s : parameterList) {
							paramString += ", " + _s;
						}
						if (paramString.startsWith(", ")) {
							paramString = paramString.substring(2);
						}
					}
					methodFirstLine += paramString;
					methodFirstLine += ")";
					String exString = "";
					if (exceptionList != null && !exceptionList.isEmpty()) {
						for (String _s : exceptionList) {
							exString += ", " + _s;
						}
						if (exString.startsWith(", ")) {
							exString = exString.substring(2);
						}
					}
					if (!exString.equals("")) {
						methodFirstLine += " throws " + exString;
					}
					if (bodyList != null && !bodyList.isEmpty()) {
						methodFirstLine += " {";
					} else {
						methodFirstLine += ";";
					}
					bw.write(methodFirstLine);
					writeNewLine(bw);
					
					// bodyList
					if (bodyList != null && !bodyList.isEmpty()) {
						for (String _s : bodyList) {
							bw.write("        " + _s);
							writeNewLine(bw);
						}
						
						bw.write("    }");
						writeNewLine(bw, 2);
					}
				}
			}
			
			bw.write("}");
			writeNewLine(bw);
		}
		
		System.out.println("生成文件[" + destFile.getName() + "]成功.");
	}
	
	private static String makeClassFirstLine(FileMeta fileMeta) throws Exception {
		//类的类型,1-接口,2-类,3-抽象类
		int classType = fileMeta.getClassType();
		
		if (classType == 1) {
			return makeInterfaceFirstLine(fileMeta);
		}
		
		if (classType == 2 || classType == 3) {
			return makeClzFirstLine(fileMeta);
		}
		
		throw new Exception("Error File Type!");
	}
	
	private static String makeInterfaceFirstLine(FileMeta fileMeta) {
		List<String> superInterfaceList = fileMeta.getSuperInterfaceList();
		
		String line = "public interface " + fileMeta.getClassName();
		String superString = "";
		if (superInterfaceList != null && !superInterfaceList.isEmpty()) {
			for (String _s : superInterfaceList) {
				superString += _s + ", ";
			}
			if (superString.endsWith(", ")) {
				superString = superString.substring(0, superString.length() - 2);
			}
			
			line += " extends " + superString;
		}
		line += " {";
		
		return line;
	}
	private static String makeClzFirstLine(FileMeta fileMeta) {
		String parentClass = fileMeta.getParentClass();
		List<String> superInterfaceList = fileMeta.getSuperInterfaceList();
		String genericName = fileMeta.getGenericName();
		
		String line = "public";
		if (fileMeta.getClassType() == 3) {
			line += " abstract";
		}
		line += " class " + fileMeta.getClassName();
		if (parentClass != null && !parentClass.trim().equals("")) {
			line += " extends " + parentClass.trim();
			if (genericName != null && !genericName.trim().equals("")) {
				line += "<" + genericName.trim() + ">";
			}
		}
		String superString = "";
		if (superInterfaceList != null && !superInterfaceList.isEmpty()) {
			for (String _s : superInterfaceList) {
				superString += _s + ", ";
			}
			if (superString.endsWith(", ")) {
				superString = superString.substring(0, superString.length() - 2);
			}
			
			line += " implements " + superString;
		}
		
		line += " {";
		
		return line;
	}
	
	private static void writeNewLine(Writer writer, int count) throws Exception {
		if(writer instanceof BufferedWriter) {
			BufferedWriter br = (BufferedWriter) writer;
			
			for (int i = 0; i < count; i++) {
				br.newLine();
			}
			
			return;
		}
		
		if (writer instanceof OutputStreamWriter) {
			OutputStreamWriter osw = (OutputStreamWriter) writer;
			
			for (int i = 0; i < count; i++) {
				osw.write("\n");
			}
			
			return;
		}
	}
	
	private static void writeNewLine(Writer writer) throws Exception {
		writeNewLine(writer, 1);
	}
}
