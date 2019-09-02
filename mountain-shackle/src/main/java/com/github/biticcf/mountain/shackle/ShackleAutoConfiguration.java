/**
 * 
 */
package com.github.biticcf.mountain.shackle;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.actuator.HasFeatures;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.github.biticcf.mountain.core.common.service.ReferContext;
import com.github.biticcf.mountain.core.common.service.WdServiceTemplate;

/**
 * author: Daniel.Cao
 * date:   2019年1月4日
 * time:   上午1:06:34
 *
 */
@Configuration
@ConditionalOnClass({Shackle.class, ReferContext.class, WdServiceTemplate.class})
@EnableConfigurationProperties({ShackleTemplateProperties.class})
public class ShackleAutoConfiguration {
	@Autowired(required = false)
	private List<ShackleTemplateSpecification> configurations = new ArrayList<>();
	
	@Bean
	public HasFeatures shackleFeature() {
		return HasFeatures.namedFeature("Shackle", Shackle.class);
	}
	
	@Bean
	public ShackleContext shackleContext() {
		ShackleContext context = new ShackleContext();
		context.setConfigurations(this.configurations);
		
		return context;
	}
}
