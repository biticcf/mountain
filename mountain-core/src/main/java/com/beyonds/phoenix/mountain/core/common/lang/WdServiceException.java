/**
 * 
 */
package com.beyonds.phoenix.mountain.core.common.lang;

/**
 * 
 * author: Daniel.Cao
 * date:   2019年8月31日
 * time:   下午1:43:44
 *
 */
public class WdServiceException extends WdRuntimeException {
	private static final long serialVersionUID = -9060890028943801760L;

	public WdServiceException(int errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }

    public WdServiceException(int errorCode, String message) {
        super(errorCode, message);
    }
    
    public WdServiceException(int errorCode, String message, String desc) {
        super(errorCode, message, desc);
    }

    public WdServiceException(int errorCode, Throwable cause) {
        super(errorCode, cause);
    }

    public WdServiceException(int errorCode) {
        super(errorCode);
    }
}
