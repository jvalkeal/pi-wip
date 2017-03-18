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
import org.springframework.cloud.iot.pi4j.component.sound.Pi4jPassiveBuzzer;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;

public class Pi4jPassiveBuzzerGpioFactoryBean extends AbstractComponentGpioFactoryBean<Pi4jPassiveBuzzer> {

	private final String pinName;

	public Pi4jPassiveBuzzerGpioFactoryBean(GpioController gpioController, NumberingScheme numberingScheme, String pinName) {
		super(gpioController, numberingScheme, Pi4jPassiveBuzzer.class);
		this.pinName = pinName;
	}

	@Override
	protected Pi4jPassiveBuzzer createInstanceInternal() throws Exception {
		GpioPinPwmOutput output;
		Pin pin = resolvePin(pinName);
		if (pin.getSupportedPinModes().contains(PinMode.PWM_OUTPUT)) {
			output = getGpioController().provisionPwmOutputPin(pin, 0);
		} else {
			output = getGpioController().provisionSoftPwmOutputPin(pin, 0);
		}
		return new Pi4jPassiveBuzzer(output);
	}
}
