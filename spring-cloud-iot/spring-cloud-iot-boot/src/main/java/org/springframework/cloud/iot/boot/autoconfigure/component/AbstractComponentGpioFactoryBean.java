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
package org.springframework.cloud.iot.boot.autoconfigure.component;

import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties.NumberingScheme;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Base factory creating gpio components.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the type of component
 */
public abstract class AbstractComponentGpioFactoryBean<T> extends AbstractFactoryBean<T> {

	private final GpioController gpioController;
	private final NumberingScheme numberingScheme;
	private final Class<T> clazz;

	public AbstractComponentGpioFactoryBean(GpioController gpioController, NumberingScheme numberingScheme, Class<T> clazz) {
		this.gpioController = gpioController;
		this.numberingScheme = numberingScheme;
		this.clazz = clazz;
	}

	@Override
	public Class<?> getObjectType() {
		return clazz;
	}

	protected Pin resolvePin(String pinName) {
		return numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + pinName)
				: RaspiPin.getPinByName("GPIO " + pinName);
	}

	protected GpioController getGpioController() {
		return gpioController;
	}
}
