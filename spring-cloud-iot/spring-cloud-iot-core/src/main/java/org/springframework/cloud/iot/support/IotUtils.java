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
package org.springframework.cloud.iot.support;

/**
 * IoT utilities.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class IotUtils {

	/**
	 * Gets the steinhart hart temperature which is based on value connected
	 * to ADC as Kelvin.
	 *
	 * @param analogValue the analog singal value
	 * @param voltageSupply the system voltage supply
	 * @param dacBits the ADC bits
	 * @param resistance the resistance
	 * @param beta the beta parameter value
	 * @param referenceTemp the refeference temp
	 * @return the steinhart hart temperature
	 */
	static public double getSteinhartHartTemperature(float analogValue, double voltageSupply, int dacBits, int resistance, double beta,
			double referenceTemp) {
		double vR = voltageSupply * analogValue / (Math.pow(2, dacBits) - 1);
		double rT = resistance * vR / (voltageSupply - vR);
		double kelvin = 1 / (((Math.log(rT / resistance)) / beta) + (1 / (273.15 + referenceTemp)));
		return kelvin;
	}
}
