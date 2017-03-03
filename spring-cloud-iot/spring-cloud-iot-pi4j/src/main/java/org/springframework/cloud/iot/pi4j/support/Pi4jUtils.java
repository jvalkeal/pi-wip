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
package org.springframework.cloud.iot.pi4j.support;

import com.pi4j.wiringpi.Gpio;

public abstract class Pi4jUtils {

	private static boolean NATIVE_SUPPORT = false;

	static {
		try {
			Gpio.delay(0);
			NATIVE_SUPPORT = true;
		} catch (Throwable e) {
		}
	}

	public static void delay(int millis) {
		if (NATIVE_SUPPORT) {
			Gpio.delay(millis);
		} else {
			try {
				Thread.sleep(millis);
			} catch (InterruptedException e) {
			}
		}
	}

	public static void delayMicroseconds(int micros) {
		if (NATIVE_SUPPORT) {
			Gpio.delayMicroseconds(micros);
		} else {
			try {
				Thread.sleep(0, micros*1000);
			} catch (InterruptedException e) {
			}
		}
	}

}
