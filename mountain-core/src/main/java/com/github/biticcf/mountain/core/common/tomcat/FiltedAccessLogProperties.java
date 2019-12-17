/**
 * 
 */
package com.github.biticcf.mountain.core.common.tomcat;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.github.biticcf.mountain.core.common.tomcat.FiltedAccessLogValve.RequestEntry;

/**
 * author:  Daniel.Cao
 * date:    2019年12月17日
 * time:    下午2:06:33
 *
 */
@ConfigurationProperties(prefix = "server.tomcat.accesslog", ignoreUnknownFields = true)
public class FiltedAccessLogProperties {
	private List<RequestEntry> filtedRequestEntries = new ArrayList<>();
	
	/**
	 * 
	 * @return filtedRequestEntries
	 */
	public List<RequestEntry> getFiltedRequestEntries() {
		return filtedRequestEntries;
	}

	/**
	 * 
	 * @param filtedRequestEntries filtedRequestEntries
	 */
	public void setFiltedRequestEntries(List<RequestEntry> filtedRequestEntries) {
		this.filtedRequestEntries = filtedRequestEntries;
	}
}
