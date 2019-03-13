/**
 * 
 */
package com.beyonds.phoenix.mountain.generator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ValueConstants;

import com.beyonds.phoenix.mountain.generator.annotation.FacadeConfig;
import com.beyonds.phoenix.mountain.generator.annotation.MethodConfig;

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
			"RequestParam", "PathVariable", "RequestBody", 
			"RequestHeader", "RequestPart", "MatrixVariable"
	};
	
	// 相对路径
	public static final Map<String, String> MODEL_DIR_MAP = new HashMap<>();
	// 包名
	public static final Map<String, String> MODEL_PACKAGE_MAP = new HashMap<>();
	// 完整路径
	public static final Map<String, String> MODEL_ALL_DIR_MAP = new HashMap<>();
	
	protected void initFacade(Facade facade, Class<?> facadeClass) {
		FacadeConfig fc = facadeClass.getAnnotation(FacadeConfig.class);
		if (fc != null) {
			facade.setDescription(fc.description());
			facade.setExecGenerator(fc.execGenerator());
			facade.setUseSwagger(fc.useSwagger());
			facade.setReGenerator(fc.reGenerator());
			
			facade.setDaoCodeFlag(fc.genDaoCode());
			facade.setDefaultCreateFlag(fc.genDefaultCreate());
			facade.setDefaultDeleteFlag(fc.genDefaultDelete());
			facade.setDefaultUpdateFlag(fc.genDefaultUpdate());
			facade.setDefaultQueryItemFlag(fc.genDefaultQueryItem());
			facade.setDefaultQueryPagesFlag(fc.genDefaultQueryPages());
		} else {
			facade.setDescription(facade.getName() + "Facade接口定义说明");
			facade.setExecGenerator(false);
			facade.setUseSwagger(true);
			facade.setReGenerator(false);
			
			facade.setDaoCodeFlag(true);
			facade.setDefaultCreateFlag(false);
			facade.setDefaultDeleteFlag(false);
			facade.setDefaultUpdateFlag(false);
			facade.setDefaultQueryItemFlag(true);
			facade.setDefaultQueryPagesFlag(true);
		}
	}
	
	// 查找Facade类的Annotations
	protected List<String> findAnnotations(Class<?> facadeClz) {
		List<String> annotations = new ArrayList<>();
		
		Annotation[] annos = facadeClz.getAnnotations();
		if (annos != null && annos.length > 0) {
			for (Annotation _an : annos) {
				if (_an instanceof FacadeConfig || _an instanceof org.springframework.web.bind.annotation.RequestMapping) {
					continue;
				}
				
				annotations.add(_an.annotationType().getName());
			}
		}
		
		return annotations;
	}
	
	// 查找Facade类的RequestMapping
	protected RequestMapping findRequestMapping(org.springframework.web.bind.annotation.RequestMapping rm) {
		RequestMapping requestMapping = new RequestMapping();
		
		// requestPaths
		String[] paths = rm.path();
		if (paths == null || paths.length <= 0) {
			paths = rm.value();
		}
		List<String> requestPaths = new ArrayList<>();
		if (paths != null && paths.length > 0) {
			for (String _s : paths) {
				requestPaths.add(_s.trim());
			}
		}
		requestMapping.setRequestPaths(requestPaths);
		
		// requestMethods
		RequestMethod[] rmethods = rm.method();
		List<String> requestMethods = new ArrayList<>();
		if (rmethods != null && rmethods.length > 0) {
			for (RequestMethod _rmd : rmethods) {
				requestMethods.add(_rmd.name());
			}
		}
		requestMapping.setRequestMethods(requestMethods);
		
		// requestConsumes
		String[] consumes = rm.consumes();
		List<String> requestConsumes = new ArrayList<>();
		if (consumes != null && consumes.length > 0) {
			for (String _s : consumes) {
				requestConsumes.add(_s);
			}
		}
		requestMapping.setRequestConsumes(requestConsumes);
		
		// requestProduces
		String[] produces = rm.produces();
		List<String> requestProduces = new ArrayList<>();
		if (produces != null && produces.length > 0) {
			for (String _s : produces) {
				requestProduces.add(_s);
			}
		}
		requestMapping.setRequestProduces(requestProduces);
		
		// requestHeaders
		String[] headers = rm.headers();
		List<String> requestHeaders = new ArrayList<>();
		if (headers != null && headers.length > 0) {
			for (String _s : headers) {
				requestHeaders.add(_s);
			}
		}
		requestMapping.setRequestHeaders(requestHeaders);
		
		// requestParams
		String[] params = rm.params();
		List<String> requestParams = new ArrayList<>();
		if (params != null && params.length > 0) {
			for (String _s : params) {
				requestParams.add(_s);
			}
		}
		requestMapping.setRequestParams(requestParams);
		
		return requestMapping;
	}
	
	// 查找Facade类的Methods
	protected List<Method> findMethods(Class<?> facadeClz) {
		List<Method> list = new ArrayList<>();
		
		java.lang.reflect.Method[] mds = facadeClz.getMethods();
		if (mds == null || mds.length <= 0) {
			throw new RuntimeException("Facade[" + facadeClz.getName() + "]定义的方法不能空!");
		}
		for (java.lang.reflect.Method _md : mds) {
			Method method = new Method();
			
			MethodConfig mc = _md.getAnnotation(MethodConfig.class);
			if (mc == null) {
				method.setDescription(_md.getName() + "方法定义说明");
				method.setListResultFlag(false);
				method.setPagination(false);
				method.setWithTransaction(true);
			} else {
				method.setDescription(mc.description());
				method.setListResultFlag(mc.listResultFlag());
				method.setPagination(mc.paginationFlag());
				method.setWithTransaction(mc.withTransaction());
			}
			method.setName(_md.getName());
			method.setExecGenerator(true);
			
			// return
			Type rtType = _md.getGenericReturnType();
			if (!(rtType instanceof ParameterizedType)) {
				throw new RuntimeException("Facade[" + facadeClz.getName() + "." + _md.getName() + "]定义的方法返回类型错误!");
			}
			ParameterizedType type1 = (ParameterizedType) rtType;
			Type[] rts = type1.getActualTypeArguments();
			method.setReturnRealType(rts[0].getTypeName());
			
			// exceptions
			Class<?>[] exceptions = _md.getExceptionTypes();
			List<String> exceptionsList = new ArrayList<>();
			if (exceptions == null || exceptions.length <= 0) {
				exceptionsList.add("java.lang.Exception");
			} else {
				for (Class<?> _c : exceptions) {
					exceptionsList.add(_c.getName());
				}
			}
			method.setExceptions(exceptionsList);
			
			// requestMapping
			RequestMapping requestMapping = findMethodMapping(_md);
			if (requestMapping == null) {
				throw new RuntimeException("Facade[" + facadeClz.getName() + "." + _md.getName() + "]定义的方法没有RequestMapping!");
			}
			method.setRequestMapping(requestMapping);
			
			// params
			Parameter[] params = _md.getParameters();
			List<Param> paramList = new ArrayList<>();
			if (params != null && params.length > 0) {
				for (Parameter _param : params) {
					Param param = new Param();
					
					param.setName(_param.getName());
					Class<?> _paramType = _param.getType();
					if (_paramType.isArray()) {
						String tmpName = _paramType.getName().substring(2);
						tmpName = tmpName.substring(0, tmpName.length() - 1);
						param.setType(tmpName + "[]");
					} else if (_paramType.isPrimitive()) { 
						param.setType(getWrappeType(_paramType));
					} else {
						param.setType(_paramType.getName());
					}
					
					param.setDescription(_param.getName() + "参数说明");
					
					Annotation[] annotations = _param.getAnnotations();
					List<String> annotationList = new ArrayList<>();
					if (annotations != null && annotations.length > 0) {
						for (Annotation _an : annotations) {
							if (_an instanceof org.springframework.web.bind.annotation.RequestBody || 
								_an instanceof org.springframework.web.bind.annotation.RequestHeader || 
								_an instanceof org.springframework.web.bind.annotation.RequestParam ||
								_an instanceof org.springframework.web.bind.annotation.PathVariable ||
								_an instanceof org.springframework.web.bind.annotation.RequestPart ||
								_an instanceof org.springframework.web.bind.annotation.MatrixVariable) {
								continue;
							}
							
							annotationList.add(_an.annotationType().getName());
						}
						
						param.setAnnotations(annotationList);
					}
					
					// RequestBody
					org.springframework.web.bind.annotation.RequestBody _requestBody = _param.getAnnotation(org.springframework.web.bind.annotation.RequestBody.class);
					if (_requestBody != null) {
						param.setRequestType("RequestBody");
						param.setNullable(false);
					}
					
					// RequestPart
					org.springframework.web.bind.annotation.RequestPart _requestPart = _param.getAnnotation(org.springframework.web.bind.annotation.RequestPart.class);
					if (_requestPart != null) {
						String _nameOld = param.getName();
						String _name = _requestPart.value();
						if (_name == null || _name.trim().equals("")) {
							_name = _requestPart.name();
						}
						if (_name != null && !_name.trim().equals("")) {
							param.setName(_name.trim());
							param.setDescription(_name.trim() + "参数说明");
						} else {
							param.setDescription(_nameOld + "参数说明");
						}
						param.setRequestType("RequestPart");
						param.setNullable(!_requestPart.required());
					}
					
					// RequestHeader
					org.springframework.web.bind.annotation.RequestHeader _requestHeader = _param.getAnnotation(org.springframework.web.bind.annotation.RequestHeader.class);
					if (_requestHeader != null) {
						String _nameOld = param.getName();
						String _name = _requestHeader.value();
						if (_name == null || _name.trim().equals("")) {
							_name = _requestHeader.name();
						}
						if (_name != null && !_name.trim().equals("")) {
							param.setName(_name.trim());
							param.setDescription(_name.trim() + "参数说明");
						} else {
							param.setDescription(_nameOld + "参数说明");
						}
						param.setRequestType("RequestHeader");
						param.setNullable(!_requestHeader.required());
						String _defaultValue = _requestHeader.defaultValue();
						if (_defaultValue != null && !ValueConstants.DEFAULT_NONE.equals(_defaultValue)) {
							param.setDefaultValue(_defaultValue.trim());
						}
					}
					
					// RequestParam
					org.springframework.web.bind.annotation.RequestParam _requestParam = _param.getAnnotation(org.springframework.web.bind.annotation.RequestParam.class);
					if (_requestParam != null) {
						String _nameOld = param.getName();
						String _name = _requestParam.value();
						if (_name == null || _name.trim().equals("")) {
							_name = _requestParam.name();
						}
						if (_name != null && !_name.trim().equals("")) {
							param.setName(_name.trim());
							param.setDescription(_name.trim() + "参数说明");
						} else {
							param.setDescription(_nameOld + "参数说明");
						}
						param.setRequestType("RequestParam");
						param.setNullable(!_requestParam.required());
						String _defaultValue = _requestParam.defaultValue();
						if (_defaultValue != null && !ValueConstants.DEFAULT_NONE.equals(_defaultValue)) {
							param.setDefaultValue(_defaultValue.trim());
						}
					}
					
					// MatrixVariable
					org.springframework.web.bind.annotation.MatrixVariable _matrixVariable = _param.getAnnotation(org.springframework.web.bind.annotation.MatrixVariable.class);
					if (_matrixVariable != null) {
						String _nameOld = param.getName();
						String _name = _matrixVariable.value();
						if (_name == null || _name.trim().equals("")) {
							_name = _matrixVariable.name();
						}
						if (_name != null && !_name.trim().equals("")) {
							param.setName(_name.trim());
							param.setDescription(_name.trim() + "参数说明");
						} else {
							param.setDescription(_nameOld + "参数说明");
						}
						param.setRequestType("MatrixVariable");
						param.setNullable(!_matrixVariable.required());
						String _defaultValue = _matrixVariable.defaultValue();
						if (_defaultValue != null && !ValueConstants.DEFAULT_NONE.equals(_defaultValue)) {
							param.setDefaultValue(_defaultValue.trim());
						}
						String _pathVar = _matrixVariable.pathVar();
						if (_pathVar != null && !ValueConstants.DEFAULT_NONE.equals(_pathVar)) {
							param.setPathVar(_pathVar.trim());
						}
					}
					
					// PathVariable
					org.springframework.web.bind.annotation.PathVariable _pathVariable = _param.getAnnotation(org.springframework.web.bind.annotation.PathVariable.class);
					if (_pathVariable != null) {
						String _nameOld = param.getName();
						String _name = _pathVariable.value();
						if (_name == null || _name.trim().equals("")) {
							_name = _pathVariable.name();
						}
						if (_name != null && !_name.trim().equals("")) {
							param.setName(_name.trim());
							param.setDescription(_name.trim() + "参数说明");
						} else {
							param.setDescription(_nameOld + "参数说明");
						}
						param.setRequestType("PathVariable");
						param.setNullable(!_pathVariable.required());
					}
					
					paramList.add(param);
				}
				method.setParams(paramList);
			}
			
			list.add(method);
		}
		
		return list;
	}
	
	private RequestMapping findMethodMapping(java.lang.reflect.Method mothod) throws RuntimeException {
		// RequestMapping
		org.springframework.web.bind.annotation.RequestMapping _requestMapping = mothod.getAnnotation(org.springframework.web.bind.annotation.RequestMapping.class);
		if (_requestMapping != null) {
			return this.findRequestMapping(_requestMapping);
		}
		
		// GetMapping
		org.springframework.web.bind.annotation.GetMapping getMapping = mothod.getAnnotation(org.springframework.web.bind.annotation.GetMapping.class);
		if (getMapping != null) {
			return findGetMapping(getMapping);
		}
		
		// PostMapping
		org.springframework.web.bind.annotation.PostMapping postMapping = mothod.getAnnotation(org.springframework.web.bind.annotation.PostMapping.class);
		if (postMapping != null) {
			return findPostMapping(postMapping);
		}
		
		// PutMapping
		org.springframework.web.bind.annotation.PutMapping putMapping = mothod.getAnnotation(org.springframework.web.bind.annotation.PutMapping.class);
		if (putMapping != null) {
			return findPutMapping(putMapping);
		}
		
		// DeleteMapping
		org.springframework.web.bind.annotation.DeleteMapping deleteMapping = mothod.getAnnotation(org.springframework.web.bind.annotation.DeleteMapping.class);
		if (deleteMapping != null) {
			return findDeleteMapping(deleteMapping);
		}
		
		// PatchMapping
		org.springframework.web.bind.annotation.PatchMapping patchMapping = mothod.getAnnotation(org.springframework.web.bind.annotation.PatchMapping.class);
		if (patchMapping != null) {
			return findPatchMapping(patchMapping);
		}
		
		return null;
	}
	
	// GetMapping
	private RequestMapping findGetMapping(org.springframework.web.bind.annotation.GetMapping rm) {
		RequestMapping requestMapping = new RequestMapping();
		
		// requestPaths
		String[] paths = rm.path();
		if (paths == null || paths.length <= 0) {
			paths = rm.value();
		}
		List<String> requestPaths = new ArrayList<>();
		if (paths != null && paths.length > 0) {
			for (String _s : paths) {
				requestPaths.add(_s.trim());
			}
		}
		requestMapping.setRequestPaths(requestPaths);
		
		// requestMethods
		List<String> requestMethods = new ArrayList<>();
		requestMethods.add("GET");
		requestMapping.setRequestMethods(requestMethods);
		
		// requestConsumes
		String[] consumes = rm.consumes();
		List<String> requestConsumes = new ArrayList<>();
		if (consumes != null && consumes.length > 0) {
			for (String _s : consumes) {
				requestConsumes.add(_s);
			}
		}
		requestMapping.setRequestConsumes(requestConsumes);
		
		// requestProduces
		String[] produces = rm.produces();
		List<String> requestProduces = new ArrayList<>();
		if (produces != null && produces.length > 0) {
			for (String _s : produces) {
				requestProduces.add(_s);
			}
		}
		requestMapping.setRequestProduces(requestProduces);
		
		// requestHeaders
		String[] headers = rm.headers();
		List<String> requestHeaders = new ArrayList<>();
		if (headers != null && headers.length > 0) {
			for (String _s : headers) {
				requestHeaders.add(_s);
			}
		}
		requestMapping.setRequestHeaders(requestHeaders);
		
		// requestParams
		String[] params = rm.params();
		List<String> requestParams = new ArrayList<>();
		if (params != null && params.length > 0) {
			for (String _s : params) {
				requestParams.add(_s);
			}
		}
		requestMapping.setRequestParams(requestParams);
		
		return requestMapping;
	}
	// PostMapping
	private RequestMapping findPostMapping(org.springframework.web.bind.annotation.PostMapping rm) {
		RequestMapping requestMapping = new RequestMapping();
		
		// requestPaths
		String[] paths = rm.path();
		if (paths == null || paths.length <= 0) {
			paths = rm.value();
		}
		List<String> requestPaths = new ArrayList<>();
		if (paths != null && paths.length > 0) {
			for (String _s : paths) {
				requestPaths.add(_s.trim());
			}
		}
		requestMapping.setRequestPaths(requestPaths);
		
		// requestMethods
		List<String> requestMethods = new ArrayList<>();
		requestMethods.add("POST");
		requestMapping.setRequestMethods(requestMethods);
		
		// requestConsumes
		String[] consumes = rm.consumes();
		List<String> requestConsumes = new ArrayList<>();
		if (consumes != null && consumes.length > 0) {
			for (String _s : consumes) {
				requestConsumes.add(_s);
			}
		}
		requestMapping.setRequestConsumes(requestConsumes);
		
		// requestProduces
		String[] produces = rm.produces();
		List<String> requestProduces = new ArrayList<>();
		if (produces != null && produces.length > 0) {
			for (String _s : produces) {
				requestProduces.add(_s);
			}
		}
		requestMapping.setRequestProduces(requestProduces);
		
		// requestHeaders
		String[] headers = rm.headers();
		List<String> requestHeaders = new ArrayList<>();
		if (headers != null && headers.length > 0) {
			for (String _s : headers) {
				requestHeaders.add(_s);
			}
		}
		requestMapping.setRequestHeaders(requestHeaders);
		
		// requestParams
		String[] params = rm.params();
		List<String> requestParams = new ArrayList<>();
		if (params != null && params.length > 0) {
			for (String _s : params) {
				requestParams.add(_s);
			}
		}
		requestMapping.setRequestParams(requestParams);
		
		return requestMapping;
	}
	// PutMapping
	private RequestMapping findPutMapping(org.springframework.web.bind.annotation.PutMapping rm) {
		RequestMapping requestMapping = new RequestMapping();
		
		// requestPaths
		String[] paths = rm.path();
		if (paths == null || paths.length <= 0) {
			paths = rm.value();
		}
		List<String> requestPaths = new ArrayList<>();
		if (paths != null && paths.length > 0) {
			for (String _s : paths) {
				requestPaths.add(_s.trim());
			}
		}
		requestMapping.setRequestPaths(requestPaths);
		
		// requestMethods
		List<String> requestMethods = new ArrayList<>();
		requestMethods.add("PUT");
		requestMapping.setRequestMethods(requestMethods);
		
		// requestConsumes
		String[] consumes = rm.consumes();
		List<String> requestConsumes = new ArrayList<>();
		if (consumes != null && consumes.length > 0) {
			for (String _s : consumes) {
				requestConsumes.add(_s);
			}
		}
		requestMapping.setRequestConsumes(requestConsumes);
		
		// requestProduces
		String[] produces = rm.produces();
		List<String> requestProduces = new ArrayList<>();
		if (produces != null && produces.length > 0) {
			for (String _s : produces) {
				requestProduces.add(_s);
			}
		}
		requestMapping.setRequestProduces(requestProduces);
		
		// requestHeaders
		String[] headers = rm.headers();
		List<String> requestHeaders = new ArrayList<>();
		if (headers != null && headers.length > 0) {
			for (String _s : headers) {
				requestHeaders.add(_s);
			}
		}
		requestMapping.setRequestHeaders(requestHeaders);
		
		// requestParams
		String[] params = rm.params();
		List<String> requestParams = new ArrayList<>();
		if (params != null && params.length > 0) {
			for (String _s : params) {
				requestParams.add(_s);
			}
		}
		requestMapping.setRequestParams(requestParams);
		
		return requestMapping;
	}
	// DeleteMapping
	private RequestMapping findDeleteMapping(org.springframework.web.bind.annotation.DeleteMapping rm) {
		RequestMapping requestMapping = new RequestMapping();
		
		// requestPaths
		String[] paths = rm.path();
		if (paths == null || paths.length <= 0) {
			paths = rm.value();
		}
		List<String> requestPaths = new ArrayList<>();
		if (paths != null && paths.length > 0) {
			for (String _s : paths) {
				requestPaths.add(_s.trim());
			}
		}
		requestMapping.setRequestPaths(requestPaths);
		
		// requestMethods
		List<String> requestMethods = new ArrayList<>();
		requestMethods.add("DELETE");
		requestMapping.setRequestMethods(requestMethods);
		
		// requestConsumes
		String[] consumes = rm.consumes();
		List<String> requestConsumes = new ArrayList<>();
		if (consumes != null && consumes.length > 0) {
			for (String _s : consumes) {
				requestConsumes.add(_s);
			}
		}
		requestMapping.setRequestConsumes(requestConsumes);
		
		// requestProduces
		String[] produces = rm.produces();
		List<String> requestProduces = new ArrayList<>();
		if (produces != null && produces.length > 0) {
			for (String _s : produces) {
				requestProduces.add(_s);
			}
		}
		requestMapping.setRequestProduces(requestProduces);
		
		// requestHeaders
		String[] headers = rm.headers();
		List<String> requestHeaders = new ArrayList<>();
		if (headers != null && headers.length > 0) {
			for (String _s : headers) {
				requestHeaders.add(_s);
			}
		}
		requestMapping.setRequestHeaders(requestHeaders);
		
		// requestParams
		String[] params = rm.params();
		List<String> requestParams = new ArrayList<>();
		if (params != null && params.length > 0) {
			for (String _s : params) {
				requestParams.add(_s);
			}
		}
		requestMapping.setRequestParams(requestParams);
		
		return requestMapping;
	}
	// PatchMapping
	private RequestMapping findPatchMapping(org.springframework.web.bind.annotation.PatchMapping rm) {
		RequestMapping requestMapping = new RequestMapping();
		
		// requestPaths
		String[] paths = rm.path();
		if (paths == null || paths.length <= 0) {
			paths = rm.value();
		}
		List<String> requestPaths = new ArrayList<>();
		if (paths != null && paths.length > 0) {
			for (String _s : paths) {
				requestPaths.add(_s.trim());
			}
		}
		requestMapping.setRequestPaths(requestPaths);
		
		// requestMethods
		List<String> requestMethods = new ArrayList<>();
		requestMethods.add("PATCH");
		requestMapping.setRequestMethods(requestMethods);
		
		// requestConsumes
		String[] consumes = rm.consumes();
		List<String> requestConsumes = new ArrayList<>();
		if (consumes != null && consumes.length > 0) {
			for (String _s : consumes) {
				requestConsumes.add(_s);
			}
		}
		requestMapping.setRequestConsumes(requestConsumes);
		
		// requestProduces
		String[] produces = rm.produces();
		List<String> requestProduces = new ArrayList<>();
		if (produces != null && produces.length > 0) {
			for (String _s : produces) {
				requestProduces.add(_s);
			}
		}
		requestMapping.setRequestProduces(requestProduces);
		
		// requestHeaders
		String[] headers = rm.headers();
		List<String> requestHeaders = new ArrayList<>();
		if (headers != null && headers.length > 0) {
			for (String _s : headers) {
				requestHeaders.add(_s);
			}
		}
		requestMapping.setRequestHeaders(requestHeaders);
		
		// requestParams
		String[] params = rm.params();
		List<String> requestParams = new ArrayList<>();
		if (params != null && params.length > 0) {
			for (String _s : params) {
				requestParams.add(_s);
			}
		}
		requestMapping.setRequestParams(requestParams);
		
		return requestMapping;
	}
	
	// boolean, byte, char, short, int, long, float, and double
	private String getWrappeType(Class<?> primitiveClass) {
		String clzName = primitiveClass.getName();
		if ("boolean".equals(clzName)) {
			return "java.lang.Boolean";
		} else if ("byte".equals(clzName)) {
			return "java.lang.Byte";
		} else if ("char".equals(clzName)) {
			return "java.lang.Character";
		} else if ("short".equals(clzName)) {
			return "java.lang.Short";
		} else if ("int".equals(clzName)) {
			return "java.lang.Integer";
		} else if ("long".equals(clzName)) {
			return "java.lang.Long";
		} else if ("float".equals(clzName)) {
			return "java.lang.Float";
		} else if ("double".equals(clzName)) {
			return "java.lang.Double";
		}
		
		return null;
	}
	
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
