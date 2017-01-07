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

import org.junit.After;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.test.util.EnvironmentTestUtils;
import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

public class I2CConfigurationTests {

	@Test
	public void testTermistor() {
		load(TestConfig.class,
				"spring.cloud.iot.i2c.addresses.0x48.bus=1",
				"spring.cloud.iot.i2c.addresses.0x48.termistor.resistance=10000",
				"spring.cloud.iot.i2c.addresses.0x48.termistor.supplyVoltage=5.0",
				"spring.cloud.iot.i2c.addresses.0x48.termistor.referenceTemp=25.0",
				"spring.cloud.iot.i2c.addresses.0x48.termistor.dacBits=8",
				"spring.cloud.iot.i2c.addresses.0x48.termistor.beta=3950"
				);
		assertThat(context.containsBean("I2C_72"), is(true));
		assertThat(context.getBeansOfType(TemperatureSensor.class).size(), is(1));
	}

	@Configuration
	@Import(TestI2CConfiguration.class)
	public static class TestConfig {

	}

	public static class TestI2CConfiguration extends I2CConfiguration {

		@Override
		protected Termistor getTermistor(int i2cBus, int i2cAddr, double voltageSupply, int dacBits, int resistance, double beta,
				double referenceTemp) {
			return Mockito.mock(Termistor.class);
		}
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
