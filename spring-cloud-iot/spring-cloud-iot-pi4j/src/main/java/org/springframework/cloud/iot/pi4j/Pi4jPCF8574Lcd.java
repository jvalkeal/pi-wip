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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.component.Lcd;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.util.Assert;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;

/**
 * {@code Pi4jPCF8574Lcd} is {@link Lcd} device using PCF8574 IC2
 * interface controlling a lcd.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jPCF8574Lcd extends LifecycleObjectSupport implements Lcd {

	private final Logger log = LoggerFactory.getLogger(Pi4jPCF8574Lcd.class);
	private final I2CLcdDisplay lcd;
	private boolean clearTextOnExit = true;

	/**
	 * Instantiates a new Pi4jPCF8574Lcd.
	 *
	 * @param lcd the instance of I2CLcdDisplay
	 */
	public Pi4jPCF8574Lcd(I2CLcdDisplay lcd) {
		Assert.notNull(lcd, "I2CLcdDisplay lcd instance must be set");
		this.lcd = lcd;
	}

	@Override
	public void setText(String text) {
		log.debug("Writing text {}", text);
		lcd.clear();
		lcd.write(text);
	}

	@Override
	protected void doDestroy() {
		if (clearTextOnExit) {
			lcd.clear();
		}
	}

	/**
	 * Sets if text is cleared when device is stopped.
	 *
	 * @param clearTextOnExit the new clear text on exit
	 */
	public void setClearTextOnExit(boolean clearTextOnExit) {
		this.clearTextOnExit = clearTextOnExit;
	}

}
