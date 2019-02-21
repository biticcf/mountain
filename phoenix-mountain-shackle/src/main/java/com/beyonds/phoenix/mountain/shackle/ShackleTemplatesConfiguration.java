/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

/**
 * @Author: Daniel.Cao
 * @Date:   2019年1月3日
 * @Time:   下午4:08:41
 *
 */
@Configuration
public class ShackleTemplatesConfiguration {
	@Bean
	@Scope("prototype")
	@ConditionalOnMissingBean
	public Shackle.Builder shackleBuilder() {
		return Shackle.builder();
	}
	
	@Bean
	@ConditionalOnMissingBean
	public Contract shackleContract() {
		return new Contract.Default();
	}
}
