/**
 *
 */
package com.beyonds.phoenix.mountain.core.common.result;

import java.io.Serializable;

/**
 * web返回结果定义
 * @Author: Daniel.Cao
 * @Date:   2018年11月19日
 * @Time:   下午8:59:57
 *
 * @param <T> 业务类型
 */
public class ReturnResult<T> implements Serializable {
	private static final long serialVersionUID = -8379100651362895882L;
	
	private   int       status;
	private   String    message;
	private   T         data;
	
	private   PaginationMeta  meta; //分页信息或者null
	
	public ReturnResult(int status) {
		this(status, null);
	}
	
	public ReturnResult(int status, String message) {
		this(status, message, null);
	}
	
	public ReturnResult(int status, String message, T data) {
		this(status, message, data, null);	
	}
	
	public ReturnResult(int status, String message, T data, PaginationMeta meta) {
		this.status = status;
		this.message = message;
		this.data = data;
		
		this.meta = meta;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public PaginationMeta getMeta() {
		return meta;
	}
	public void setMeta(PaginationMeta meta) {
		this.meta = meta;
	}
}
