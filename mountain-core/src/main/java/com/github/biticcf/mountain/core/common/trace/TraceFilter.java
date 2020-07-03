/**
 * 
 */
package com.github.biticcf.mountain.core.common.trace;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import com.github.biticcf.mountain.core.common.util.SnowFlakeUtils;

/**
 * author: DanielCao
 * date:   2020-7-2
 * time:   21:14:25
 * + traceId拦截器
 */
public class TraceFilter implements Filter {
	private String prefix;
	
	public TraceFilter(String prefix) {
		this.prefix = prefix;
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		String _traceId = TraceContext.getTrace();
		// 当前线程已经处理过
		if (_traceId != null && !_traceId.trim().equals("")) {
			chain.doFilter(request, response);
			
			return;
		}
		
		// 生成新的traceId
		HttpServletRequest req = (HttpServletRequest) request;
		_traceId = req.getHeader(TraceContext.TRACE_ID);
		String _newTraceId = new SnowFlakeUtils().nextId(prefix);
		if (_traceId == null || _traceId.trim().equals("")) {
			_traceId = _newTraceId;
		} else {
			_traceId = _traceId + "," + _newTraceId;
		}
		TraceContext.addTrace(_traceId);
		
		chain.doFilter(request, response);
	}

}
