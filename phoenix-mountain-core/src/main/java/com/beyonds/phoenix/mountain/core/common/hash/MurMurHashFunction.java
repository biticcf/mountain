/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
 
/**
 * MurMurHash算法，是非加密HASH算法，性能很高， 
 * 比传统的CRC32,MD5，SHA-1（这两个算法都是加密HASH算法，复杂度本身就很高，带来的性能上的损害也不可避免） 
 * 等HASH算法要快很多，而且据说这个算法的碰撞率很低.
 * http://murmurhash.googlepages.com/  
 * 
 * @author  DanielCao
 * @date    2015年4月1日
 * @time    下午8:02:48
 *
 */
public class MurMurHashFunction implements HashFunction {
	
	@Override
	public long hash(Object obj) {
		if (obj == null) {
			return 0L;
		}
		String key = obj.toString();
		ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
		long seed = 0x1234ABCD;

		ByteOrder byteOrder = buf.order();
		buf.order(ByteOrder.LITTLE_ENDIAN);

		long m = 0xc6a4a7935bd1e995L;
		int r = 47;

		long h = seed ^ (buf.remaining() * m);

		long k;
		while (buf.remaining() >= 8) {
			k = buf.getLong();

			k *= m;
			k ^= k >>> r;
			k *= m;

			h ^= k;
			h *= m;
		}

		if (buf.remaining() > 0) {
			ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
			finish.put(buf).rewind();
			h ^= finish.getLong();
			h *= m;
		}

		h ^= h >>> r;
		h *= m;
		h ^= h >>> r;

		buf.order(byteOrder);

		return h;
	}
}
