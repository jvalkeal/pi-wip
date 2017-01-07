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
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.component.Relay;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;

public class GpioConfigurationTests {

	@Test
	public void testDimmedLeds() {
		load(TestConfig.class,
				"spring.cloud.iot.gpio.pins.17.dimmedLed.enabled=true",
				"spring.cloud.iot.gpio.pins.18.dimmedLed.enabled=true"
				);
		assertThat(context.containsBean("GPIO_17"), is(true));
		assertThat(context.containsBean("GPIO_18"), is(true));
		assertThat(context.getBeansOfType(DimmedLed.class).size(), is(2));
	}

	@Test
	public void testRelay() {
		load(TestConfig.class,
				"spring.cloud.iot.gpio.pins.19.relay.enabled=true"
				);
		assertThat(context.containsBean("GPIO_19"), is(true));
		assertThat(context.getBeansOfType(Relay.class).size(), is(1));
	}

	@Configuration
	@Import(TestI2GpioConfiguration.class)
	public static class TestConfig {

		@Bean
		GpioController gpioController() {
			GpioController gpioController = Mockito.mock(GpioController.class);
			GpioPinPwmOutput gpioPinPwmOutput = Mockito.mock(GpioPinPwmOutput.class);
			GpioPinDigitalOutput gpioPinDigitalOutput = Mockito.mock(GpioPinDigitalOutput.class);
			when(gpioController.provisionSoftPwmOutputPin(any(), anyInt())).thenReturn(gpioPinPwmOutput);
			when(gpioController.provisionDigitalOutputPin(any())).thenReturn(gpioPinDigitalOutput);
			return gpioController;
		}

	}

	public static class TestI2GpioConfiguration extends GpioConfiguration {
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
