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
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

public class IotConfigurationPropertiesTests {

	@Test
	public void testI2CProperties() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebEnvironment(false);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=IotConfigurationPropertiesTests1" });
		IotConfigurationProperties properties = context.getBean(IotConfigurationProperties.class);
		assertThat(properties, notNullValue());
		assertThat(properties.getI2C(), notNullValue());
		assertThat(properties.getI2C().getAddresses(), notNullValue());
		assertThat(properties.getI2C().getAddresses().get(0x48), notNullValue());
		assertThat(properties.getI2C().getAddresses().get(0x48).getType(), is("temperature"));
		context.close();
	}

	@Test
	public void testDeviceProperties() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebEnvironment(false);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=IotConfigurationPropertiesTests2" });
		IotConfigurationProperties properties = context.getBean(IotConfigurationProperties.class);
		assertThat(properties, notNullValue());
		assertThat(properties.getDevice(), notNullValue());
		assertThat(properties.getDevice().getLcd(), notNullValue());
		assertThat(properties.getDevice().getLcd().getRows(), is(2));
		assertThat(properties.getDevice().getLcd().getColums(), is(16));
		assertThat(properties.getDevice().getLcd().isClearTextOnExit(), is(true));
		context.close();
	}

	@Configuration
	@EnableConfigurationProperties({ IotConfigurationProperties.class })
	protected static class TestConfiguration {
	}
}
