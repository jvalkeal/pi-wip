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
package org.springframework.cloud.iot.boot.autoconfigure;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.cloud.iot.boot.autoconfigure.component.ComponentsConfiguration;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.component.IncrementalRotary;
import org.springframework.cloud.iot.component.Led;
import org.springframework.cloud.iot.component.Relay;
import org.springframework.cloud.iot.component.ShiftRegister;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.PinPullResistance;

public class ComponentsConfigurationTests {

	@Test
	public void testShiftRegister() {
		load(TestConfig.class,
				"spring.cloud.iot.components.myShiftRegister.shiftRegister.enabled=true",
				"spring.cloud.iot.components.myShiftRegister.shiftRegister.gpio.sdiPin=17",
				"spring.cloud.iot.components.myShiftRegister.shiftRegister.gpio.rclkPin=18",
				"spring.cloud.iot.components.myShiftRegister.shiftRegister.gpio.srclkPin=27"
				);
		assertThat(context.containsBean("GPIO_myShiftRegister"), is(true));
		assertThat(context.getBeansOfType(ShiftRegister.class).size(), is(1));
	}

	@Test
	public void testIncrementalRotary() {
		load(TestConfig.class,
				"spring.cloud.iot.components.myRotary.incrementalRotary.enabled=true",
				"spring.cloud.iot.components.myRotary.incrementalRotary.incrementSteps=20",
				"spring.cloud.iot.components.myRotary.incrementalRotary.gpio.leftPin=17",
				"spring.cloud.iot.components.myRotary.incrementalRotary.gpio.rightPin=18"
				);
		assertThat(context.containsBean("GPIO_myRotary"), is(true));
		assertThat(context.getBeansOfType(IncrementalRotary.class).size(), is(1));
	}

	@Test
	public void testButton() {
		load(TestConfig.class,
				"spring.cloud.iot.components.myButton.button.enabled=true",
				"spring.cloud.iot.components.myButton.button.gpio.pin=27"
				);
		assertThat(context.containsBean("GPIO_myButton"), is(true));
		assertThat(context.getBeansOfType(Button.class).size(), is(1));
	}

	@Test
	public void testDimmedLeds() {
		load(TestConfig.class,
				"spring.cloud.iot.components.myDimmedLedRed.dimmedLed.gpio.pin=17",
				"spring.cloud.iot.components.myDimmedLedGreen.dimmedLed.gpio.pin=18"
				);
		assertThat(context.containsBean("GPIO_myDimmedLedRed"), is(true));
		assertThat(context.containsBean("GPIO_myDimmedLedGreen"), is(true));
		assertThat(context.getBeansOfType(DimmedLed.class).size(), is(2));
	}

	@Test
	public void testRelay() {
		load(TestConfig.class,
				"spring.cloud.iot.components.myRelay.relay.gpio.pin=19"
				);
		assertThat(context.containsBean("GPIO_myRelay"), is(true));
		assertThat(context.getBeansOfType(Relay.class).size(), is(1));
	}

	@Test
	public void testLed() {
		load(TestConfig.class,
				"spring.cloud.iot.components.myLed1.led.enabled=true",
				"spring.cloud.iot.components.myLed1.led.gpio.pin=17",
				"spring.cloud.iot.components.myLed2.led.enabled=true",
				"spring.cloud.iot.components.myLed2.led.gpio.pin=27"
				);
		assertThat(context.containsBean("GPIO_myLed1"), is(true));
		assertThat(context.containsBean("GPIO_myLed2"), is(true));
		assertThat(context.getBeansOfType(Led.class).size(), is(2));
	}

	@Configuration
	@Import(TestComponentsConfiguration.class)
	public static class TestConfig {

		@Bean
		GpioController gpioController() {
			GpioController gpioController = Mockito.mock(GpioController.class);
			GpioPinPwmOutput gpioPinPwmOutput = Mockito.mock(GpioPinPwmOutput.class);
			GpioPinDigitalInput gpioPinDigitalInput = Mockito.mock(GpioPinDigitalInput.class);
			GpioPinDigitalOutput gpioPinDigitalOutput = Mockito.mock(GpioPinDigitalOutput.class);
			when(gpioController.provisionSoftPwmOutputPin(any(), anyInt())).thenReturn(gpioPinPwmOutput);
			when(gpioController.provisionDigitalOutputPin(any())).thenReturn(gpioPinDigitalOutput);
			when(gpioController.provisionDigitalInputPin(any())).thenReturn(gpioPinDigitalInput);
			when(gpioController.provisionDigitalInputPin(any(), any(PinPullResistance.class))).thenReturn(gpioPinDigitalInput);
			return gpioController;
		}

	}

	public static class TestComponentsConfiguration extends ComponentsConfiguration {
	}

	protected AnnotationConfigApplicationContext context;

	@After
	public void close() {
		if (this.context != null) {
			this.context.close();
		}
	}

	protected void load(Class<?> config, String... environment) {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		EnvironmentTestUtils.addEnvironment(context, environment);
		if (config != null) {
			context.register(config);
		}
		context.register(PropertyPlaceholderAutoConfiguration.class);
		context.refresh();
		this.context = context;
	}
}
