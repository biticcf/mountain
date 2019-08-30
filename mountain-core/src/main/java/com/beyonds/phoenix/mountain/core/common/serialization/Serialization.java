/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.serialization;

import java.io.IOException;

/**
 * @Author: DanielCao
 * @Date:   2018年5月16日
 * @Time:   上午8:58:45
 * 
 * 自定义RPC序列化/反序列化工具,默认提供了4种实现
 * 1,KryoSerialization:专为JAVA定制的序列化协议,序列化后字节数少,利于网络传输.但不支持跨语言(或支持的代价比较大).
 * 2,Hessian2Serialization:支持跨语言,序列化后字节数适中,API易用,是国内主流rpc框架dubbo和motan的默认序列化协议.
 * 3,FastJsonSerialization:可以将其作为一个跨语言序列化的简易实现方案.
 * 4,JDKSerialization:Java默认序列化实现方案.
 */
public interface Serialization {
	/**
	 * 序列化一个对象
	 * @param <T> 对象类型
	 * @param obj 对象值
	 * @return 序列化结果值
	 * @throws IOException 异常
	 */
	<T> byte[] serialize(T obj) throws IOException;
	/**
	 * 反序列化成一个对象
	 * @param <T> 对象类型
	 * @param bytes 待反序列化的值
	 * @param clz 待反序列化的实例
	 * @return 反序列化的对象结果
	 * @throws IOException 反序列化异常
	 */
	<T> T deserialize(byte[] bytes, Class<T> clz) throws IOException;
}
