/**
 * 
 */
package com.github.biticcf.mountain.core.common.result;

import com.github.biticcf.mountain.core.common.model.WdBaseModel;

/**
 * 
 * author: DanielCao
 * date:   2017年5月9日
 * time:   下午7:11:24
 *
 * @param <T> 结果对象类型
 */
public final class CallResult<T> extends WdBaseModel {
	private static final long serialVersionUID = 1340732101243901485L;
	
	private boolean     isSuccess; //接口执行成功,取到结果,返回true
	
	private int         resultCode;      //返回代码
    private String      resultMessage;   //返回信息
    
	private T           businessResult;   //返回的查询结果
	private Throwable   throwable;        //返回异常信息

	private CallResult(
			boolean isSuccess, 
			int resultCode, 
			String resultMessage,
			T businessResult, 
			Throwable throwable) {
		super();
		this.isSuccess = isSuccess;
		this.resultCode = resultCode;
		this.resultMessage = resultMessage;
		this.businessResult = businessResult;
		this.throwable = throwable;
	}
	
	/**
	 * @param <T> 结果对象类型
	 * @param isSuccess 是否成功标志
	 * @param resultCode 返回代码
	 * @param resultMessage 返回信息
	 * @param businessResult 结果对象
	 * @param throwable 异常
	 * @return 返回结果
	 */
	public static <T> CallResult<T> makeCallResult(
			boolean isSuccess, 
			int resultCode, 
			String resultMessage,
			T businessResult, 
			Throwable throwable) {
		return new CallResult<T>(isSuccess, resultCode, resultMessage, businessResult, throwable);
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public int getResultCode() {
		return resultCode;
	}

	public String getResultMessage() {
		return resultMessage;
	}

	public T getBusinessResult() {
		return businessResult;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
