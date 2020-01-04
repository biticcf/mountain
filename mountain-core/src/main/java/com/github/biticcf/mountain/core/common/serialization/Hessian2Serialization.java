/**
 * 
 */
package com.github.biticcf.mountain.core.common.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;

/**
 * author: DanielCao
 * date:   2018年5月16日
 * time:   上午9:14:48
 *
 */
public class Hessian2Serialization implements Serialization {
	private static Log logger = LogFactory.getLog(Hessian2Serialization.class);
	
	public Hessian2Serialization() {
		
	}
	
	@Override
	public <T> byte[] serialize(T obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Hessian2Output out = new Hessian2Output(baos);
		out.writeObject(obj);
		out.flush();
		
		return baos.toByteArray();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		Hessian2Input input = new Hessian2Input(bais);
		
		return (T) input.readObject(clz);
	}
	
	/**
	 * 测试用例
	 * @param args 命令行参数
	 * @throws IOException 测试异常
	 */
	public static void main(String[] args) throws IOException {
		BigDecimal bd = new BigDecimal("1234567.000000000000000000000000000000001");
		Serialization serialization = new Hessian2Serialization();
		
		byte[] bytes = serialization.serialize(bd);
		BigDecimal bd1 = serialization.deserialize(bytes, BigDecimal.class);
		
		logger.info("" + bd1);
	}
}
