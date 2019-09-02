/**
 * 
 */
package com.github.biticcf.mountain.generator.annotation;

/**
 * author: Daniel.Cao
 * date:   2019年3月13日
 * time:   上午9:09:00
 *
 */
public enum EnuFKAction {
	CASCADE("CASCADE"), 
	NO_ACTION("NO ACTION"), 
	RESTRICT("RESTRICT"), 
	SET_NULL("SET NULL");
	
	private String desc;
	
	private EnuFKAction(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}
}
