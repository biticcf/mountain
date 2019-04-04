/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月12日
 * @Time:   上午12:18:57
 *
 */
public class MainGenerator extends GeneratorBase {
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
//		args = new String[] {
//				"dir=C:/User/workspaces_github/phoenix-mountain-platform.git/phoenix-mountain-server", 
//				"file=/src/generator/facades-define.xml"
//				};
		
		if (args == null || args.length < 2) {
			throw new Exception("[Arguments] Not Correct!");
		}
		
		MainGenerator mainGenerator = new MainGenerator();
		
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
		
		// 3.生成Po文件元文件
		List<FileMeta> poFileMetaList = new PoMetaGenerator().generatorFileMeta(project, null, null);
		
		// 4.生成Model文件元文件
		List<FileMeta> modelFileMetaList = new ModelMetaGenerator().generatorFileMeta(project, poFileMetaList, null);
		
		// 5,生成Facade接口定义文件元文件
		List<FileMeta> facadeFileMetaList = new FacadeMetaGenerator().generatorFileMeta(project, null, 1);
		
		// 6,生成Controller接口实现文件元文件
		List<FileMeta> controllerFileMetaList = new ControllerMetaGenerator().generatorFileMeta(project, facadeFileMetaList, 1);
		
		// 7,生成Service接口文件元文件
		List<FileMeta> serviceFileMetaList = new ServiceMetaGenerator().generatorFileMeta(project, controllerFileMetaList, null);
		
		// 8,生成Domain定义文件元文件
		List<FileMeta> domainFileMetaList = new DomainMetaGenerator().generatorFileMeta(project, serviceFileMetaList, null);
		
		// 9.生成DomainRepository定义文件元文件
		List<FileMeta> domainRepositoryFileMetaList = new DomainRepositoryMetaGenerator().generatorFileMeta(project, poFileMetaList, null);
		
		// 10.生成DaoRepository定义文件元文件
		List<FileMeta> daoFileMetaList = new DaoMetaGenerator().generatorFileMeta(project, domainRepositoryFileMetaList, null);
		
		// 11.生成SqlProvider定义文件元文件
		List<FileMeta> sqlProviderFileMetaList = new MapperMetaGenerator().generatorFileMeta(project, poFileMetaList, null);
		
		// 12.生成ConstantContext定义文件元文件
		List<FileMeta> contextFileMetaList = new ContextMetaGenerator().generatorFileMeta(project, facadeFileMetaList, null);
		
		System.out.println("xml解析成功!");
		
		// 7,使用元文件生成实际文件
		// 生成Po文件
		for (FileMeta fileMeta : poFileMetaList) {
			if (fileMeta.isReGenerator()) {
				String destDir = MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_DOMAIN) + "/dao/po";
				FileGeneratorUtils.generatorFile(fileMeta, destDir, true, 1);
			}
		}
		
		// 生成Model文件
		for (FileMeta fileMeta : modelFileMetaList) {
			if (fileMeta.isReGenerator()) {
				String destDir = MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_MODEL);
				FileGeneratorUtils.generatorFile(fileMeta, destDir, true, 1);
			}
		}
		
		// 生成Facade文件
		for (FileMeta fileMeta : facadeFileMetaList) {
			if (fileMeta.isReGenerator()) {
				FileGeneratorUtils.generatorFile(fileMeta, MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_FACADE), true, 2);
			}
		}
		
		// 生成Controller文件
		for (FileMeta fileMeta : controllerFileMetaList) {
			if (fileMeta.isReGenerator()) {
				FileGeneratorUtils.generatorFile(fileMeta, MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_CONTROLLER), true, 1);
			}
		}
		
		// 生成Service文件
		for (FileMeta fileMeta : serviceFileMetaList) {
			if (fileMeta.isReGenerator()) {
				FileGeneratorUtils.generatorFile(fileMeta, MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_SERVICE), true, 2);
			}
		}
		
		// 生成Domain文件
		for (FileMeta fileMeta : domainFileMetaList) {
			if (fileMeta.isReGenerator()) {
				FileGeneratorUtils.generatorFile(fileMeta, MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_DOMAIN), true, 1);
			}
		}
		
		// 生成DomainRepository文件
		for (FileMeta fileMeta : domainRepositoryFileMetaList) {
			if (fileMeta.isReGenerator()) {
				String destDir = MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_DOMAIN) + "/repository";
				FileGeneratorUtils.generatorFile(fileMeta, destDir, true, 1);
			}
		}
		
		// 生成DaoRepository文件
		for (FileMeta fileMeta : daoFileMetaList) {
			if (fileMeta.isReGenerator()) {
				String destDir = MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_DOMAIN) + "/dao";
				FileGeneratorUtils.generatorFile(fileMeta, destDir, true, 2);
			}
		}
		
		// 生成ConstantContext文件
		for (FileMeta fileMeta : contextFileMetaList) {
			if (fileMeta.isReGenerator()) {
				String destDir = MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_DOMAIN) + "/support";
				FileGeneratorUtils.generatorFile(fileMeta, destDir, true, 1);
			}
		}
		
		// 生成SqlProvider文件
		for (FileMeta fileMeta : sqlProviderFileMetaList) {
			if (fileMeta.isReGenerator()) {
				String destDir = MODEL_ALL_DIR_MAP.get(PROJECT_MODEL_DOMAIN) + "/dao/sqlprovider";
				FileGeneratorUtils.generatorFile(fileMeta, destDir, true, 1);
			}
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
		checkFacades(facades, baseDir);
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
	private void checkFacades(List<Facade> facades, String baseDir) throws Exception {
		if (facades == null || facades.isEmpty()) {
			throw new Exception("[<facades>] Cannot Empty!");
		}
		
		for (Facade _facade : facades) {
			// 查找Facade文件
			String baseApi = baseDir.substring(0, baseDir.length() - MAVEN_MODEL_SERVER.length()) + MAVEN_MODEL_API;
			String facadePath = baseApi + "/target/classes/"+ MODEL_DIR_MAP.get(PROJECT_MODEL_FACADE) + "/" + _facade.getName() + "Facade.class";
			System.out.println("开始查找Facade[" + facadePath + "]...");
			
			Class<?> facadeClass = this.getClassUnsafe(facadePath);
			if (facadeClass == null) {
				throw new Exception("[Facade[" + facadePath + "]] Cannot Found!");
			}
			System.out.println("查找到Facade[" + facadePath + "].");
			
			// init
			initFacade(_facade, facadeClass);
			
			// 检查po文件
			if (_facade.getDaoCodeFlag() != null && _facade.getDaoCodeFlag()) {
				checkPoFile(_facade, baseDir);
			}
			
			// annotations
			List<String> annotations = findAnnotations(facadeClass);
			if (annotations != null && !annotations.isEmpty()) {
				_facade.setAnnotations(annotations);
			}
			
			// requestMapping可空
			org.springframework.web.bind.annotation.RequestMapping rm = facadeClass.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
			RequestMapping requestMapping = findRequestMapping(rm);
			if (requestMapping != null) {
				_facade.setRequestMapping(requestMapping);
			}
			
			// methods非空
			List<Method> methods = findMethods(facadeClass);
			if (methods == null || methods.isEmpty()) {
				throw new Exception("[facades.facade.name[" + _facade.getName() + "]] Cannot find mothods!");
			}
			_facade.setMethods(methods);
		}
	}
}
