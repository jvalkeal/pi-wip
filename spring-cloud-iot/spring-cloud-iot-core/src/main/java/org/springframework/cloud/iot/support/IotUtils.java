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
	 * Get the steinhart hart temperature which is based on value connected
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

	/**
	 * Checks if bits are set by doing 'bitwise and' operation
	 * with value and flags.
	 *
	 * @param value the value
	 * @param flags the flags
	 * @return true, if is bit set
	 */
	public static boolean isBitSet(int value, int flags) {
		return (flags & value) == flags;
	}

	/**
	 * Sets the bits for value using 'bitwise or' operation
	 * and return new modified value.
	 *
	 * @param value the value
	 * @param flags the flags
	 * @return the modified value
	 */
	public static int setBit(int value, int flags) {
		return (flags | value);
	}

	/**
	 * Unset bits for value using 'bitwise and' operation
	 * and return modified value.
	 *
	 * @param value the value
	 * @param flags the flags
	 * @return the modified value
	 */
	public static int unsetBit(int value, int flags) {
		return value &= flags;
	}

	/**
	 * Returns the values from each provided array combined into a single array.
	 * For example, {@code concat(new byte[] {a, b}, new byte[] {}, new byte[]
	 * {c}} returns the array {@code {a, b, c}}.
	 *
	 * @param arrays zero or more {@code byte} arrays
	 * @return a single array containing all the values from the source arrays, in order
	 */
	public static byte[] concat(byte[]... arrays) {
		int length = 0;
		for (byte[] array : arrays) {
			length += array.length;
		}
		byte[] result = new byte[length];
		int pos = 0;
		for (byte[] array : arrays) {
			System.arraycopy(array, 0, result, pos, array.length);
			pos += array.length;
		}
		return result;
	}
}
