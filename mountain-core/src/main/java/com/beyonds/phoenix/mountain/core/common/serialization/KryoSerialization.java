/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * author: DanielCao
 * date:   2018年5月16日
 * time:   上午9:05:13
 *
 */
public class KryoSerialization implements Serialization {
	private static Logger logger = LoggerFactory.getLogger(KryoSerialization.class);
	
	private final ThreadLocal<Kryo> kryoLocal = new ThreadLocal<Kryo>() {
		@Override
		protected Kryo initialValue() {
			Kryo kryo = new Kryo();
			kryo.setReferences(true);
			kryo.setRegistrationRequired(false);
			
			return kryo;
		}
	};
	
	public KryoSerialization() {
		
	}
	
	@Override
	public <T> byte[] serialize(T obj) throws IOException {
		Kryo kryo = kryoLocal.get();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Output output = new Output(baos);
		kryo.writeObject(output, obj);
		output.close();
		
		return baos.toByteArray();
	}
	
	@Override
	public <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException {
		Kryo kryo = kryoLocal.get();
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		Input input = new Input(bais);
		input.close();
		
		return (T) kryo.readObject(input, clz);
	}
	
	/**
	 * 测试用例
	 * @param args 命令行参数
	 * @throws IOException 测试异常
	 */
	public static void main(String[] args) throws IOException {
		BigDecimal bd = new BigDecimal("1234567.000000000000000000000000000000001");
		Serialization serialization = new KryoSerialization();
		
		byte[] bytes = serialization.serialize(bd);
		BigDecimal bd1 = serialization.deserialize(bytes, BigDecimal.class);
		
		logger.info("" + bd1);
	}
}
