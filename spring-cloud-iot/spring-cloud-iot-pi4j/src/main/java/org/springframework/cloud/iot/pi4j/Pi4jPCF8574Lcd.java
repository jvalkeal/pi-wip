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

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.component.Lcd;
import org.springframework.cloud.iot.component.display.LayoutManager;
import org.springframework.cloud.iot.properties.LayoutProperties;
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
	private boolean clearOnExit = true;

	private Map<String, LayoutProperties> layout;
	private LayoutManager layoutManager;

	/**
	 * Instantiates a new Pi4jPCF8574Lcd.
	 *
	 * @param lcd the instance of I2CLcdDisplay
	 */
	public Pi4jPCF8574Lcd(I2CLcdDisplay lcd) {
		Assert.notNull(lcd, "I2CLcdDisplay lcd instance must be set");
		this.lcd = lcd;
	}

	/**
	 * Instantiates a new Pi4jPCF8574Lcd.
	 *
	 * @param lcd the instance of I2CLcdDisplay
	 */
	public Pi4jPCF8574Lcd(I2CLcdDisplay lcd, Map<String, LayoutProperties> layout) {
		Assert.notNull(lcd, "I2CLcdDisplay lcd instance must be set");
		this.lcd = lcd;
		this.layoutManager = new LayoutManager(layout);
	}

	@Override
	public void setText(String text) {
		log.debug("Writing text {}", text);
		lcd.clear();
		lcd.write(text);
	}

	@Override
	public void setText(String segment, String text) {
		layoutManager.setContent(segment, text);
		lcd.clear();
		Character[][] layoutContent = layoutManager.layoutContent();
		for (int i = 0; i < layoutContent.length; i++) {
			lcd.setCursorPosition(i);
			char[] data = new char[layoutContent[i].length];
			for (int j = 0; j < layoutContent[i].length; j++) {
				Character c = layoutContent[i][j];
				if (c != null) {
					data[j] = c;
				} else {
					data[j] = ' ';
				}
			}
			lcd.write(data);
		}

	}

	@Override
	protected void doDestroy() {
		if (clearOnExit) {
			lcd.clear();
		}
	}



	/**
	 * Sets if text is cleared when device is stopped.
	 * If {@code #clearOnExit} is passed as {@code NULL}
	 * value is reset to default {@code TRUE} value.
	 *
	 * @param clearOnExit the new clear text on exit
	 */
	public void setClearOnExit(Boolean clearOnExit) {
		if (clearOnExit != null) {
			this.clearOnExit = clearOnExit;
		} else {
			this.clearOnExit = true;
		}
	}
}
