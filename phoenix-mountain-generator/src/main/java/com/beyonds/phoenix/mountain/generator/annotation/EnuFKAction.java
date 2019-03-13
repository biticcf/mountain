/**
 * 
 */
package com.beyonds.phoenix.mountain.generator.annotation;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年3月13日
 * @Time:   上午9:09:00
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
