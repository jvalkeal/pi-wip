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

import java.util.Collection;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties.NumberingScheme;
import org.springframework.cloud.iot.pi4j.Pi4jButton;
import org.springframework.cloud.iot.properties.ReferenceMode;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinPullResistance;

/**
 * {@link FactoryBean} building instanses of a {@link Pi4jButton}.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jButtonGpioFactoryBean extends AbstractComponentGpioFactoryBean<Pi4jButton> {

	private final String pinName;
	private final ReferenceMode referenceMode;

	/**
	 * Instantiates a new Pi4jButtonGpioFactoryBean.
	 *
	 * @param gpioController the gpio controller
	 * @param numberingScheme the numbering scheme
	 * @param tags the tags
	 * @param pinName the pin name
	 * @param referenceMode the reference mode
	 */
	public Pi4jButtonGpioFactoryBean(GpioController gpioController, NumberingScheme numberingScheme, Collection<String> tags, String pinName,
			ReferenceMode referenceMode) {
		super(gpioController, numberingScheme, tags, Pi4jButton.class);
		this.pinName = pinName;
		this.referenceMode = referenceMode;
	}

	@Override
	protected Pi4jButton createInstanceInternal() throws Exception {
		GpioPinDigitalInput input = getGpioController().provisionDigitalInputPin(resolvePin(pinName));
		// setup needed pull resistance depending on configured mode.
		// needed for correct event matching depending
		// if reference voltage in button is connected to ground or vcc
		if (referenceMode == ReferenceMode.GND) {
			input.setPullResistance(PinPullResistance.PULL_UP);
		} else if (referenceMode == ReferenceMode.VCC) {
			input.setPullResistance(PinPullResistance.PULL_DOWN);
		}
		return new Pi4jButton(input);
	}
}
