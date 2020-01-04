/**
 * 
 */
package com.github.biticcf.mountain.core.common.serialization;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * author: DanielCao
 * date:   2018年5月16日
 * time:   上午9:36:18
 *
 */
public class FastJsonSerialization implements Serialization {
	private static Log logger = LogFactory.getLog(FastJsonSerialization.class);
	
	private static final String CHARSET_NAME = "UTF-8";
	
	public FastJsonSerialization() {
		
	}
	
	@Override
	public <T> byte[] serialize(T obj) throws IOException {
		SerializeWriter out = new SerializeWriter();
		JSONSerializer serializer = new JSONSerializer(out);
		serializer.config(SerializerFeature.WriteEnumUsingToString, true);
		serializer.config(SerializerFeature.WriteClassName, true);
		serializer.write(obj);
		
		return out.toBytes(CHARSET_NAME);
	}
	
	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
		return JSON.parseObject(new String(bytes, CHARSET_NAME), clz);
	}
	
	/**
	 * 测试用例
	 * @param args 命令行参数
	 * @throws IOException 异常
	 */
	public static void main(String[] args) throws IOException {
		BigDecimal bd = new BigDecimal("1234567.000000000000000000000000000000001");
		Serialization serialization = new FastJsonSerialization();
		
		byte[] bytes = serialization.serialize(bd);
		BigDecimal bd1 = serialization.deserialize(bytes, BigDecimal.class);
		
		logger.info("" + bd1);
	}
}
