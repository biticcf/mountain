/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.model;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.time.DateFormatUtils;

/**
 * 
 * @Author: DanielCao
 * @Date:   2017年5月9日
 * @Time:   下午7:11:02
 *
 */
public abstract class WdBaseModel implements Serializable {
	private static final long serialVersionUID = -4060588360322778384L;
	
	@Override
	public String toString() {
		try {
			return (new ReflectionToStringBuilder(this, ToStringStyle.DEFAULT_STYLE) {
				@Override
				protected Object getValue(final Field field) 
						throws IllegalArgumentException, IllegalAccessException {
					if (Date.class.isAssignableFrom(field.getType())) {
						Object _value = field.get(this.getObject());
						if (_value != null) {
							return DateFormatUtils.format(
									(Date) _value, 
									"yyyy-MM-dd HH:mm:ss");
						}
						
						return _value;
					} else {
						return super.getValue(field);
					}
				}
			}).setExcludeFieldNames("password", "passwd").toString(); 
        } catch (Exception e) {
            // NOTICE: 这样做的目的是避免由于toString()的异常导致系统异常终止
            // 大部分情况下，toString()用在日志输出等调试场景
            return super.toString();
        }
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
