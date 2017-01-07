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

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.boot.I2cConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

public class I2cConfigurationPropertiesTests {

	@Test
	public void testI2CProperties() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebEnvironment(false);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=I2cConfigurationPropertiesTests1" });
		I2cConfigurationProperties properties = context.getBean(I2cConfigurationProperties.class);
		assertThat(properties, notNullValue());
		assertThat(properties.getAddresses(), notNullValue());
		assertThat(properties.getAddresses().get(0x48), notNullValue());
		assertThat(properties.getAddresses().get(0x48).getBus(), is(1));
		assertThat(properties.getAddresses().get(0x48).getTermistor(), notNullValue());
		assertThat(properties.getAddresses().get(0x48).getTermistor().getSupplyVoltage(), is(5.0));
		assertThat(properties.getAddresses().get(0x48).getTermistor().getResistance(), is(10000));
		assertThat(properties.getAddresses().get(0x48).getTermistor().getReferenceTemp(), is(25.0));
		assertThat(properties.getAddresses().get(0x48).getTermistor().getDacBits(), is(8));
		assertThat(properties.getAddresses().get(0x48).getTermistor().getBeta(), is(3950.0));

		assertThat(properties.getAddresses().get(0x27), notNullValue());
		assertThat(properties.getAddresses().get(0x27).getBus(), is(1));
		assertThat(properties.getAddresses().get(0x27).getLcd(), notNullValue());
		assertThat(properties.getAddresses().get(0x27).getLcd().getRows(), is(2));
		assertThat(properties.getAddresses().get(0x27).getLcd().getColums(), is(16));
		assertThat(properties.getAddresses().get(0x27).getLcd().isClearTextOnExit(), is(true));

		context.close();
	}

	@Configuration
	@EnableConfigurationProperties({ I2cConfigurationProperties.class })
	protected static class TestConfiguration {
	}
}
