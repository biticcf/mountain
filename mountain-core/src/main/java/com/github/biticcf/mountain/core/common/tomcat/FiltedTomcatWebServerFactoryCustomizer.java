/**
 * 
 */
package com.github.biticcf.mountain.core.common.tomcat;

import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.embedded.TomcatWebServerFactoryCustomizer;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.boot.web.embedded.tomcat.ConfigurableTomcatWebServerFactory;
import org.springframework.core.env.Environment;

/**
 * author:  Daniel.Cao
 * date:    2019年12月17日
 * time:    下午3:37:14
 *
 */
public class FiltedTomcatWebServerFactoryCustomizer extends TomcatWebServerFactoryCustomizer {
	private final ServerProperties serverProperties;
	private final FiltedAccessLogProperties filtedServerProperties;
	
	public FiltedTomcatWebServerFactoryCustomizer(Environment environment, 
			ServerProperties serverProperties, FiltedAccessLogProperties filtedServerProperties) {
		super(environment, serverProperties);
		
		this.serverProperties = serverProperties;
		this.filtedServerProperties = filtedServerProperties;
	}

	@Override
	public void customize(ConfigurableTomcatWebServerFactory factory) {
		boolean accesslogEnable = serverProperties.getTomcat().getAccesslog().isEnabled();
		// 阻止父节点进行AccessLog组装
		serverProperties.getTomcat().getAccesslog().setEnabled(false);
		super.customize(factory);
		
		// 恢复设置
		serverProperties.getTomcat().getAccesslog().setEnabled(accesslogEnable);
		
		// 配置AccessLog
		if (accesslogEnable) {
			customizeAccessLog(factory);
		}
	}
	
	/**
	 * 
	 * @param factory factory
	 */
	private void customizeAccessLog(ConfigurableTomcatWebServerFactory factory) {
		ServerProperties.Tomcat tomcatProperties = this.serverProperties.getTomcat();
		FiltedAccessLogValve valve = new FiltedAccessLogValve();
		PropertyMapper map = PropertyMapper.get();
		ServerProperties.Tomcat.Accesslog accessLogConfig = tomcatProperties.getAccesslog();
		map.from(accessLogConfig.getConditionIf()).to(valve::setConditionIf);
		map.from(accessLogConfig.getConditionUnless()).to(valve::setConditionUnless);
		map.from(accessLogConfig.getPattern()).to(valve::setPattern);
		map.from(accessLogConfig.getDirectory()).to(valve::setDirectory);
		map.from(accessLogConfig.getPrefix()).to(valve::setPrefix);
		map.from(accessLogConfig.getSuffix()).to(valve::setSuffix);
		map.from(accessLogConfig.getEncoding()).whenHasText().to(valve::setEncoding);
		map.from(accessLogConfig.getLocale()).whenHasText().to(valve::setLocale);
		map.from(accessLogConfig.isCheckExists()).to(valve::setCheckExists);
		map.from(accessLogConfig.isRotate()).to(valve::setRotatable);
		map.from(accessLogConfig.isRenameOnRotate()).to(valve::setRenameOnRotate);
		map.from(accessLogConfig.getMaxDays()).to(valve::setMaxDays);
		map.from(accessLogConfig.getFileDateFormat()).to(valve::setFileDateFormat);
		map.from(accessLogConfig.isIpv6Canonical()).to(valve::setIpv6Canonical);
		map.from(accessLogConfig.isRequestAttributesEnabled()).to(valve::setRequestAttributesEnabled);
		map.from(accessLogConfig.isBuffered()).to(valve::setBuffered);
		
		// 添加不输出日志的请求
		map.from(this.filtedServerProperties.getFiltedRequestEntries()).to(valve::setFiltedRequestEntries);
		
		factory.addEngineValves(valve);
	}
}
