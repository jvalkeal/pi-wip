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

import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties.NumberingScheme;
import org.springframework.cloud.iot.pi4j.Pi4jLed;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 * Factory bean handling {@link Pi4jLed}.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jLedGpioFactoryBean extends AbstractComponentGpioFactoryBean<Pi4jLed> {

	private Boolean illuminateOnStart;
	private Boolean illuminateOnExit;
	private String pinName;

	public Pi4jLedGpioFactoryBean(GpioController gpioController, NumberingScheme numberingScheme, String pinName,
			Boolean illuminateOnStart, Boolean illuminateOnExit) {
		super(gpioController, numberingScheme, Pi4jLed.class);
		this.pinName = pinName;
		this.illuminateOnStart = illuminateOnStart;
		this.illuminateOnExit = illuminateOnExit;
	}

	@Override
	protected Pi4jLed createInstanceInternal() throws Exception {
		GpioPinDigitalOutput pin = getGpioController().provisionDigitalOutputPin(resolvePin(pinName));
		return new Pi4jLed(pin, illuminateOnStart, illuminateOnExit);
	}
}
