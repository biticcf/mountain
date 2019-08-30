/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;

/**
 * @author DanielCao
 * @date 2016年12月9日
 * @time 下午1:41:01
 * 处理输入日期类型
 */
public class StringDateConverter implements Converter<String, Date> {
	private static Logger logger = LoggerFactory.getLogger(StringDateConverter.class);
	
	private static final String TIME_PATTERN_REGEX = "^\\d{1,13}$";
	
	private static ThreadLocal<SimpleDateFormat[]> dateFormatLocal = new ThreadLocal<SimpleDateFormat[]>() {
		@Override
		protected SimpleDateFormat[] initialValue() {
			return new SimpleDateFormat[] {
					new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"),
					new SimpleDateFormat("yyyy-MM-dd HH:mm"),
					new SimpleDateFormat("yyyy-MM-dd HH"),
					new SimpleDateFormat("yyyy-MM-dd")
				};
		}
	};
	
	@Override
	public Date convert(final String source) {
		if (source == null || source.trim().equals("")) {
			return null;
		}
		
		Date result = null;
		String _src = source.trim();
		// 1,数字类型
		if (_src.matches(TIME_PATTERN_REGEX)) {
			try {
				long lTime = Long.parseLong(_src);
				if (_src.length() > 10) {
					result = new Date(lTime);
				} else {
					result =  new Date(1000L * lTime);
				}
			} catch (Exception e) {
				result = null;
				
				logger.warn("[" + source + "]无法转化为日期!");
			}
			
			return result;
		}
		// 2,日期类型
		SimpleDateFormat[] dateFormats = dateFormatLocal.get();
		for (SimpleDateFormat dateFormat : dateFormats) {
			try {
				dateFormat.setLenient(false);
				
				return dateFormat.parse(source);
			} catch (ParseException e) {
				logger.warn("[" + source + "]无法转化为" + dateFormat.toPattern() + "格式的日期!");
			}
		}
		
		return null;
	}
}
