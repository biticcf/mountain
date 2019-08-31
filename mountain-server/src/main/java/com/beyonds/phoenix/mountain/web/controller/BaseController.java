/**
 *
 */
package com.beyonds.phoenix.mountain.web.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.beyonds.phoenix.mountain.core.common.lang.Logable;

/**
 * 
 * author: Daniel.Cao
 * date:   2019年8月31日
 * time:   下午2:05:19
 *
 */
@ControllerAdvice
public abstract class BaseController implements Logable {
	protected static Logger logger = LoggerFactory.getLogger("WEB.LOG");
	
	public BaseController() {
		
	}
}
