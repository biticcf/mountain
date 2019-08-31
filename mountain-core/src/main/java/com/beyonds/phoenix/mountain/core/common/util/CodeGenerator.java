/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.util;

import java.util.UUID;
/**
 * author  DanielCao
 * date    2015年3月11日
 * time    下午4:44:23
 * Code代码生成器
 * code代码结构定义：
 * +前缀(2位) + 当前时间毫秒数(13位) + 序号(6位,每毫秒从000000开始)
 */
public final class CodeGenerator {
	//默认前缀(99)
	public static final String CODE_PREFIX_DEFAULT = "99";
	//组合商品代码前缀
	public static final String CODE_PREFIX_COMBINED_GOODS = "gc";
	//组合商品组代码前缀
	public static final String CODE_PREFIX_COMBINED_GROUP = "gg";
	//商品促销活动编码
	public static final String CODE_PREFIX_PROMITION_GOODS = "gp";
	//生成活动跟踪Id前缀
	public static final String CODE_PREFIX_TRACE_ACTIVITY = "ta";
	//生成traceId前缀
	public static final String CODE_PREFIX_TRACE_ID = "tr";
	
	private CodeGenerator() {
		
	}
	
	/**
	 * 根据给定前缀生成CODE码
	 * @param prefix 前缀
	 * @return 返回结果
	 */
	public static String generateCode(final String prefix) {
		String newPrefix = prefix;
		if (prefix == null || prefix.trim().length() != 2) {
			newPrefix = CODE_PREFIX_DEFAULT;
		} else {
			newPrefix = prefix.trim();
		}
		
		return newPrefix + randomUUID();
	}
	private static String randomUUID() {  
        return UUID.randomUUID().toString().replace("-", "");
    }
}
