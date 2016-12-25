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

import org.springframework.cloud.iot.component.Lcd;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;

public class Pi4jPCF8574Lcd implements Lcd {

	private I2CLcdDisplay lcd;

	public Pi4jPCF8574Lcd(I2CLcdDisplay lcd) {
		this.lcd = lcd;
	}

	@Override
	public void setText(String text) {
		lcd.write(text);
	}
}
