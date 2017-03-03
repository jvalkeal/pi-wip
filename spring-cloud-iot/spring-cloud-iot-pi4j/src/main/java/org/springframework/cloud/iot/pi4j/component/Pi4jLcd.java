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
package org.springframework.cloud.iot.pi4j.component;

import java.util.Map;

import org.springframework.cloud.iot.component.Lcd;
import org.springframework.cloud.iot.component.display.LayoutManager;
import org.springframework.cloud.iot.properties.LayoutProperties;

/**
 * {@code Pi4jLcd} is a implementation of a {@link Lcd} supporting
 * usual LCD alphanumeric dot matrix displays.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jLcd implements Lcd {

	private final AbstractPi4jHD44780 lcdDriver;
	private LayoutManager layoutManager;

	public Pi4jLcd(Pi4jPCF8574HD44780 pi4jPCF8574HD44780) {
		this.lcdDriver = pi4jPCF8574HD44780;
	}

	public Pi4jLcd(Pi4jHD44780 pi4jHD44780) {
		this.lcdDriver = pi4jHD44780;
	}

	@Override
	public void setText(String text) {
		lcdDriver.lcdClear();
		lcdDriver.lcdWrite(text);
	}

	public void setLayout(Map<String, LayoutProperties> layout) {
		if (layout != null) {
			this.layoutManager = new LayoutManager(layout);
		}
	}

	@Override
	public void setText(String segment, String text) {
		layoutManager.setContent(segment, text);
		lcdDriver.lcdClear();
		Character[][] layoutContent = layoutManager.layoutContent();
		for (int i = 0; i < layoutContent.length; i++) {
			lcdDriver.setCursorPosition(i, 0);
			char[] data = new char[layoutContent[i].length];
			for (int j = 0; j < layoutContent[i].length; j++) {
				Character c = layoutContent[i][j];
				if (c != null) {
					data[j] = c;
				} else {
					data[j] = ' ';
				}
			}
			lcdDriver.lcdWrite(new String(data));
		}
	}
}
