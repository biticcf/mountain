/**
 * 
 */
package com.github.biticcf.mountain.web.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * author: Daniel.Cao
 * date:   2019年1月5日
 * time:   下午2:36:56
 *
 */
@RestController
public class DefaultErrorController {
	@RequestMapping(value = {"/error"}, method = {RequestMethod.GET})
    public void error(HttpServletRequest request, HttpServletResponse response) throws NoHandlerFoundException {
		Object obj = request.getAttribute("javax.servlet.error.request_uri");
		String errorUrl = obj == null? null : obj.toString();
		
        throw new NoHandlerFoundException(request.getMethod(), errorUrl, new HttpHeaders());
    }
}
