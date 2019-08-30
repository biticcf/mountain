
package com.beyonds.phoenix.mountain.core.common.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 保密码、序列号生成工具
 * @author  DanielCao
 * @date    2015年4月1日
 * @time    下午8:07:53
 *
 */
public final class KeyCodeGenerator {
	public static final DateFormat DATE_FORMAT_LONG = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static final DateFormat DATE_FORMAT_SHORT = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final DateFormat DATE_FORMAT_LONG_TRIM = new SimpleDateFormat("yyyyMMddHHmmss");
	public static final DateFormat DATE_FORMAT_SHORT_TRIM = new SimpleDateFormat("yyyyMMdd");
	
	private KeyCodeGenerator() {
		
	}
	
	/**
	 * 获取当前时间数值，精确到秒
	 * @return int 秒
	 */
	public static int getCurrentTimestamp() {
		return (int) (System.currentTimeMillis() / 1000);
	}
	/**
	 * 获取结束时间,截止到23:59:59
	 * @param date 入参
	 * @return 返回结果
	 */
	public static int getEndTimestamp(final Date date) {
		Calendar cal = Calendar.getInstance();
		if (date == null) {
			cal.setTime(new Date());
		} else {
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 59);
		cal.set(Calendar.SECOND, 59);
		
		return (int) (cal.getTimeInMillis() / 1000);
	}
	/**
	 * 获取开始时间,从0:00:00
	 * @param date 入参
	 * @return 出参
	 */
	public static int getBeginTimestamp(final Date date) {
		Calendar cal = Calendar.getInstance();
		if (date == null) {
			cal.setTime(new Date());
		} else {
			cal.setTime(date);
		}
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		return (int) (cal.getTimeInMillis() / 1000);
	}
	/**
	 * 根据尝试次数、最后尝试时间计算本次是否应该推迟重试
	 * 【发送时间点：0,5,20,45,80,125,360,735,1280】
	 * @param tryTimes int 尝试次数
	 * @param lastTryTime int 最后尝试时间
	 * @param status 类型
	 * @return boolean 如果推迟尝试，返回true，否则返回false
	 */
	public static boolean delayTry(int tryTimes, int lastTryTime, byte status) {
		if (status == 0) {
			return false; //状态为0不延迟
		}
		int currentTime = KeyCodeGenerator.getCurrentTimestamp();
		int delayMinute = tryTimes * tryTimes * 5 * (Math.max(1, (tryTimes - 4)));
		if ((currentTime - lastTryTime) / 60 < delayMinute) {
			return true;
		}
		return false;
	}
	/**
	 * int类型时间转化为时间串
	 * @param intTime 入参
	 * @param dateFormat 入参
	 * @return 出参
	 */
	public static String parseTimeToString(final int intTime, final DateFormat dateFormat) {
		if (intTime <= 0) {
			return dateFormat.format(new Date());
		}
		long time = 1000L * intTime;
		Date date = new Date(time);
		
		return dateFormat.format(date);
	}
	/**
	 * 
	 * @param intTime 入参
	 * @return 出参
	 */
	public static String parseTimeToString(final int intTime) {
		return parseTimeToString(intTime, DATE_FORMAT_LONG);
	}
	public static final char[] CHARS = new char[] {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 
		'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', 
		'3', '4', '5', '6', '7', '8', '9', '0', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 
		'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z' };
	
	/**
	 * 
	 * @param length 入参
	 * @return 出参
	 */
	public static String produceCode(int length) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < length; i++) {
			sb.append(CHARS[(int) (((long) (System.currentTimeMillis() * Math.random())) 
					% (1L * CHARS.length))]);
		}
		return sb.toString();
	}
}
