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
import org.springframework.cloud.iot.pi4j.component.Pi4jHD44780;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;

/**
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jHD44780ComponentFactoryBean extends AbstractComponentGpioFactoryBean<Pi4jHD44780> {

	private final String rsPinName;
	private final String ePinName;
	private final String d4PinName;
	private final String d5PinName;
	private final String d6PinName;
	private final String d7PinName;

	public Pi4jHD44780ComponentFactoryBean(GpioController gpioController, NumberingScheme numberingScheme, String rsPinName,
			String ePinName, String d4PinName, String d5PinName, String d6PinName, String d7PinName) {
		super(gpioController, numberingScheme, Pi4jHD44780.class);
		this.rsPinName = rsPinName;
		this.ePinName = ePinName;
		this.d4PinName = d4PinName;
		this.d5PinName = d5PinName;
		this.d6PinName = d6PinName;
		this.d7PinName = d7PinName;
	}

	@Override
	protected Pi4jHD44780 createInstanceInternal() throws Exception {
		GpioPinDigitalOutput rsPin = getGpioController().provisionDigitalOutputPin(resolvePin(rsPinName));
		GpioPinDigitalOutput ePin = getGpioController().provisionDigitalOutputPin(resolvePin(ePinName));
		GpioPinDigitalOutput d4Pin = getGpioController().provisionDigitalOutputPin(resolvePin(d4PinName));
		GpioPinDigitalOutput d5Pin = getGpioController().provisionDigitalOutputPin(resolvePin(d5PinName));
		GpioPinDigitalOutput d6Pin = getGpioController().provisionDigitalOutputPin(resolvePin(d6PinName));
		GpioPinDigitalOutput d7Pin = getGpioController().provisionDigitalOutputPin(resolvePin(d7PinName));

		Pi4jHD44780 pi4jHD44780 = new Pi4jHD44780(rsPin, ePin, d4Pin, d5Pin, d6Pin, d7Pin);
		pi4jHD44780.afterPropertiesSet();
		return pi4jHD44780;
	}
}
