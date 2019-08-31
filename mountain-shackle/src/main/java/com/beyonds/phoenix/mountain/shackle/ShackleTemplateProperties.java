/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * author: Daniel.Cao
 * date:   2019年1月4日
 * time:   上午12:42:16
 *
 */
@ConfigurationProperties("shackle.template")
public class ShackleTemplateProperties {
	private boolean defaultToProperties = true;

	private String defaultConfig = "default";

	private Map<String, ShackleTemplateConfiguration> config = new HashMap<>();

	public boolean isDefaultToProperties() {
		return defaultToProperties;
	}

	public void setDefaultToProperties(boolean defaultToProperties) {
		this.defaultToProperties = defaultToProperties;
	}

	public String getDefaultConfig() {
		return defaultConfig;
	}

	public void setDefaultConfig(String defaultConfig) {
		this.defaultConfig = defaultConfig;
	}

	public Map<String, ShackleTemplateConfiguration> getConfig() {
		return config;
	}

	public void setConfig(Map<String, ShackleTemplateConfiguration> config) {
		this.config = config;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShackleTemplateProperties that = (ShackleTemplateProperties) o;
		return defaultToProperties == that.defaultToProperties &&
				Objects.equals(defaultConfig, that.defaultConfig) &&
				Objects.equals(config, that.config);
	}

	@Override
	public int hashCode() {
		return Objects.hash(defaultToProperties, defaultConfig, config);
	}

	public static class ShackleTemplateConfiguration {
		private Class<Contract> contract;
		
		public Class<Contract> getContract() {
			return contract;
		}

		public void setContract(Class<Contract> contract) {
			this.contract = contract;
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			ShackleTemplateConfiguration that = (ShackleTemplateConfiguration) o;
			
			return Objects.equals(contract, that.contract);
		}

		@Override
		public int hashCode() {
			return Objects.hash(contract);
		}
	}
}
