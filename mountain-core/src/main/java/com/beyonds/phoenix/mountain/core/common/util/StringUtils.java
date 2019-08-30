/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author  DanielCao
 * @date    2015年4月1日
 * @time    下午8:09:09
 *
 */
public final class StringUtils {
	//key value pair pattern.
	private static final Pattern KVP_PATTERN = Pattern.compile("([_.a-zA-Z0-9][-_.a-zA-Z0-9]*)[=](.*)");
	
	private StringUtils() {
		
	}
	
	/**
	 * 判断一个字符串是否空串,其中null,""," "都算作空串
	 * @param str 输入值
	 * @return 输出值
	 */
	public static boolean isEmpty(final String str) {
		if (str == null) {
			return true;
		}
		
		String _tmpStr = str.trim();
		if ("".equals(_tmpStr)) {
			return true;
		}
		
		return false;
	}
	/**
	 * 判断一个字符串是否是数字
	 * @param str 输入值
	 * @param type 1-不允许带符号,2-允许带符号(-\+)
	 * @return 输出值
	 */
	public static boolean isNumeric(final String str, final int type) {
		if (isEmpty(str)) {
			return false;
		}
		if (type < 1 || type > 2) {
			return false;
		}
		String _tmpString = str.trim();
		int _length = _tmpString.length();
		// 不包含符号位
		if (type == 1 || _length == 1) {
			return org.apache.commons.lang3.StringUtils.isNumeric(_tmpString);
		}
		//判断符号位
		String _firstStr = _tmpString.substring(0, 1);
		if ("+".equals(_firstStr) || "-".equals(_firstStr)) {
			return org.apache.commons.lang3.StringUtils.isNumeric(_tmpString.substring(1));
		} else {
			return org.apache.commons.lang3.StringUtils.isNumeric(_tmpString);
		}
	}
	/**
	 * 
	 * @param str 输入值
	 * @return 输出值
	 */
	public static boolean isUnsignedNumeric(final String str) {
		return isNumeric(str, 1);
	}
	/**
	 * 
	 * @param str 输入值
	 * @return 输出值
	 */
	public static boolean isSignedNumeric(final String str) {
		return isNumeric(str, 2);
	}
	/**
	 * 根据pattern拼字符串
	 * @param src 输入值
	 * @param posSign 输入值
	 * @return 输出值
	 */
	public static String makeStringByParam(final String src, final String posSign) {
		if (StringUtils.isEmpty(posSign)) {
			return src;
	    }
		return makeStringByParams(src, new String[] {posSign.trim()});
	}
	/**
	 * 根据pattern拼字符串
	 * @param src 输入值
	 * @param posSigns 输入值
	 * @return 输出值
	 */
	public static String makeStringByParams(final String src, final String[] posSigns) {
		if (StringUtils.isEmpty(src)) {
			return "";
		}
		if (posSigns == null || posSigns.length == 0) {
			return src;
		}
		Object[] _objs = new Object[posSigns.length];
		System.arraycopy(posSigns, 0, _objs, 0, posSigns.length);
		
		return MessageFormat.format(src, _objs);
	}
	/**
	 * 字符串转化为int,失败的话返回默认值
	 * @param str 输入值
	 * @param defaultValue 输入值
	 * @return 输出值
	 */
	public static int parseInt(final String str, final int defaultValue) {
		if (!isSignedNumeric(str)) {
			return defaultValue;
		}
		int _intValue = defaultValue;
		try {
			_intValue = Integer.parseInt(str.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return _intValue;
	}
	/**
	 * 字符串转化为Long,失败的话返回默认值
	 * @param str 输入值
	 * @param defaultValue 输入值
	 * @return 输出值
	 */
	public static long parseLong(final String str, final long defaultValue) {
		if (!isSignedNumeric(str)) {
			return defaultValue;
		}
		long _longValue = defaultValue;
		try {
			_longValue = Long.parseLong(str.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _longValue;
	}
	/**
	 * 字符串转化为Long,失败的话返回默认值
	 * @param str 输入值
	 * @param defaultValue 输入值
	 * @return 输出值
	 */
	public static byte parseByte(final String str, final byte defaultValue) {
		if (!isSignedNumeric(str)) {
			return defaultValue;
		}
		byte _byteValue = defaultValue;
		try {
			_byteValue = Byte.parseByte(str.trim());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return _byteValue;
	}
	/**
     * parse query string to Parameters.
     * 
     * @param qs query string.
     * @return Parameters instance.
     */
	public static Map<String, String> parseQueryString(String qs) {
	    if (qs == null || qs.length() == 0) {
            return new HashMap<String, String>();
	    }
	    
	    return parseKeyValuePair(qs, "\\&");
	}
	
	/**
	 * 主要用来在解析json时遇到的"null"串
	 * @param src 输入值
	 * @param defaultString 输入值
	 * @return 输出值
	 */
	public static String parserString(String src, String defaultString) {
		if (src == null) {
			return defaultString;
		}
		src = src.trim();
		if ("null".equalsIgnoreCase(src)) {
			return defaultString;
		}
		
		return src;
	}
	/**
	 * parse key-value pair.
	 * 
	 * @param str string.
	 * @param itemSeparator item separator.
	 * @return key-value map;
	 */
	private static Map<String, String> parseKeyValuePair(String str, String itemSeparator) {
		String[] _tmp = str.split(itemSeparator);
		Map<String, String> _map = new HashMap<String, String>(_tmp.length);
		for (int _i = 0; _i < _tmp.length; _i++) {
			Matcher _matcher = KVP_PATTERN.matcher(_tmp[_i]);
			if (!_matcher.matches()) {
				continue;
			}
			_map.put(_matcher.group(1), _matcher.group(2));
		}
		
		return _map;
	}
	/**
	 * 把数字num拼成长度是length的字符串,
	 * 长度不足时在前面补filler
	 * 超长时返回num整串
	 * 如果num小于0,负号变为1
	 * 
	 * @param num 输入值
	 * @param length 输入值
	 * @param filler 输入值
	 * @return 输出值
	 */
	public static String makeStringFromNumber(int num, int length,  char filler) {
		StringBuilder _sb = new StringBuilder();
		if (num < 0) {
			_sb.append("1");
			_sb.append(0 - num);
		} else {
			_sb.append(num);
		}
		String _tmpString = _sb.toString();
		if (length <= 0 || _tmpString.length() >= length) {
			return _tmpString;
		}
		StringBuilder _sbTmp = new StringBuilder();
		for (int _i = 0; _i < length - _tmpString.length(); _i++) {
			_sbTmp.append(filler);
		}
		_sbTmp.append(_tmpString);
		
		return _sbTmp.toString();
	}
	
	/**
	 * 检查是否合法用户名
	 * @param loginName 输入值
	 * @return 输出值
	 */
	public static boolean checkLoginName(final String loginName) {
		if (loginName == null) {
			return false;
		}
		
		return loginName.matches("^[a-zA-Z]\\w{5,24}$"); // _a-zA-Z0-9,6至25长度,第一位必须是字母
	}
	/**
	 * 检查是否合法手机号
	 * @param mobile 输入值
	 * @return 输出值
	 */
	public static boolean checkMobile(final String mobile) {
		if (mobile == null) {
			return false;
		}
		return mobile.matches("^1\\d{10}$");
	}
	/**
	 * 检查是否合法邮箱地址
	 * @param email 输入值
	 * @return 输出值
	 */
	public static boolean checkEmail(final String email) {
		if (email == null) {
			return false;
		}
		
		return email.matches("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
	}
}
