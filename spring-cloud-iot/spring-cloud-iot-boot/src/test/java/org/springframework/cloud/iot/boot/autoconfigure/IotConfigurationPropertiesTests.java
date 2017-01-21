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
	public void testComponentsProperties() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebEnvironment(false);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=IotConfigurationPropertiesTests1" });
		IotConfigurationProperties properties = context.getBean(IotConfigurationProperties.class);
		assertThat(properties, notNullValue());
		assertThat(properties.getComponents(), notNullValue());

		assertThat(properties.getComponents().get("myButton"), notNullValue());
		assertThat(properties.getComponents().get("myButton").getButton(), notNullValue());
		assertThat(properties.getComponents().get("myButton").getButton().isEnabled(), is(true));
		assertThat(properties.getComponents().get("myButton").getButton().getGpio().getPin(), is(27));

		assertThat(properties.getComponents().get("myRotary"), notNullValue());
		assertThat(properties.getComponents().get("myRotary").getIncrementalRotary(), notNullValue());
		assertThat(properties.getComponents().get("myRotary").getIncrementalRotary().isEnabled(), is(true));
		assertThat(properties.getComponents().get("myRotary").getIncrementalRotary().getIncrementSteps(), is(20));
		assertThat(properties.getComponents().get("myRotary").getIncrementalRotary().getGpio().getLeftPin(), is(17));
		assertThat(properties.getComponents().get("myRotary").getIncrementalRotary().getGpio().getRightPin(), is(18));
		assertThat(properties.getComponents().get("myRotary").getIncrementalRotary().getGpio().getClickPin(), is(27));

		assertThat(properties.getComponents().get("myShiftRegister"), notNullValue());
		assertThat(properties.getComponents().get("myShiftRegister").getShiftRegister(), notNullValue());
		assertThat(properties.getComponents().get("myShiftRegister").getShiftRegister().isEnabled(), is(true));
		assertThat(properties.getComponents().get("myShiftRegister").getShiftRegister().getGpio().getSdiPin(), is(17));
		assertThat(properties.getComponents().get("myShiftRegister").getShiftRegister().getGpio().getRclkPin(), is(18));
		assertThat(properties.getComponents().get("myShiftRegister").getShiftRegister().getGpio().getSrclkPin(), is(27));

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
//		assertThat(properties.getDevice(), notNullValue());
//		assertThat(properties.getDevice().getLcd(), notNullValue());
//		assertThat(properties.getDevice().getLcd().getRows(), is(2));
//		assertThat(properties.getDevice().getLcd().getColums(), is(16));
//		assertThat(properties.getDevice().getLcd().isClearTextOnExit(), is(true));
		context.close();
	}

	@Configuration
	@EnableConfigurationProperties({ IotConfigurationProperties.class })
	protected static class TestConfiguration {
	}
}
