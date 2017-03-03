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
import org.springframework.util.Assert;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

/**
 * {@code Pi4jHD44780} provides functionality to use HD44780 devices
 * without additional backpacks which are normally used to reduce
 * amount of needed pins. For example {@code Pi4jHD44780} at minimum
 * will need 6 pins assuming 4-bit mode is used and rwPin is forced
 * to write only.
 * <p>
 * At maximum 11 pins are needed with 8-bit mode if rwPin needs to be
 * used with read mode as well.
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jHD44780 extends AbstractPi4jHD44780 {

	private final static Logger log = LoggerFactory.getLogger(Pi4jHD44780.class);

	private final GpioPinDigitalOutput rsPin;
	private final GpioPinDigitalOutput rwPin;
	private final GpioPinDigitalOutput ePin;
	private final GpioPinDigitalOutput d0Pin;
	private final GpioPinDigitalOutput d1Pin;
	private final GpioPinDigitalOutput d2Pin;
	private final GpioPinDigitalOutput d3Pin;
	private final GpioPinDigitalOutput d4Pin;
	private final GpioPinDigitalOutput d5Pin;
	private final GpioPinDigitalOutput d6Pin;
	private final GpioPinDigitalOutput d7Pin;

	public Pi4jHD44780(GpioPinDigitalOutput rsPin, GpioPinDigitalOutput ePin, GpioPinDigitalOutput d4Pin,
			GpioPinDigitalOutput d5Pin, GpioPinDigitalOutput d6Pin, GpioPinDigitalOutput d7Pin) {
		this(rsPin, null, ePin, null, null, null, null, d4Pin, d5Pin, d6Pin, d7Pin);
	}

	public Pi4jHD44780(GpioPinDigitalOutput rsPin, GpioPinDigitalOutput rwPin, GpioPinDigitalOutput ePin,
			GpioPinDigitalOutput d0Pin, GpioPinDigitalOutput d1Pin, GpioPinDigitalOutput d2Pin,
			GpioPinDigitalOutput d3Pin, GpioPinDigitalOutput d4Pin, GpioPinDigitalOutput d5Pin,
			GpioPinDigitalOutput d6Pin, GpioPinDigitalOutput d7Pin) {
		super(d0Pin != null);
		Assert.notNull(rsPin, "'rsPin' must be set");
		Assert.notNull(ePin, "'ePin' must be set");
		if (d0Pin != null || d1Pin != null || d2Pin != null || d3Pin != null) {
			Assert.notNull(d0Pin, "'d0Pin' must be set");
			Assert.notNull(d1Pin, "'d1Pin' must be set");
			Assert.notNull(d2Pin, "'d2Pin' must be set");
			Assert.notNull(d3Pin, "'d3Pin' must be set");
		}
		Assert.notNull(d4Pin, "'d4Pin' must be set");
		Assert.notNull(d5Pin, "'d5Pin' must be set");
		Assert.notNull(d6Pin, "'d6Pin' must be set");
		Assert.notNull(d7Pin, "'d7Pin' must be set");
		this.rsPin = rsPin;
		this.rwPin = rwPin;
		this.ePin = ePin;
		this.d0Pin = d0Pin;
		this.d1Pin = d1Pin;
		this.d2Pin = d2Pin;
		this.d3Pin = d3Pin;
		this.d4Pin = d4Pin;
		this.d5Pin = d5Pin;
		this.d6Pin = d6Pin;
		this.d7Pin = d7Pin;
	}

	@Override
	protected void write4Bit(int data, boolean registerSelect, boolean writeUpperBits) {
		Pi4jUtils.delay(1);
		rsPin.setState(registerSelect);

		// bits to pins are written in reverse order meaning
		// last 8th bit goes to first pin. with 4bit mode
		// we need to write twice and this is a first take.
		// i.e. if incoming data is 00110011
		// we shift by 4 getting 00000011 and we're ready
		// to process last 4 bits ending with 00000011.
		// no need to do logical and with as it's done
		// in take 2 as shifting makes sure first 4 bits gets zeroed.
		if (writeUpperBits) {
			int myData = data >> 4;
			// check last bit and set d4 pin
			d4Pin.setState((myData & 0x01) == 0x01);
			// shift by one to prepare check for d5, and so on...
			myData >>= 0x01;
			d5Pin.setState((myData & 0x01) == 0x01);
			myData >>= 0x01;
			d6Pin.setState((myData & 0x01) == 0x01);
			myData >>= 0x01;
			d7Pin.setState((myData & 0x01) == 0x01);
			log.debug("Pin states for d4,d5,d6,d7 after upper bits {} {} {} {}", d4Pin.getState(), d5Pin.getState(),
					d6Pin.getState(), d7Pin.getState());
		} else {
			int myData = data & 0x0F;
			d4Pin.setState((myData & 0x01) == 0x01);
			myData >>= 0x01;
			d5Pin.setState((myData & 0x01) == 0x01);
			myData >>= 0x01;
			d6Pin.setState((myData & 0x01) == 0x01);
			myData >>= 0x01;
			d7Pin.setState((myData & 0x01) == 0x01);
			log.debug("Pin states for d4,d5,d6,d7 after lower bits {} {} {} {}", d4Pin.getState(), d5Pin.getState(),
					d6Pin.getState(), d7Pin.getState());
		}
	}

	@Override
	protected void instructExecute(int data, boolean registerSelect, boolean writeUpperBits) {
		// pulsing ePin instructs hd44780 to read data
		// from pins based on their states
		ePin.setState(PinState.HIGH);
		ePin.setState(PinState.LOW);
	}
}
