/**
 *
 */
package com.github.biticcf.mountain.web.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;

import com.github.biticcf.mountain.core.common.lang.Logable;

/**
 * 
 * author: Daniel.Cao
 * date:   2019年8月31日
 * time:   下午2:05:19
 *
 */
@ControllerAdvice
public abstract class BaseController implements Logable {
	protected static Log logger = LogFactory.getLog("WEB.LOG");
	
	public BaseController() {
		
	}
}
