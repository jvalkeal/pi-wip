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

import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.util.ClassUtils;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.temperature.TemperatureScale;

@Ignore
@RunWith(MockitoJUnitRunner.class)
public class Pi4jHD44780Tests {

	@Mock
	private GpioPinDigitalOutput rsPin;
	@Mock
	private GpioPinDigitalOutput ePin;
	@Mock
	private GpioPinDigitalOutput d4Pin;
	@Mock
	private GpioPinDigitalOutput d5Pin;
	@Mock
	private GpioPinDigitalOutput d6Pin;
	@Mock
	private GpioPinDigitalOutput d7Pin;

	@Test
	public void testInit() {

		PinStateTracker tracker = new PinStateTracker(rsPin, ePin, d4Pin, d5Pin, d6Pin, d7Pin);

		Pi4jHD44780 pi4jHD44780 = new Pi4jHD44780(rsPin, ePin, d4Pin, d5Pin, d6Pin, d7Pin);
		pi4jHD44780.afterPropertiesSet();

		ArrayList<Boolean[]> pinStates = tracker.getPinStates();
		verify(rsPin,times(11)).setState(PinState.LOW);


	}

	private static class PinStateTracker {
		private final ArrayList<Boolean[]> pinStates = new ArrayList<>();
		private final Boolean[] currentPinStates = new Boolean[]{false, false, false, false, false, false};

		public PinStateTracker(GpioPinDigitalOutput rsPin, GpioPinDigitalOutput ePin, GpioPinDigitalOutput d4Pin,
				GpioPinDigitalOutput d5Pin, GpioPinDigitalOutput d6Pin, GpioPinDigitalOutput d7Pin) {
			doAnswer(i -> {
				PinState pinState = i.getArgument(0);
				currentPinStates[0] = pinState.equals(PinState.HIGH);
				return null;
			}).when(rsPin).setState(ArgumentMatchers.any());
			doAnswer(i -> {
				currentPinStates[0] = i.getArgument(0);
				return null;
			}).when(rsPin).setState(ArgumentMatchers.anyBoolean());

			doAnswer(i -> {
				PinState pinState = i.getArgument(0);
				if (!currentPinStates[1] && PinState.HIGH.equals(pinState)) {
					pinStates.add(new Boolean[] { currentPinStates[0], pinState.equals(PinState.HIGH), currentPinStates[2],
					currentPinStates[3], currentPinStates[4], currentPinStates[5] });
				}
				currentPinStates[1] = pinState.equals(PinState.HIGH);
				return null;
			}).when(ePin).setState(ArgumentMatchers.any());
			doAnswer(i -> {
				boolean pinState = i.getArgument(0);
				if (!currentPinStates[1] && pinState) {
					pinStates.add(new Boolean[] { currentPinStates[0], pinState, currentPinStates[2],
					currentPinStates[3], currentPinStates[4], currentPinStates[5] });
				}
				currentPinStates[1] = pinState;
				return null;
			}).when(ePin).setState(ArgumentMatchers.anyBoolean());

			doAnswer(i -> {
				PinState pinState = i.getArgument(0);
				currentPinStates[2] = pinState.equals(PinState.HIGH);
				return null;
			}).when(d4Pin).setState(ArgumentMatchers.any());
			doAnswer(i -> {
				currentPinStates[2] = i.getArgument(0);
				return null;
			}).when(d4Pin).setState(ArgumentMatchers.anyBoolean());

			doAnswer(i -> {
				PinState pinState = i.getArgument(0);
				currentPinStates[3] = pinState.equals(PinState.HIGH);
				return null;
			}).when(d5Pin).setState(ArgumentMatchers.any());
			doAnswer(i -> {
				currentPinStates[3] = i.getArgument(0);
				return null;
			}).when(d5Pin).setState(ArgumentMatchers.anyBoolean());

			doAnswer(i -> {
				PinState pinState = i.getArgument(0);
				currentPinStates[4] = pinState.equals(PinState.HIGH);
				return null;
			}).when(d6Pin).setState(ArgumentMatchers.any());
			doAnswer(i -> {
				currentPinStates[4] = i.getArgument(0);
				return null;
			}).when(d6Pin).setState(ArgumentMatchers.anyBoolean());

			doAnswer(i -> {
				PinState pinState = i.getArgument(0);
				currentPinStates[5] = pinState.equals(PinState.HIGH);
				return null;
			}).when(d7Pin).setState(ArgumentMatchers.any());
			doAnswer(i -> {
				currentPinStates[5] = i.getArgument(0);
				return null;
			}).when(d7Pin).setState(ArgumentMatchers.anyBoolean());
		}

		public ArrayList<Boolean[]> getPinStates() {
			return pinStates;
		}
	}

}
