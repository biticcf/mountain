
package com.beyonds.phoenix.mountain.core.common.util;

/**
 * 
 * @Author: Daniel.Cao
 * @Date:   2018年11月19日
 * @Time:   上午11:38:59
 *
 */
public final class ArraysUtil {
	
    private ArraysUtil() {
		
	}
	/**
	 * 转化为Long数组
	 * @param strArr 待转化的数组
	 * @return 转化的结果
	 */
    public static Long[] toLongArr(String[] strArr) {
        Long[] longArr = new Long[strArr.length]; 
        for (int i = 0; i < strArr.length; i++) {
            longArr[i] = Long.parseLong(strArr[i]);
        }
        return longArr;
    }
    /**
	 * 转化为Long数组
	 * @param longArr 待转化的数组
	 * @return 转化的结果
	 */
    public static Long[] toLongArr(long[] longArr) {
        Long[] rs = new Long[longArr.length]; 
        for (int i = 0; i < longArr.length; i++) {
            rs[i] = Long.valueOf(longArr[i]);
        }
        return rs;
    }
    /**
     * 转化为long数组
     * @param longArr 待转化的数组
     * @return 转化的结果
     */
    public static long[] toLongArr(Long[] longArr) {
        long[] rs = new long[longArr.length];
        for (int i = 0; i < longArr.length; i++) {
            rs[i] = Long.valueOf(longArr[i]);
        }
        return rs;
    }
    /**
     * Long数组转化为字符串
     * @param ids 待转化的数组
     * @param separator 分隔符
     * @return 转化的结果
     */
    public static String toRawString(Long[] ids, String separator) {
    	if (ids == null || ids.length == 0) {
    		return "";
    	}
    	StringBuilder builder = new StringBuilder();
    	
    	for (Long id : ids) {
    		if (id != null) {
    			builder.append(id.longValue()).append(separator);
    		}
    	}
    	
    	if (builder.length() > 0) {
    		return builder.substring(0, builder.length() - separator.length());
    	}
    	
    	return builder.toString();
    }
}
