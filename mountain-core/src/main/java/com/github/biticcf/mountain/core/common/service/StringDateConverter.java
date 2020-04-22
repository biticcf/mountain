/**
 * 
 */
package com.github.biticcf.mountain.core.common.service;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.converter.Converter;

import com.github.biticcf.mountain.core.common.util.DatetimeUtils;

/**
 * author DanielCao
 * date 2016年12月9日
 * time 下午1:41:01
 * +处理输入日期类型
 */
public class StringDateConverter implements Converter<String, Date> {
	private static Log logger = LogFactory.getLog(StringDateConverter.class);
	
	private static final String TIME_PATTERN_REGEX = "^\\d{1,13}$";
	
	private static final String[] DATE_TIME_PATTERNS = new String[] {
			"yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd HH:mm",
			"yyyy-MM-dd HH",
			"yyyy-MM-dd"
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
		for (String _pattern : DATE_TIME_PATTERNS) {
			result = DatetimeUtils.parseDatetimeSafely(_src, _pattern);
			if (result != null) {
				return result;
			}
		}
		logger.warn("[" + source + "]无法转化为日期!");
		
		return null;
	}
}
