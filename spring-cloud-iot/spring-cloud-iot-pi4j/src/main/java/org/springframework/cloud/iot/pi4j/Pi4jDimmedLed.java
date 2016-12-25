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
package org.springframework.cloud.iot.pi4j;

import org.springframework.cloud.iot.component.DimmedLed;

import com.pi4j.io.gpio.GpioPinPwmOutput;

public class Pi4jDimmedLed implements DimmedLed {

	private final GpioPinPwmOutput output;
	private int modulation;

	public Pi4jDimmedLed(GpioPinPwmOutput output) {
		this.output = output;
	}

	@Override
	public void setEnabled(boolean enabled) {
		output.setPwm(enabled ? modulation : 0);
	}

	@Override
	public void setPercentage(int percent) {
		if (percent < 0) {
			modulation = 0;
		} else if (percent > 100) {
			modulation = 100;
		} else {
			modulation = percent;
		}
		output.setPwm(modulation);
	}
}
