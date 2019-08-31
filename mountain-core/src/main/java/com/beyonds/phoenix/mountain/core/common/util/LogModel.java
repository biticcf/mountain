/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSONObject;

/**
 * author  DanielCao
 * date    2015年2月4日
 * time    下午4:10:40
 * +日志系统
 */
public final class LogModel {
	/** 附加信息集 */
	private Map<String, Object> datas;
	private String method;
	private long start = 0L;
	private final AtomicInteger serialId = new AtomicInteger(0);   
	
	/**
	 * 私有构造方法，初始化LogModel
	 * @param name 日志名称
	 */
	private LogModel(String name) {
		datas = new HashMap<String, Object>();
		start = System.currentTimeMillis();
		method = name + "#" + start  + "#";
		datas.put("_method", method);
	}
	
	/**
	 * LogModel入口，创建一个新的LogModel实例
	 * @param method 该LogModel实例的标志
	 * @return 实例
	 */
	public static LogModel newLogModel(String method) {
		return new LogModel(method);
	}
	/**
	 * 设置结果码和结果提示信息
	 * @param result 结果key
	 * @param message 结果value
	 * @return 实例
	 */
	public LogModel setResultMessage(long result, String message) {
		addMetaData("_result", result).addMetaData("_message", message);
		return this;
	}
	/**
	 * 增加一个名称为key，值为value的属性
	 * @param key key值
	 * @param value value值
	 * @return 实例
	 */
	public LogModel addMetaData(String key, Object value) {
		if (value != null) {
			datas.put(key, value);
		} else {
			datas.put(key, "");
		}
		return this;
	}
	/**
	 * 删除一个名称为key的属性
	 * @param key key值
	 * @return 实例
	 */
	public LogModel delMetaData(String key) {
		if (key != null) {
			datas.remove(key); 		
		}
		return this;
	}
	/**
	 * LogModel转化为Map
	 * @return 参数列表Map
	 */
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (Map.Entry<String, Object> entry : datas.entrySet()) {
			map.put(entry.getKey(), entry.getValue());
		}
		return map;
	}
	/**
	 * LogModel转化为json字符串
	 * @param purge true清空LogModel（只保留_method属性）;false保留所有属性
	 * @return 字符串话
	 */
	public String toJson(boolean purge) {
		try {
			datas.put("_serialId", serialId.incrementAndGet());
			if (purge) {
				datas.put("handleCost", System.currentTimeMillis() - start);
				JSONObject ja = (JSONObject) JSONObject.toJSON(datas);
				purge();
				
				return ja.toString();
			} else {
				Map<String, Object> map = toMap();
				map.put("handleCost", System.currentTimeMillis() - start);
				JSONObject ja = (JSONObject) JSONObject.toJSON(map);

				return ja.toString();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "{data:error}";
	}

	/**
	 * 清空缓存
	 */
	private void purge() {
		this.datas.clear();
		datas.put("_method", method);

	}
	/**
	 * 缓存转化为结果
	 * @return 结果字符串
	 */
	public String endJson() {
		return toJson(true);

	}
	/**
	 * 缓存转化为结果
	 * @return 结果字符串
	 */
	public String toJson() {
		return toJson(true);
	}
}
