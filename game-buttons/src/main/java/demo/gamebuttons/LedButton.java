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

public enum LedButton {

	LED1(0x80),
	LED2(0x40),
	LED3(0x20),
	LED4(0x10);

	private static final Random random = new Random();
	private final int value;

	private LedButton(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static int getSet(LedButton... ledButtons) {
		int value = 0;
		for (LedButton ledButton : ledButtons) {
			value = IotUtils.setBit(value, ledButton.getValue());
		}
		return value;
	}

	public static LedButton random() {
		return LedButton.class.getEnumConstants()[random.nextInt(4)];
	}
}
