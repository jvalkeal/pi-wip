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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

import com.pi4j.component.temperature.impl.Tmp102;

@RunWith(MockitoJUnitRunner.class)
public class Pi4jPCF8591TemperatureSensorTests {

	@Mock
	private Tmp102 tmp102;

	@Test
	public void testValue() {
		System.out.println(getTemperature());
		System.out.println(getTemperature2(124, 5, 8, 1000, 3950, 25));
//		when(tmp102.getTemperature()).thenReturn(0.0);
//		assertThat(tmp102, notNullValue());
//		Pi4jPCF8591TemperatureSensor sensor = new Pi4jPCF8591TemperatureSensor("foo", tmp102);
//		assertThat(sensor.getTemperature(), is(0.0));
	}

	static public double getTemperature() {
		double analogVal = 124;
		double Vr = 5 * analogVal / 255;
		double Rt = 10000 * Vr / (5 - Vr);
		double t = 1 / (((Math.log(Rt / 10000)) / 3950) + (1 / (273.15 + 25)));
		t = t - 273.15;
		return t;
	}

	static public double getTemperature2(double analog, float vSupply, int bits, int r, int b2550, double refTemp) {
		double vR = vSupply * analog / (Math.pow(2, bits) -1 );
		double rT = r * vR / (vSupply - vR);
		double kelvin = 1 / (((Math.log(rT / r)) / b2550) + (1 / (273.15 + refTemp)));
		return kelvin - 273.15;
	}

}
