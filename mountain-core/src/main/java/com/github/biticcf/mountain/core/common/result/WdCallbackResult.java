/**
 * 
 */
package com.github.biticcf.mountain.core.common.result;

/**
 * +返回结果数据结构定义
 * author: DanielCao
 * date:   2017年5月8日
 * time:   下午1:51:51
 *
 * @param <T> 结果对象类型
 */
public final class WdCallbackResult<T> {
	private static final String SUCCESS_MSG = "SUCCESS";
	private static final String SYS_ERROR = "SYS_ERROR";
	
    private int             resultCode;      //返回代码
    private String          resultMessage;   //返回信息
    
    private T               businessObject;  //返回业务数据
    private Throwable       throwable;       //返回异常
    
    private boolean         isSuccess;       //是否成功
    
    private WdCallbackResult(
    		int resultCode,
    		String resultMessage,
    		T businessObject,
    		Throwable throwable,
    		boolean isSuccess) {
    	this.resultCode = resultCode;
    	this.resultMessage = resultMessage;
    	this.businessObject = businessObject;
    	this.throwable = throwable;
    	
    	this.isSuccess = isSuccess;
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @return 结果对象
     */
    public static <T> WdCallbackResult<T> success(int resultCode) {
    	return success(resultCode, null);
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @param businessObject 结果业务对象
     * @return 结果集
     */
    public static <T> WdCallbackResult<T> success(
    		int resultCode,
    		T businessObject) {
    	return success(resultCode, SUCCESS_MSG, businessObject);
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @param resultMessage 结果信息对象
     * @param businessObject 结果业务对象
     * @return 结果集
     */
    public static <T> WdCallbackResult<T> success(
    		int resultCode,
    		String resultMessage,
    		T businessObject) {
    	return new WdCallbackResult<T>(resultCode, resultMessage, businessObject, null, true);
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @return 结果集
     */
    public static <T> WdCallbackResult<T> failure(int resultCode) {
    	return failure(resultCode, SYS_ERROR);
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @param throwable 异常
     * @return 结果集
     */
    public static <T> WdCallbackResult<T> failure(
    		int resultCode,
    		Throwable throwable) {
    	return failure(resultCode, SYS_ERROR, throwable);
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @param resultMessage 结果消息
     * @return 结果集
     */
    public static <T> WdCallbackResult<T> failure(
    		int resultCode,
    		String resultMessage) {
    	return failure(resultCode, resultMessage, null, null);
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @param resultMessage 结果消息
     * @param throwable 异常
     * @return 结果集
     */
    public static <T> WdCallbackResult<T> failure(
    		int resultCode,
    		String resultMessage,
    		Throwable throwable) {
    	return failure(resultCode, resultMessage, null, throwable);
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @param resultMessage 结果消息
     * @param businessObject 结果对象
     * @return 结果集
     */
    public static <T> WdCallbackResult<T> failure(
    		int resultCode,
    		String resultMessage,
    		T businessObject) {
    	return failure(resultCode, resultMessage, businessObject, null);
    }
    
    /**
     * @param <T> 结果对象类型
     * @param resultCode 结果code
     * @param resultMessage 结果消息
     * @param businessObject 结果对象
     * @param throwable 异常
     * @return 结果集
     */
    public static <T> WdCallbackResult<T> failure(
    		int resultCode,
    		String resultMessage,
    		T businessObject,
    		Throwable throwable) {
    	return new WdCallbackResult<T>(resultCode, resultMessage, businessObject, throwable, false);
    }
    
	public int getResultCode() {
		return resultCode;
	}
	public String getResultMessage() {
		return resultMessage;
	}
	public T getBusinessObject() {
		return businessObject;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public boolean isSuccess() {
		return isSuccess;
	}
	public boolean isFailure() {
		return !isSuccess;
	}
}
