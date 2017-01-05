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

public class IotUtils {

	public static int map(int x, int in_min, int in_max, int out_min, int out_max) {
		return (x - in_min) * (out_max - out_min) / (in_max - in_min) + out_min;
	}

	static public double getSteinhartHartTemperature(double analog, float vSupply, int bits, int r, int b2550, double refTemp) {
		double vR = vSupply * analog / (Math.pow(2, bits) - 1);
		double rT = r * vR / (vSupply - vR);
		double kelvin = 1 / (((Math.log(rT / r)) / b2550) + (1 / (273.15 + refTemp)));
		return kelvin - 273.15;
	}

}
