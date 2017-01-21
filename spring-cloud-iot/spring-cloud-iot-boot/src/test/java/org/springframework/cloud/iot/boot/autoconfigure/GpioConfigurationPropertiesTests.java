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
import org.springframework.cloud.iot.boot.GpioConfigurationProperties;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

public class GpioConfigurationPropertiesTests {

	@Test
	public void testGpioProperties() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebEnvironment(false);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=GpioConfigurationPropertiesTests1" });
		GpioConfigurationProperties properties = context.getBean(GpioConfigurationProperties.class);
		assertThat(properties, notNullValue());
		assertThat(properties.getPins(), notNullValue());

		assertThat(properties.getPins().get(17), notNullValue());
		assertThat(properties.getPins().get(17).getDimmedLed(), notNullValue());
		assertThat(properties.getPins().get(17).getDimmedLed().isEnabled(), is(false));

		assertThat(properties.getPins().get(18), notNullValue());
		assertThat(properties.getPins().get(18).getDimmedLed(), notNullValue());
		assertThat(properties.getPins().get(18).getDimmedLed().isEnabled(), is(true));

		assertThat(properties.getPins().get(19), notNullValue());
		assertThat(properties.getPins().get(19).getRelay(), notNullValue());
		assertThat(properties.getPins().get(19).getRelay().isEnabled(), is(true));

		assertThat(properties.getPins().get(20), notNullValue());
		assertThat(properties.getPins().get(20).getButton(), notNullValue());
		assertThat(properties.getPins().get(20).getButton().isEnabled(), is(true));

		context.close();
	}

	@Configuration
	@EnableConfigurationProperties({ GpioConfigurationProperties.class })
	protected static class TestConfiguration {
	}

}
