/**
 * 
 */
package com.github.biticcf.mountain.core.common.tomcat;

import java.util.List;

import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.valves.AccessLogValve;

/**
 * author:  Daniel.Cao
 * date:    2019年12月17日
 * time:    下午1:24:43
 *
 */
public class FiltedAccessLogValve extends AccessLogValve {
	private List<RequestEntry> filtedRequestEntries;
	
	@Override
	public void log(Request request, Response response, long time) {
		String uri = request.getDecodedRequestURI();
		String method = request.getMethod();
		
		// 不跳过,输出日志
		if (uri == null || uri.trim().equals("")) {
			super.log(request, response, time);
			
			return;
		}
		
		uri = uri.trim();
		if (filtedRequestEntries != null && !filtedRequestEntries.isEmpty()) {
			boolean needSkip = false;
			for (RequestEntry _entry : filtedRequestEntries) {
				String _uri = _entry.getUri();
				if (_uri == null || _uri.trim().equals("")) {
					continue;
				}
				boolean uriChecked = false;
				Integer _type = _entry.getType();
				if (_type == null) {
					_type = 0;
				}
				if (uri.equalsIgnoreCase(_entry.getUri())) {
					uriChecked = true;
				} else {
					if (_type.intValue() == 1 && uri.toLowerCase().startsWith(_uri.toLowerCase())) { // 前缀匹配
						uriChecked = true;
					} else if (_type.intValue() == 2 && uri.toLowerCase().endsWith(_uri.toLowerCase())) { // 后缀匹配
						uriChecked = true;
					} else if (_type.intValue() == 3 && uri.toLowerCase().matches(_uri.toLowerCase())) {
						uriChecked = true;
					}
				}
				if (uriChecked) {
					String _method = _entry.getMethod();
					// 跳过,不输出日志
					if (_method == null || _method.trim().equals("") || _method.trim().equals("-")) {
						needSkip = true;
						break;
					}
					
					_method = _method.trim();
					// 跳过,不输出日志
					if (method != null && method.trim().equalsIgnoreCase(_method)) {
						needSkip = true;
						break;
					}
				}
			}
			// 跳过,不输出日志
			if (needSkip) {
				return;
			}
		}
		
		// 不跳过,输出日志
		super.log(request, response, time);
	}
	
	/**
	 * 
	 * @return filtedRequestEntries
	 */
	public List<RequestEntry> getFiltedRequestEntries() {
		return filtedRequestEntries;
	}
	
	/**
	 * 
	 * @param filtedRequestEntries filtedRequestEntries
	 */
	public void setFiltedRequestEntries(List<RequestEntry> filtedRequestEntries) {
		this.filtedRequestEntries = filtedRequestEntries;
	}
	
	public static class RequestEntry {
		private String uri; // 请求的uri
		private String method; //求情的method
		private Integer type; //uri匹配模式:0/null-完全匹配;1-前缀匹配;2-后缀匹配;3-正则表达式
		
		/**
		 * 
		 * @return uri
		 */
		public String getUri() {
			return uri;
		}
		/**
		 * 
		 * @param uri uri
		 */
		public void setUri(String uri) {
			this.uri = uri;
		}
		/**
		 * 
		 * @return method
		 */
		public String getMethod() {
			return method;
		}
		/**
		 * 
		 * @param method method
		 */
		public void setMethod(String method) {
			this.method = method;
		}
		/**
		 * 
		 * @return type
		 */
		public Integer getType() {
			return type;
		}
		/**
		 * 
		 * @param type type
		 */
		public void setType(Integer type) {
			this.type = type;
		}
	}
}
