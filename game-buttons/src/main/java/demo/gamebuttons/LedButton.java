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
package demo.gamebuttons;

import java.util.Random;

import org.springframework.cloud.iot.support.IotUtils;

/**
 * Enumeration of a supported {@code LedButton}s.
 *
 * @author Janne Valkealahti
 *
 */
public enum LedButton {

	/** led1 as 0x80=10000000 */
	LED1(0x80),
	/** led2 as 0x40=01000000 */
	LED2(0x40),
	/** led3 as 0x20=00100000 */
	LED3(0x20),
	/** led4 as 0x10=00010000 */
	LED4(0x10);

	private static final Random random = new Random();
	private final int value;

	/**
	 * Instantiates a new led button.
	 *
	 * @param value the value
	 */
	private LedButton(int value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Gets the sets of {@code LedButton}s.
	 *
	 * @param ledButtons the led buttons
	 * @return the sets the
	 */
	public static int getSet(LedButton... ledButtons) {
		int value = 0;
		for (LedButton ledButton : ledButtons) {
			value = IotUtils.setBit(value, ledButton.getValue());
		}
		return value;
	}

	/**
	 * Gets a random {@code LedButton}.
	 *
	 * @return the led button
	 */
	public static LedButton random() {
		return LedButton.class.getEnumConstants()[random.nextInt(4)];
	}
}
