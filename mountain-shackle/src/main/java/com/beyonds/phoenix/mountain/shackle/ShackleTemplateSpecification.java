/**
 * 
 */
package com.beyonds.phoenix.mountain.shackle;

import java.util.Arrays;
import java.util.Objects;

import org.springframework.cloud.context.named.NamedContextFactory;

/**
 * author: Daniel.Cao
 * date:   2019年1月3日
 * time:   下午6:59:39
 *
 */
class ShackleTemplateSpecification implements NamedContextFactory.Specification {
	private String name;

	private Class<?>[] configuration;

	public ShackleTemplateSpecification() {}

	public ShackleTemplateSpecification(String name, Class<?>[] configuration) {
		this.name = name;
		this.configuration = configuration;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Class<?>[] getConfiguration() {
		return configuration;
	}

	public void setConfiguration(Class<?>[] configuration) {
		this.configuration = configuration;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ShackleTemplateSpecification that = (ShackleTemplateSpecification) o;
		return Objects.equals(name, that.name) &&
				Arrays.equals(configuration, that.configuration);
	}

	@Override
	public int hashCode() {
		return Objects.hash(name, configuration);
	}

	@Override
	public String toString() {
		return new StringBuilder("ShackleTemplateSpecification{")
				.append("name='").append(name).append("', ")
				.append("configuration=").append(Arrays.toString(configuration))
				.append("}").toString();
	}
}
