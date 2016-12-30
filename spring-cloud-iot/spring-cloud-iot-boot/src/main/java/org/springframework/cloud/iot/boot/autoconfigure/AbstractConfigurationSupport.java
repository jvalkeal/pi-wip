/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.iot.boot.autoconfigure;

import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

/**
 * Support class for various configuration classes.
 *
 * @author Janne Valkealahti
 *
 */
public class AbstractConfigurationSupport implements EnvironmentAware {

	private ConfigurableEnvironment environment;

	@Override
	public void setEnvironment(Environment environment) {
		Assert.isInstanceOf(ConfigurableEnvironment.class, environment,
				"environment must be of type ConfigurableEnvironment but was " + environment);
		this.environment = (ConfigurableEnvironment)environment;
	}

	/**
	 * Gets the environment.
	 *
	 * @return the environment
	 */
	public ConfigurableEnvironment getEnvironment() {
		return environment;
	}

	/**
	 * Builds iot configuration properties.
	 *
	 * @return the iot configuration properties
	 */
	protected IotConfigurationProperties buildProperties() {
		IotConfigurationProperties iotConfigurationProperties = new IotConfigurationProperties();
		PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(iotConfigurationProperties);
		factory.setTargetName(IotConfigurationProperties.NAMESPACE);
		factory.setPropertySources(environment.getPropertySources());
		try {
			factory.bindPropertiesToTarget();
		} catch (BindException e) {
			throw new RuntimeException("Unable to bind properties", e);
		}
		return iotConfigurationProperties;
	}
}