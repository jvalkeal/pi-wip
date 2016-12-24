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

import org.springframework.cloud.iot.component.TemperatureSensor;

import com.pi4j.component.temperature.impl.Tmp102;

/**
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jPCF8591TemperatureSensor implements TemperatureSensor {

	private Tmp102 tmp102;

	/**
	 * Instantiates a new pi4j pc f8591 temperature sensor.
	 *
	 * @param tmp102 the tmp102
	 */
	public Pi4jPCF8591TemperatureSensor(Tmp102 tmp102) {
		this.tmp102 = tmp102;
	}

	@Override
	public double getTemperature() {
		double analogVal = tmp102.getTemperature();
		double Vr = 5 * analogVal / 255;
		double Rt = 10000 * Vr / (5 - Vr);
		double t = 1 / (((Math.log(Rt / 10000)) / 3950) + (1 / (273.15 + 25)));
		t = t - 273.15;
		return t;
	}
}
