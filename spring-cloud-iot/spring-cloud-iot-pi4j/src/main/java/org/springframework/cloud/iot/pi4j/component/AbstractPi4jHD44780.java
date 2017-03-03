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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.iot.pi4j.support.Pi4jUtils;
import org.springframework.cloud.iot.support.LifecycleObjectSupport;
import org.springframework.util.StringUtils;

/**
 * The {@code HD44780} LCD controller is an alphanumeric dot matrix liquid
 * crystal display (LCD) controller developed by Hitachi.
 * <p>
 * Base implementation for {@code HD44780} support. Low level control and
 * operating logic is handled in this class and then implementations are
 * required to implement needed logic with supported hardware interfaces.
 * <p>
 * Implementation is required to work with same low level data as normal
 * {@code HD44780} chip is operated via pins. Other implementations may
 * have I2C or SPI interfaces.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class AbstractPi4jHD44780 extends LifecycleObjectSupport {

	private final static Logger log = LoggerFactory.getLogger(AbstractPi4jHD44780.class);

	// Commands
	private final static int LCD_HOME = 0x02;			// # 00000010
	private final static int LCD_CLEAR = 0x01;			// # 00000001
	private final static int LCD_ENTRY = 0x04;			// # 00000100
	private final static int LCD_CTRL = 0x08;			// # 00001000
	private final static int LCD_CDSHIFT = 0x10;		// # 00010000
	private final static int LCD_FUNC = 0x20;			// # 00100000
	private final static int LCD_CGRAM = 0x40;			// # 01000000
	private final static int LCD_DGRAM = 0x80;			// # 10000000

	// Entry register
	private final static int LCD_ENTRY_SH = 0x01;		// # 00000001
	private final static int LCD_ENTRY_ID = 0x02;		// # 00000010

	// Control register
	private final static int LCD_DISPLAY_CTRL = 0x04;	// # 00000100
	private final static int LCD_CURSOR_CTRL = 0x02;	// # 00000010
	private final static int LCD_BLINK_CTRL = 0x01;		// # 00000001

	// Function register
	private final static int LCD_FUNC_F = 0x04;			// # 00000100
	private final static int LCD_FUNC_N = 0x08;			// # 00001000
	private final static int LCD_FUNC_DL = 0x10;		// # 00010000

	private final static int LCD_CDSHIFT_RL = 0x04;		// # 00000100

	private final boolean bit4Mode;
	private final int[] LCD_LINE_ADDRESS      = { 0x80, 0xC0, 0x94, 0xD4 };

	protected AbstractPi4jHD44780(boolean bit4Mode) {
		this.bit4Mode = bit4Mode;
	}

	@Override
	protected void onInit() throws Exception {
		initLcd4Bit();
	}

	public void lcdClear() {
		write4BitInternal(LCD_CLEAR, false, true);
		write4BitInternal(LCD_CLEAR, false, false);
		Pi4jUtils.delay(5);
		write4BitInternal(LCD_HOME, false, true);
		write4BitInternal(LCD_HOME, false, false);
		Pi4jUtils.delay(5);
	}

	public void lcdWrite(String text) {
		log.debug("Writing data '{}'", text);
		if (!StringUtils.hasText(text)) {
			return;
		}
		for (int i = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			write4BitInternal(c, true, true);
			write4BitInternal(c, true, false);
		}
	}

	public void setCursorPosition(int row, int column) {
		Pi4jUtils.delay(5);
		int pos = LCD_LINE_ADDRESS[row] + column;
		write4BitInternal(pos, false, true);
		write4BitInternal(pos, false, false);
	}

	protected void initLcd4Bit() {
		log.info("Starting lcd initialisation sequence");
		// for next 4 writes we're still on default 8 bit mode
		write4BitInternal(LCD_FUNC | LCD_FUNC_DL, false, true); // #00110000
		write4BitInternal(LCD_FUNC | LCD_FUNC_DL, false, true);
		write4BitInternal(LCD_FUNC | LCD_FUNC_DL, false, true);
		write4BitInternal(LCD_FUNC, false, true);// # 00100000

		write4BitInternal(LCD_FUNC | LCD_FUNC_N, false, true);// # 00101000
		write4BitInternal(LCD_FUNC | LCD_FUNC_N, false, false);// # 00101000
		write4BitInternal(LCD_CTRL | LCD_DISPLAY_CTRL, false, true);// # 00001100
		write4BitInternal(LCD_CTRL | LCD_DISPLAY_CTRL, false, false);// # 00001100
		write4BitInternal(LCD_ENTRY | LCD_ENTRY_ID, false, true);// # 00000110
		write4BitInternal(LCD_ENTRY | LCD_ENTRY_ID, false, false);// # 00000110
		write4BitInternal(LCD_CLEAR, false, true);// # 00000001
		write4BitInternal(LCD_CLEAR, false, false);// # 00000001
	}

	protected abstract void write4Bit(int data, boolean registerSelect, boolean writeUpperBits);

	protected abstract void instructExecute(int data, boolean registerSelect, boolean writeUpperBits);

	private void write4BitInternal(int data, boolean registerSelect, boolean writeUpperBits) {
		Pi4jUtils.delay(1);
		log.debug("write4BitInternal {} {} {}", registerSelect, writeUpperBits, String.format("%8s", Integer.toBinaryString(data & 0xFF)).replace(' ', '0'));
		write4Bit(data, registerSelect, writeUpperBits);
		instructExecute(data, registerSelect, writeUpperBits);
	}

}
