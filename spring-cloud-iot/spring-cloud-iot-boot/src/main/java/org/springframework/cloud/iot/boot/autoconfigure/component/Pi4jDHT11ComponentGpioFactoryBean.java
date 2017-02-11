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
import org.springframework.cloud.iot.pi4j.component.Pi4jDHT11HumiditySensor;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.PinMode;

/**
 * Factory bean creating DHT11 sensor component.
 *
 * @author jvalkealahti
 *
 */
public class Pi4jDHT11ComponentGpioFactoryBean extends AbstractComponentGpioFactoryBean<Pi4jDHT11HumiditySensor> {

	private final String pinName;

	public Pi4jDHT11ComponentGpioFactoryBean(GpioController gpioController, NumberingScheme numberingScheme, String pinName) {
		super(gpioController, numberingScheme, Pi4jDHT11HumiditySensor.class);
		this.pinName = pinName;
	}

	@Override
	protected Pi4jDHT11HumiditySensor createInstance() throws Exception {
		GpioPinDigitalMultipurpose pin = getGpioController().provisionDigitalMultipurposePin(resolvePin(pinName), PinMode.DIGITAL_OUTPUT);
		Pi4jDHT11HumiditySensor sensor = new Pi4jDHT11HumiditySensor(pin);
		sensor.afterPropertiesSet();
		return sensor;
	}
}
