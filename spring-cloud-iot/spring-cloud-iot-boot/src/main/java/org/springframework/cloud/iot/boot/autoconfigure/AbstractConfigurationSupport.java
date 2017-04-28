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

import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
//import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.cloud.iot.boot.properties.GpioConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.RaspberryConfigurationProperties;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

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
	protected IotConfigurationProperties buildIotProperties() {
		IotConfigurationProperties properties = new IotConfigurationProperties();
		try {
			Binder.get(environment).bind(IotConfigurationProperties.NAMESPACE, Bindable.ofInstance(properties));
		} catch (Exception e) {
			throw new RuntimeException("Unable to bind properties", e);
		}
		return properties;
	}

	protected RaspberryConfigurationProperties buildRaspberryProperties() {
		RaspberryConfigurationProperties properties = new RaspberryConfigurationProperties();
		try {
			Binder.get(environment).bind(RaspberryConfigurationProperties.NAMESPACE, Bindable.ofInstance(properties));
		} catch (Exception e) {
			throw new RuntimeException("Unable to bind properties", e);
		}
		return properties;
	}

	/**
	 * Builds the gpio properties.
	 *
	 * @return the gpio configuration properties
	 */
	protected GpioConfigurationProperties buildGpioProperties() {
		GpioConfigurationProperties properties = new GpioConfigurationProperties();
		try {
			Binder.get(environment).bind(GpioConfigurationProperties.NAMESPACE, Bindable.ofInstance(properties));
		} catch (Exception e) {
			throw new RuntimeException("Unable to bind properties", e);
		}
		return properties;
	}

	protected Termistor getTermistor(int i2cBus, int i2cAddr, double voltageSupply, int dacBits, int resistance, double beta,
			double referenceTemp) {
		try {
			return new Termistor(i2cBus, i2cAddr, voltageSupply, dacBits, resistance, beta, referenceTemp);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
}
