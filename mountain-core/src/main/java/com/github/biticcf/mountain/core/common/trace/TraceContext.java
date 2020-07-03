/**
 * 
 */
package com.github.biticcf.mountain.core.common.trace;

/**
 * author: DanielCao
 * date:   2020-7-3
 * time:   2:05:34
 * +trace环境上下文
 */
public final class TraceContext {
	public static final String TRACE_ID = "_trace_id_";
	
	private static final InheritableThreadLocal<String>  traceCache = new InheritableThreadLocal<>();
	
	/**
	 * + 添加一个traceId
	 * @param traceId traceId
	 */
	public static void addTrace(String traceId) {
		traceCache.set(traceId);
	}
	
	/**
	 * + 删除一个traceId
	 */
	public static void deleteTrace() {
		traceCache.remove();
	}
	
	/**
	 * + 查询当前traceId
	 * @return traceId
	 */
	public static String getTrace() {
		return traceCache.get();
	}
}
