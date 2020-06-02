/**
 * 
 */
package com.github.biticcf.mountain.core.common.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * author： Daniel.Cao
 * date:   2020年4月21日
 * time:   上午10:31:19
 * +日期相关工具类,功能包括:
 * + 1, 获取一天的开始时间,并格式化为yyyy-MM-dd 0:00:00
 * + 2, 获取一天的结束时间,并格式化为yyyy-MM-dd 23:59:59
 * + 3, 格式化日期(线程安全)
 * + 4, 解析日期(线程安全)
 * + 5, 获取给定日期所在月份的天数
 * + 6, 获取给定日期所在月份的第一天
 * + 6.1, 获取给定日期所在月份的第一天，并且格式化
 * + 7, 获取给定日期所在月份的最后一天
 * + 7.1, 获取给定日期所在月份的最后一天，并且格式化
 * + 8, 获取给定日期所在星期的第一天
 * + 8.1, 获取给定日期所在星期的第一天,并且格式化
 * + 9, 获取给定日期所在星期的最后一天
 * + 9.1, 获取给定日期所在星期的最后一天,并且格式化
 * + 10, 把当前时间加/减一段时间后算出新的时间
 * + 10.1, 把当前时间加/减一段时间后算出新的时间,并且格式化
 */
public final class DatetimeUtils {
	private static final Log logger = LogFactory.getLog(DatetimeUtils.class);
	
	/**
	 * + 1, 获取一天的开始时间,并格式化为yyyy-MM-dd 0:00:00
	 * @param date 指定日期
	 * 
	 * @return 指定日期的开始时间
	 */
	public static String getTheDayBegin(final Date date) {
		if (date == null) {
			return null;
		}
		
		return formatDatetimeSafely(date, "yyyy-MM-dd 0:00:00");
	}
	
	/**
	 * + 2, 获取一天的结束时间,并格式化为yyyy-MM-dd 23:59:59
	 * @param date 指定日期
	 * 
	 * @return 指定日期的结束时间
	 */
	public static String getTheDayEnd(final Date date) {
		if (date == null) {
			return null;
		}
		
		return formatDatetimeSafely(date, "yyyy-MM-dd 23:59:59");
	}
	
	/**
	 * + 3, 格式化日期(线程安全)
	 * @param date 日期
	 * @param pattern 合格的日期时间格式
	 * 
	 * @return 格式化后的日期时间串
	 */
	public static String formatDatetimeSafely(final Date date, final String pattern) {
		if(date == null || pattern == null || pattern.trim().equals("")) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(pattern.trim()).format(date);
		} catch (Exception e) {
			logger.error("日期格式化错误[pattern:" + pattern + "]!");
		}
		
		return null;
	}
	
	/**
	 * + 4, 解析日期(线程安全)
	 * @param source 待解析的日期串
	 * @param pattern 日期格式
	 * 
	 * @return 解析后的日期
	 */
	public static Date parseDatetimeSafely(final String source, final String pattern) {
		if(source == null || source.trim().equals("") || pattern == null || pattern.trim().equals("")) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(pattern.trim()).parse(source.trim());
		} catch (Exception e) {
			logger.error("日期解析错误[source:" + source + ", pattern:" + pattern + "]!");
		}
		
		return null;
	}
	
	/**
	 * + 5, 获取给定日期所在月份的天数
	 * @param date 给定日期
	 * 
	 * @return 该月天数
	 */
	public static int getDaysOfTheMonth(final Date date) {
		if(date == null) {
			return 0;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		
		return c.getActualMaximum(Calendar.DAY_OF_MONTH);
	}
	/**
	 * + 6, 获取给定日期所在月份的第一天
	 * @param date 给定日期
	 * 
	 * @return 日期值
	 */
	public static Date getFirstOfTheMonth(final Date date) {
		if(date == null) {
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.DAY_OF_MONTH, 1); // 第一天
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();
	}
	/**
	 * + 6.1, 获取给定日期所在月份的第一天，并且格式化
	 * @param date 给定日期
	 * @param pattern 给定日期格式
	 * 
	 * @return 日期值
	 */
	public static String getFirstOfTheMonth(final Date date, final String pattern) {
		Date dateNew = getFirstOfTheMonth(date);
		if(dateNew == null || pattern == null || pattern.trim().equals("")) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(pattern.trim()).format(dateNew);
		} catch (Exception e) {
			logger.error("日期格式化错误[pattern:" + pattern + "]!");
		}
		
		return null;
	}
	
	/**
	 * + 7, 获取给定日期所在月份的最后一天
	 * @param date 给定日期
	 * 
	 * @return 日期值
	 */
	public static Date getLastOfTheMonth(final Date date) {
		if(date == null) {
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.MONTH, +1); //下个月
		c.set(Calendar.DAY_OF_MONTH, 1); //下个月第1天
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.MILLISECOND, -1); // 减去1毫秒,回到上月最后一天
		
		return c.getTime();
	}
	/**
	 * + 7.1, 获取给定日期所在月份的最后一天，并且格式化
	 * @param date 给定日期
	 * @param pattern 给定日期格式
	 * 
	 * @return 日期值
	 */
	public static String getLastOfTheMonth(final Date date, final String pattern) {
		Date dateNew = getLastOfTheMonth(date);
		if(dateNew == null || pattern == null || pattern.trim().equals("")) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(pattern.trim()).format(dateNew);
		} catch (Exception e) {
			logger.error("日期格式化错误[pattern:" + pattern + "]!");
		}
		
		return null;
	}
	
	/**
	 * + 8, 获取给定日期所在星期的第一天
	 * @param date 给定日期
	 * @param weekStyle 1-周日为第一天，其他(默认)-周一为第一天
	 * 
	 * @return 日期值
	 */
	public static Date getFirstOfTheWeek(final Date date, int weekStyle) {
		if(date == null) {
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (weekStyle == 1) { //第一天(周日)
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		} else { //第一天(周一),遇到周日,需要前移一周
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				c.add(Calendar.DAY_OF_MONTH, -7);
			}
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		
		return c.getTime();
	}
	/**
	 * + 8.1, 获取给定日期所在星期的第一天,并且格式化
	 * @param date 给定日期
	 * @param weekStyle 1-周日为第一天，其他(默认)-周一为第一天
	 * @param pattern 给定日期格式
	 * 
	 * @return 日期值
	 */
	public static String getFirstOfTheWeek(final Date date, int weekStyle, final String pattern) {
		Date dateNew = getFirstOfTheWeek(date, weekStyle);
		if(dateNew == null || pattern == null || pattern.trim().equals("")) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(pattern.trim()).format(dateNew);
		} catch (Exception e) {
			logger.error("日期格式化错误[pattern:" + pattern + "]!");
		}
		
		return null;
	}
	
	/**
	 * + 9, 获取给定日期所在星期的最后一天
	 * @param date 给定日期
	 * @param weekStyle 1-周日为第一天，其他(默认)-周一为第一天
	 * 
	 * @return 日期值
	 */
	public static Date getLastOfTheWeek(final Date date, final int weekStyle) {
		if(date == null) {
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (weekStyle == 1) { //第一天(周日)
			c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
		} else {  //第一天(周一),遇到周日,需要前移一周
			if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
				c.add(Calendar.DAY_OF_MONTH, -7);
			}
			c.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
		}
		c.add(Calendar.DAY_OF_MONTH, +7); //下周第一天
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		c.set(Calendar.MILLISECOND, 0);
		c.add(Calendar.MILLISECOND, -1); // 减去1毫秒,回到上周最后一天
		
		return c.getTime();
	}
	/**
	 * + 9.1, 获取给定日期所在星期的最后一天,并且格式化
	 * @param date 给定日期
	 * @param weekStyle 1-周日为第一天，其他(默认)-周一为第一天
	 * @param pattern 给定日期格式
	 * 
	 * @return 日期值
	 */
	public static String getLastOfTheWeek(final Date date, final int weekStyle, final String pattern) {
		Date dateNew = getLastOfTheWeek(date, weekStyle);
		if(dateNew == null || pattern == null || pattern.trim().equals("")) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(pattern.trim()).format(dateNew);
		} catch (Exception e) {
			logger.error("日期格式化错误[pattern:" + pattern + "]!");
		}
		
		return null;
	}
	
	/**
	 * + 10, 把当前时间加/减一段时间后算出新的时间
	 * @param date 当前时间
	 * @param datetimeType 1-year,2-month,3-day,4-hour,5-minute,6-seconds,7-milliseconds
	 * @param amount 时间增量,可以是正负
	 * 
	 * @return 新的时间
	 */
	public static Date getDatetimeDuring(final Date date, final int datetimeType, final int amount) {
		if(date == null) {
			return null;
		}
		
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		if (datetimeType == 1) { // year
			c.add(Calendar.YEAR, amount);
		} else if (datetimeType == 2) { // month
			c.add(Calendar.MONTH, amount);
		} else if (datetimeType == 3) { // day
			c.add(Calendar.DAY_OF_MONTH, amount);
		} else if (datetimeType == 4) { // hour
			c.add(Calendar.HOUR_OF_DAY, amount);
		} else if (datetimeType == 5) { // minute
			c.add(Calendar.MINUTE, amount);
		} else if (datetimeType == 6) { // seconds
			c.add(Calendar.SECOND, amount);
		} else if (datetimeType == 7) { // milliseconds
			c.add(Calendar.MILLISECOND, amount);
		}
		
		return c.getTime();
	}
	/**
	 * + 10.1, 把当前时间加/减一段时间后算出新的时间,并且格式化
	 * @param date 当前时间
	 * @param datetimeType 1-year,2-month,3-day,4-hour,5-minute,6-seconds,7-milliseconds
	 * @param amount 时间增量,可以是正负
	 * @param pattern 给定日期格式
	 * 
	 * @return 新的时间
	 */
	public static String getDatetimeDuring(final Date date, final int datetimeType, final int amount, final String pattern) {
		Date dateNew = getDatetimeDuring(date, datetimeType, amount);
		if(dateNew == null || pattern == null || pattern.trim().equals("")) {
			return null;
		}
		
		try {
			return new SimpleDateFormat(pattern.trim()).format(dateNew);
		} catch (Exception e) {
			logger.error("日期格式化错误[pattern:" + pattern + "]!");
		}
		
		return null;
	}
	
	/**
	 * +主方法
	 * @param args 入口参数
	 */
	public static void main(String[] args) {
		Date date = DatetimeUtils.parseDatetimeSafely("2020-04-01", "yyyy-MM-dd");
		String week1 = DatetimeUtils.getFirstOfTheWeek(date, 0, "yyyy-MM-dd");// 取得周一的日期
		String week2 = DatetimeUtils.getLastOfTheWeek(date, 0, "yyyy-MM-dd");// 取得周日的日期
		
		System.out.println(week1);
		System.out.println(week2);
	}
}
