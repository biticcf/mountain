/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.springframework.http.MediaType;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月12日
 * @Time:   上午12:18:57
 *
 */
public class MainReactorGenerator extends GeneratorBase {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		//args = new String[] {"dir=C:/User/workspaces_core/phoenix-mountain-platform.git/phoenix-mountain-server",
		//		"file=/src/generator/facades-define-min.xml"};
		
		if (args == null || args.length < 2) {
			throw new Exception("[Arguments] Not Correct!");
		}
		
		MainReactorGenerator mainGenerator = new MainReactorGenerator();
		
		// 1,加载配置文件
		String baseDir = null, file = null;
		for (String _s : args) {
			String[] _tmps = _s.split("=");
			if (_tmps.length != 2) {
				continue;
			}
			
			if (_tmps[0] == null || _tmps[0].trim().equals("") 
			 || _tmps[1] == null || _tmps[1].trim().equals("")) {
				continue;
			}
			
			if (_tmps[0].trim().equals("dir") && (!_tmps[1].trim().endsWith("-" + MAVEN_MODEL_SERVER))) {
				return;
			}
			
			if (_tmps[0].trim().equals("dir")) {
				baseDir = _tmps[1].trim();
			} else if (_tmps[0].trim().equals("file")) {
				file = _tmps[1].trim();
			}
		}
		
		if (baseDir == null || file == null) {
			throw new Exception("[Arguments] Not Correct!");
		}
		
		String srcXmlFilePath = baseDir + "/" + file;
		File srcFile = new File(srcXmlFilePath);
		if (!srcFile.exists() || srcFile.isDirectory()) {
			throw new Exception("[" + file + "] Not Found!");
		}
		XMLValidation.validateXMLSchema(srcXmlFilePath);
		String xmlString = FileUtils.readFileToString(srcFile, "UTF-8");
		Project project = XmlUtil.xmlToObject(xmlString, Project.class);
		
		// 2,校验配置文件
		mainGenerator.checkProject(project, file, baseDir);
		System.out.println("xml校验成功!");
		
		// 3,生成Facade接口定义文件元文件
		List<FileMeta> facadeFileMetaList = new FacadeMetaGenerator().generatorFileMeta(project, null, 2);
		
		// 4,生成Controller接口实现文件元文件
		List<FileMeta> controllerFileMetaList = new ControllerMetaGenerator().generatorFileMeta(project, facadeFileMetaList, 2);
		
		// 5,生成Service接口文件元文件
		List<FileMeta> serviceFileMetaList = new ServiceMetaGenerator().generatorFileMeta(project, controllerFileMetaList, null);
		
		// 6,生成Domain定义文件元文件
		List<FileMeta> domainFileMetaList = new DomainMetaGenerator().generatorFileMeta(project, serviceFileMetaList, null);
		
		System.out.println("xml解析成功!");
		
		// 7,使用元文件生成实际文件
		// 生成Facade文件
		for (FileMeta fileMeta : facadeFileMetaList) {
			FileGeneratorUtils.generatorFile(fileMeta, MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_FACADE), true);
		}
		
		// 生成Controller文件
		for (FileMeta fileMeta : controllerFileMetaList) {
			FileGeneratorUtils.generatorFile(fileMeta, MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_CONTROLLER), true);
		}
		
		// 生成Service文件
		for (FileMeta fileMeta : serviceFileMetaList) {
			FileGeneratorUtils.generatorFile(fileMeta, MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_SERVICE), true);
		}
		
		// 生成Domain文件
		for (FileMeta fileMeta : domainFileMetaList) {
			FileGeneratorUtils.generatorFile(fileMeta, MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_DOMAIN), true);
		}
		
		System.out.println("xml生成成功!");
	}
	
	// 2,校验配置文件
	private void checkProject(Project project, String file, String baseDir) throws Exception {
		if (project == null) {
			throw new Exception("[" + file + "] Not Correct!");
		}
		// 1,检查Properties属性
		Properties properties = project.getProperties();
		checkProperties(properties);
		
		// 2,检查models属性
		List<Model> models = project.getModels();
		checkModels(models, baseDir);
		
		// 3,检查facades属性
		List<Facade> facades = project.getFacades();
		checkFacades(facades);
	}
	// 2.1,检查Properties属性
	private void checkProperties(Properties properties) throws Exception {
		if (properties == null) {
			throw new Exception("[<Properties>] Cannot Empty!");
		}
		// 1,name项目名称
		String name = properties.getName();
		if (isEmpty(name)) {
			throw new Exception("[Properties.name] Cannot Empty!");
		}
		properties.setName(name.trim());
		
		// 2,company
		String company = properties.getCompany();
		if (company == null || !company.trim().matches("^[a-z][a-z0-9]*$")) {
			throw new Exception("[Properties.company] Cannot Empty Or Not Correct!");
		}
		properties.setCompany(company.toString());
		
		// 3,scope
		String scope = properties.getScope();
		if (scope == null || !scope.trim().matches("^[a-z][a-z0-9]*$")) {
			throw new Exception("[Properties.scope] Cannot Empty Or Not Correct!");
		}
		properties.setScope(scope.trim());
		
		// 4,template
		String template = properties.getTemplate();
		if (template == null || !template.trim().matches("^[a-z][a-z0-9]*$")) {
			throw new Exception("[Properties.template] Cannot Empty Or Not Correct!");
		}
		properties.setTemplate(template.trim());
		
		// 5,override
		Boolean override = properties.getOverride();
		properties.setOverride(override == null ? false : override);
		
		// 6,dirs
		List<Dir> dirs = properties.getDirs();
		String modelDir = null, facadeDir = null, controllerDir = null, serviceDir = null, domainDir = null;
		if (dirs != null && !dirs.isEmpty()) {
			for (Dir _dir : dirs) {
				if (PROJECT_MODEL_MODEL.equals(_dir.getName()) && !isEmpty(_dir.getValue())) {
					modelDir = _dir.getValue().trim();
				} else if (PROJECT_MODEL_FACADE.equals(_dir.getName()) && !isEmpty(_dir.getValue())) {
					facadeDir = _dir.getValue().trim();
				} else if (PROJECT_MODEL_CONTROLLER.equals(_dir.getName()) && !isEmpty(_dir.getValue())) {
					controllerDir = _dir.getValue().trim();
				} else if (PROJECT_MODEL_SERVICE.equals(_dir.getName()) && !isEmpty(_dir.getValue())) {
					serviceDir = _dir.getValue().trim();
				} else if (PROJECT_MODEL_DOMAIN.equals(_dir.getName()) && !isEmpty(_dir.getValue())) {
					domainDir = _dir.getValue().trim();
				}
			}
		}
		if (modelDir == null) {
			modelDir = "com/" + company + "/" + scope + "/" + template + "/" + PROJECT_MODEL_MODEL;
		}
		MODEL_DIR_MAP.put(PROJECT_MODEL_MODEL, modelDir);
		MODEL_PACKAGE_MAP.put(PROJECT_MODEL_MODEL, modelDir.replaceAll("/", "."));
		
		if (facadeDir == null) {
			facadeDir = "com/" + company + "/" + scope + "/" + template + "/" + PROJECT_MODEL_FACADE;
		}
		MODEL_DIR_MAP.put(PROJECT_MODEL_FACADE, facadeDir);
		MODEL_PACKAGE_MAP.put(PROJECT_MODEL_FACADE, facadeDir.replaceAll("/", "."));
		
		if (controllerDir == null) {
			controllerDir = "com/" + company + "/" + scope + "/" + template + "/web/" + PROJECT_MODEL_CONTROLLER;
		}
		MODEL_DIR_MAP.put(PROJECT_MODEL_CONTROLLER, controllerDir);
		MODEL_PACKAGE_MAP.put(PROJECT_MODEL_CONTROLLER, controllerDir.replaceAll("/", "."));
		
		if (serviceDir == null) {
			serviceDir = "com/" + company + "/" + scope + "/" + template + "/" + PROJECT_MODEL_SERVICE;
		}
		MODEL_DIR_MAP.put(PROJECT_MODEL_SERVICE, serviceDir);
		MODEL_PACKAGE_MAP.put(PROJECT_MODEL_SERVICE, serviceDir.replaceAll("/", "."));
		
		if (domainDir == null) {
			domainDir = "com/" + company + "/" + scope + "/" + template + "/" + PROJECT_MODEL_DOMAIN;
		}
		MODEL_DIR_MAP.put(PROJECT_MODEL_DOMAIN, domainDir);
		MODEL_PACKAGE_MAP.put(PROJECT_MODEL_DOMAIN, domainDir.replaceAll("/", "."));
	}
	// 2.2,检查models属性
	private void checkModels(List<Model> models, String baseDir) throws Exception {
		String modelDir = null, facadeDir = null, controllerDir = null, serviceDir = null, domainDir = null;
		if (models != null && !models.isEmpty()) {
			List<String> modelNameList = new ArrayList<>();
			List<String> dirRefList = new ArrayList<>();
			for (Model _model : models) {
				String name = _model.getName();
				if (isEmpty(name) || !name.trim().matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
					throw new Exception("[models.model.name] Cannot Empty Or Not Correct!");
				}
				name = name.trim();
				List<String> includeDirs = _model.getIncludeDirs();
				if (includeDirs == null || includeDirs.isEmpty()) {
					throw new Exception("[models.model.includeDirs] Cannot Empty!");
				}
				if (modelNameList.contains(name)) {
					throw new Exception("[models.model.name] Cannot Duplicated!");
				}
				modelNameList.add(name);
				String _baseDir = baseDir.substring(0, baseDir.length() - PROJECT_MODEL_SERVICE.length() + 1) + name + "/src/main/java";
				
				for (String _includeDir : includeDirs) {
					if (isEmpty(_includeDir) || !MODEL_DIR_MAP.containsKey(_includeDir.trim())) {
						throw new Exception("[models.model.includeDirs.dirRef] Cannot Empty Or Not Correct!");
					}
					_includeDir = _includeDir.trim();
					if (dirRefList.contains(_includeDir)) {
						throw new Exception("[models.model.includeDirs.dirRef] Cannot Duplicated!");
					}
					dirRefList.add(_includeDir);
					
					String _allDir = _baseDir + "/" + MODEL_DIR_MAP.get(_includeDir);
					if (PROJECT_MODEL_MODEL.equals(_includeDir)) {
						modelDir = _allDir;
					} else if (PROJECT_MODEL_FACADE.equals(_includeDir)) {
						facadeDir = _allDir;
					} else if (PROJECT_MODEL_CONTROLLER.equals(_includeDir)) {
						controllerDir = _allDir;
					} else if (PROJECT_MODEL_SERVICE.equals(_includeDir)) {
						serviceDir = _allDir;
					} else if (PROJECT_MODEL_DOMAIN.equals(_includeDir)) {
						domainDir = _allDir;
					}
				}
			}
		}
		
		String baseApi = baseDir.substring(0, baseDir.length() - MAVEN_MODEL_SERVER.length()) + MAVEN_MODEL_API;
		String baseServer = baseDir.substring(0, baseDir.length() - MAVEN_MODEL_SERVER.length()) + MAVEN_MODEL_SERVER;
		if (modelDir == null) {
			modelDir = baseApi + "/src/main/java/" + MODEL_DIR_MAP.get(PROJECT_MODEL_MODEL);
		}
		MODEL_ALL_DIR_MAP.put(PROJECT_MODEL_MODEL, modelDir);
		
		if (facadeDir == null) {
			facadeDir = baseApi + "/src/main/java/" + MODEL_DIR_MAP.get(PROJECT_MODEL_FACADE);
		}
		MODEL_ALL_DIR_MAP.put(PROJECT_MODEL_FACADE, facadeDir);
		
		if (controllerDir == null) {
			controllerDir = baseServer + "/src/main/java/" + MODEL_DIR_MAP.get(PROJECT_MODEL_CONTROLLER);
		}
		MODEL_ALL_DIR_MAP.put(PROJECT_MODEL_CONTROLLER, controllerDir);
		
		if (serviceDir == null) {
			serviceDir = baseServer + "/src/main/java/" + MODEL_DIR_MAP.get(PROJECT_MODEL_SERVICE);
		}
		MODEL_ALL_DIR_MAP.put(PROJECT_MODEL_SERVICE, serviceDir);
		
		if (domainDir == null) {
			domainDir = baseServer + "/src/main/java/" + MODEL_DIR_MAP.get(PROJECT_MODEL_DOMAIN);
		}
		MODEL_ALL_DIR_MAP.put(PROJECT_MODEL_DOMAIN, domainDir);
	}
	// 2.3,检查facades属性
	private void checkFacades(List<Facade> facades) throws Exception {
		if (facades == null || facades.isEmpty()) {
			throw new Exception("[<facades>] Cannot Empty!");
		}
		
		List<String> facadeNameList = new ArrayList<>();
		
		for (Facade _facade : facades) {
			// name
			String name = _facade.getName();
			if (isEmpty(name) || !name.trim().matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
				throw new Exception("[facades.facade.name[" + name + "]] Cannot Empty Or Not Correct!");
			}
			if (facadeNameList.contains(name)) {
				throw new Exception("[facades.facade.name[" + name + "]] Cannot Dumplicate!");
			}
			facadeNameList.add(name);
			_facade.setName(name.trim());
			
			// description
			// 不做检查
			
			// execGenerator
			Boolean execGenerator = _facade.getExecGenerator();
			_facade.setExecGenerator(execGenerator == null ? true : execGenerator);
			
			// useSwagger
			Boolean useSwagger = _facade.getUseSwagger();
			_facade.setUseSwagger(useSwagger == null ? false : useSwagger);
			
			// annotations确保填写完整路径
			List<String> annotations = _facade.getAnnotations();
			List<String> newAnnotations = new ArrayList<>();
			if (annotations != null && !annotations.isEmpty()) {
				for (String _annotation : annotations) {
					if (isEmpty(_annotation)) {
						continue;
					}
					// 忽略mvc注解和swagger注解
					if (_annotation.indexOf("org.springframework.web.bind.annotation") >= 0 
					 || _annotation.indexOf("io.swagger.annotations") >= 0) {
						continue;
					}
					if (_annotation.indexOf(".") <= 0) {
						throw new Exception("[facades.facade.annotations.annotation[" + _annotation + "]] Not Correct!");
					}
					if (!newAnnotations.contains(_annotation.trim())) {
						newAnnotations.add(_annotation.trim());
					}
				}
			}
			_facade.setAnnotations(newAnnotations);
			
			// requestMapping可空
			RequestMapping requestMapping = _facade.getRequestMapping();
			if (requestMapping != null) {
				checkRequestMapping(requestMapping);
			}
			
			// methods非空
			List<Method> methods = _facade.getMethods();
			checkMethods(methods);
		}
	}
	// 2.3.1,检查requestMapping
	private void checkRequestMapping(RequestMapping requestMapping) throws Exception {
		if (requestMapping == null) {
			throw new Exception("[facades.facade.methods.method.requestMapping] Cannot Empty!");
		}
		
		// requestPaths,非空
		List<String> requestPaths = requestMapping.getRequestPaths();
		if (requestPaths == null || requestPaths.isEmpty()) {
			throw new Exception("[requestMapping.requestPaths] Cannot Empty!");
		}
		
		// requestMethods,可空
		List<String> requestMethods = requestMapping.getRequestMethods();
		if (requestMethods != null && !requestMethods.isEmpty()) {
			List<String> newRequestMethods = new ArrayList<>();
			for (String _method : requestMethods) {
				if (isEmpty(_method)) {
					continue;
				}
				if (!isMethodValid(_method.trim())) {
					throw new Exception("[requestMapping.requestMethods.requestMethod[" + _method + "]] Not Correct!");
				}
				String _newMethod = _method.trim().toUpperCase();
				if (!newRequestMethods.contains(_newMethod)) {
					newRequestMethods.add(_newMethod);
				}
			}
			requestMapping.setRequestMethods(newRequestMethods);
		}
		
		// requestConsumes,可空
		List<String> requestConsumes = requestMapping.getRequestConsumes();
		if (requestConsumes != null && !requestConsumes.isEmpty()) {
			List<String> newRequestConsumes = new ArrayList<>();
			for (String _consume : requestConsumes) {
				if (isEmpty(_consume)) {
					continue;
				}
				MediaType mediaType = null;
				try {
					mediaType = MediaType.parseMediaType(_consume.trim());
				} catch (Exception e) {
					mediaType = null;
				}
				if (mediaType == null) {
					throw new Exception("[requestMapping.requestConsumes.requestConsume[" + _consume + "]] Not Correct!");
				}
				if (!newRequestConsumes.contains(_consume.trim())) {
					newRequestConsumes.add(_consume.trim());
				}
			}
			requestMapping.setRequestConsumes(newRequestConsumes);
		}
		
		// requestProduces,可空
		List<String> requestProduces = requestMapping.getRequestProduces();
		List<String> newRequestProduces = new ArrayList<>();
		if (requestProduces != null && !requestProduces.isEmpty()) {
			for (String _produce : requestProduces) {
				if (isEmpty(_produce)) {
					continue;
				}
				MediaType mediaType = null;
				try {
					mediaType = MediaType.parseMediaType(_produce.trim());
				} catch (Exception e) {
					mediaType = null;
				}
				if (mediaType == null) {
					throw new Exception("[requestMapping.requestProduces.requestProduce[" + _produce + "]] Not Correct!");
				}
				if (!newRequestProduces.contains(_produce.trim())) {
					newRequestProduces.add(_produce.trim());
				}
			}
		}
		if (newRequestProduces.isEmpty()) {
			newRequestProduces.add("application/json");
		}
		requestMapping.setRequestProduces(newRequestProduces);
		
		// requestHeaders,可空
		// 不做校验
		
		// requestParams,可空
		// 不做校验
	}
	// 2.3.1.1 检查Method是否合法
	private boolean isMethodValid(String method) {
		for (String _s : SUPPORT_METHODS) {
			if (_s.equalsIgnoreCase(method.trim())) {
				return true;
			}
		}
		
		return false;
	}
	// 2.3.2,检查methods
	private void checkMethods(List<Method> methods) throws Exception {
		if (methods == null || methods.isEmpty()) {
			throw new Exception("[facades.facade.methods] Cannot Empty!");
		}
		
		List<String> methodNameList = new ArrayList<>();
		for (Method _method : methods) {
			// name,非空
			String name = _method.getName();
			if (isEmpty(name) || !name.trim().matches("^[a-z][A-Za-z0-9]*$")) {
				throw new Exception("[facades.facade.methods.method.name[" + name + "]] Cannot Empty Or Not Correct!");
			}
			String _newName = name.trim().toLowerCase();
			if (methodNameList.contains(_newName)) {
				throw new Exception("[facades.facade.methods.method.name[" + name + "]] Cannot Dumplicate!");
			}
			methodNameList.add(_newName);
			_method.setName(name.trim());
			
			// description,可空
			// 不检查
			
			// execGenerator可空,默认true
			Boolean execGenerator = _method.getExecGenerator();
			_method.setExecGenerator(execGenerator == null ? true : execGenerator);
			
			// 返回值结果实际类型(不考虑ReturnResult的外层封装)
			String returnRealType = _method.getReturnRealType();
			if (isEmpty(returnRealType) || returnRealType.trim().indexOf(".") <= 0) {
				throw new Exception("[facades.facade.methods.method.returnRealType[" + returnRealType + "]] Cannot Empty Or Not Correct!");
			}
			_method.setReturnRealType(returnRealType.trim());
			
			// listResultFlag查询结果是否列表,可空,默认false
			Boolean listResultFlag = _method.getListResultFlag();
			_method.setListResultFlag(listResultFlag == null ? false : listResultFlag);
			
			// pagination查询结果是否分页,可空,默认false
			Boolean pagination = _method.getPagination();
			_method.setPagination(pagination == null? false : pagination);
			
			// withTransaction该方法涉及的业务是否需要开启事务,可空,默认true
			Boolean withTransaction = _method.getWithTransaction();
			_method.setWithTransaction(withTransaction == null ? true : withTransaction);
			
			// requestMapping,非空
			RequestMapping requestMapping = _method.getRequestMapping();
			checkRequestMapping(requestMapping);
			
			// exceptions,可空,默认是java.lang.Exception,需要填写完整java类名
			List<String> exceptions = _method.getExceptions();
			List<String> newExceptions = new ArrayList<>();
			if (exceptions != null && !exceptions.isEmpty()) {
				for (String _exception : exceptions) {
					if (isEmpty(_exception)) {
						continue;
					}
					if (_exception.trim().indexOf(".") <= 0) {
						throw new Exception("[facades.facade.methods.method.exceptions.exception[" + _exception + "]] Not Correct!");
					}
					if (!newExceptions.contains(_exception.trim())) {
						newExceptions.add(_exception.trim());
					}
				}
			}
			if (newExceptions.isEmpty()) {
				newExceptions.add("java.lang.Exception");
			}
			_method.setExceptions(newExceptions);
			
			// params可空
			List<Param> params = _method.getParams();
			checkParams(params);
		}
	}
	// 2.3.2.1,检查params可空
	private void checkParams(List<Param> params) throws Exception {
		if (params == null || params.isEmpty()) {
			return;
		}
		
		List<String> paramList = new ArrayList<>();
		for (Param _param : params) {
			// name
			String name = _param.getName();
			if (isEmpty(name) || !name.trim().matches("^[a-z][A-Za-z0-9]*$")) {
				throw new Exception("[facades.facade.methods.method.params.param.name[" + name + "]] Cannot Empty Or Not Correct!");
			}
			String _newName = name.trim().toLowerCase();
			if (paramList.contains(_newName)) {
				throw new Exception("[facades.facade.methods.method.params.param.name[" + name + "]] Cannot Dumplicate!");
			}
			paramList.add(_newName);
			
			_param.setName(name.trim());
			
			// type完整java类型路径,基础类型请用封装类型替代
			String type = _param.getType();
			if (isEmpty(type) || type.trim().indexOf(".") <= 0) {
				throw new Exception("[facades.facade.methods.method.params.param.type[" + type + "]] Cannot Empty Or Not Correct!");
			}
			_param.setType(type.trim());
			
			// description可空
			// 无需检查
			
			// nullable可空,默认false
			Boolean nullable = _param.getNullable();
			_param.setNullable(nullable == null ? false : nullable);
			
			// defaultValue可空
			// 不检查
			
			// requestType可空,默认RequestParam
			String requestType = _param.getRequestType();//SUPPORT_REQUEST_TYPE
			if (isEmpty(requestType)) {
				requestType = "RequestParam";
			} else {
				boolean checked = false;
				for (String _ss : SUPPORT_REQUEST_TYPE) {
					if (_ss.equalsIgnoreCase(requestType.trim())) {
						checked = true;
						requestType = _ss;
						break;
					}
				}
				if (!checked) {
					throw new Exception("[facades.facade.methods.method.params.param.requestType[" + requestType + "]] Not Correct!");
				}
			}
			_param.setRequestType(requestType);
			
			// annotations可空,参数前可能带的注解,不包含Mvc和Swagger注解,填写完整java路径
			List<String> annotations = _param.getAnnotations();
			List<String> newAnnotations = new ArrayList<>();
			if (annotations != null && !annotations.isEmpty()) {
				for (String annotation : annotations) {
					if (isEmpty(annotation)) {
						continue;
					}
					annotation = annotation.trim();
					// 忽略mvc注解和swagger注解
					if (annotation.indexOf("org.springframework.web.bind.annotation") >= 0 
					 || annotation.indexOf("io.swagger.annotations") >= 0) {
						continue;
					}
					if (annotation.indexOf(".") <= 0) {
						throw new Exception("[facades.facade.methods.method.params.param.annotations.annotation[" + annotation + "]] Not Correct!");
					}
					newAnnotations.add(annotation);
				}
			}
			_param.setAnnotations(newAnnotations);
		}
	}

}
