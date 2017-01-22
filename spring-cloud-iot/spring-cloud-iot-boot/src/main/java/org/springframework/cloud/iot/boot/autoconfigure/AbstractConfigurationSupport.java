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
import org.springframework.cloud.iot.IotSystemException;
import org.springframework.cloud.iot.boot.GpioConfigurationProperties;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.RaspberryConfigurationProperties;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;

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
		PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(properties);
		factory.setTargetName(IotConfigurationProperties.NAMESPACE);
		factory.setPropertySources(environment.getPropertySources());
		try {
			factory.bindPropertiesToTarget();
		} catch (BindException e) {
			throw new RuntimeException("Unable to bind properties", e);
		}
		return properties;
	}

	protected RaspberryConfigurationProperties buildRaspberryProperties() {
		RaspberryConfigurationProperties properties = new RaspberryConfigurationProperties();
		PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(properties);
		factory.setTargetName(RaspberryConfigurationProperties.NAMESPACE);
		factory.setPropertySources(environment.getPropertySources());
		try {
			factory.bindPropertiesToTarget();
		} catch (BindException e) {
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
		PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(properties);
		factory.setTargetName(GpioConfigurationProperties.NAMESPACE);
		factory.setPropertySources(environment.getPropertySources());
		try {
			factory.bindPropertiesToTarget();
		} catch (BindException e) {
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

	protected I2CLcdDisplay getI2CLcdDisplay(int i2cBus, int i2cAddr, int rows, int columns) {
		try {
			return new I2CLcdDisplay(rows, columns, i2cBus, i2cAddr, 3, 0, 1, 2, 7, 6, 5, 4);
		} catch (Exception e) {
			throw new IotSystemException("Error constructing I2CLcdDisplay", e);
		}
	}
}
