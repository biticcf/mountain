/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.hash;

/**
 * 
 * author  DanielCao
 * date    2015年4月1日
 * time    下午8:02:35
 *
 */
public interface HashFunction {
	/**
	 * 计算对象的hash值
	 * @param obj 输入对象
	 * @return 输出hash值
	 */
	long hash(Object obj);
}
