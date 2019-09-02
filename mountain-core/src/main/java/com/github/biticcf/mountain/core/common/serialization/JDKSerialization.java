/**
 * 
 */
package com.github.biticcf.mountain.core.common.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * author: DanielCao
 * date:   2018年5月16日
 * time:   上午10:16:10
 *
 */
public class JDKSerialization implements Serialization {
	private static Logger logger = LoggerFactory.getLogger(JDKSerialization.class);
	
	public JDKSerialization() {
		
	}
	
	@Override
	public <T> byte[] serialize(T obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(baos);
		out.writeObject(obj);
		out.close();
		
		return baos.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream input = new ObjectInputStream(bais);
		
		try {
			return (T) input.readObject();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			input.close();
		}
		
		return null;
	}
	
	/**
	 * 测试用例
	 * @param args 命令行参数
	 * @throws IOException 测试异常
	 */
	public static void main(String[] args) throws IOException {
		BigDecimal bd = new BigDecimal("1234567.000000000000000000000000000000001");
		Serialization serialization = new JDKSerialization();
		
		byte[] bytes = serialization.serialize(bd);
		BigDecimal bd1 = serialization.deserialize(bytes, BigDecimal.class);
		
		logger.info("" + bd1);
	}
}
