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

import org.springframework.cloud.iot.component.ShiftRegister;
import org.springframework.cloud.iot.support.IotUtils;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

/**
 *
 *   +---------+
 * 1-|Q1    VCC|-16
 * 2-|Q2     Q0|-15
 * 3-|Q3    SER|-14
 * 4-|Q4     OE|-13
 * 5-|Q5   RCLK|-12
 * 6-|Q6  SRCLK|-11
 * 7-|Q7  SRCLR|-10
 * 8-|GRD   Q7'|-9
 *   +---------+
 *
 * SER - Data input
 * RCLK - Input to storage register, Rising edge from shift to memory
 * SRCLK - Input to shift register, Rising edge data is shifted.
 * OE - Output enabled, active at low level
 * SRCLR - Reset active, active at low level
 *
 * @author Janne Valkealahti
 *
 */
public class Pi4jShiftRegister implements ShiftRegister {

	private final GpioPinDigitalOutput sdi;
	private final GpioPinDigitalOutput rclk;
	private final GpioPinDigitalOutput srclk;

	/**
	 * Instantiates a new pi4j shift register.
	 *
	 * @param sdi the sdi output
	 * @param rclk the rclk output
	 * @param srclk the srclk output
	 */
	public Pi4jShiftRegister(GpioPinDigitalOutput sdi, GpioPinDigitalOutput rclk, GpioPinDigitalOutput srclk) {
		this.sdi = sdi;
		this.rclk = rclk;
		this.srclk = srclk;
	}

	@Override
	public void shift(int bits) {
		// check individual bits and set sdi
		// to high or low. below table shows
		// a logic used in for loop.
		// 128 0x80 10000000
		// 64  0x40 01000000
		// 32  0x20 00100000
		// 16  0x10 00010000
		// 8   0x08 00001000
		// 4   0x04 00000100
		// 2   0x02 00000010
		// 1   0x01 00000001
		for (int i = 128; i >= 1; i /= 2) {
			sdi.setState(IotUtils.isBitSet(i, bits));
			srclk.setState(PinState.HIGH);
			srclk.setState(PinState.LOW);
		}
	}

	@Override
	public void store() {
		// bounce rclk to transfer stuff from
		// a register into a memory
		rclk.setState(PinState.HIGH);
		rclk.setState(PinState.LOW);
	}
}
