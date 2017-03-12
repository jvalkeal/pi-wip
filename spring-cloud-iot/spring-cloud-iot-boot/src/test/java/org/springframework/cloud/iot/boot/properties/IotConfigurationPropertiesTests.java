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
package org.springframework.cloud.iot.boot.properties;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties;
import org.springframework.cloud.iot.properties.ReferenceMode;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

public class IotConfigurationPropertiesTests {

	@Test
	public void testComponentsProperties() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=IotConfigurationPropertiesTests1" });
		IotConfigurationProperties properties = context.getBean(IotConfigurationProperties.class);
		assertThat(properties, notNullValue());
		assertThat(properties.getComponents(), notNullValue());

		assertThat(properties.getComponents().get("myLed"), notNullValue());
		assertThat(properties.getComponents().get("myLed").getLed(), notNullValue());
		assertThat(properties.getComponents().get("myLed").getLed().isEnabled(), is(true));
		assertThat(properties.getComponents().get("myLed").getLed().getIlluminateOnStart(), is(true));
		assertThat(properties.getComponents().get("myLed").getLed().getIlluminateOnExit(), is(false));
		assertThat(properties.getComponents().get("myLed").getLed().getGpio().getPin(), is(17));

		assertThat(properties.getComponents().get("myButton"), notNullValue());
		assertThat(properties.getComponents().get("myButton").getButton(), notNullValue());
		assertThat(properties.getComponents().get("myButton").getButton().isEnabled(), is(true));
		assertThat(properties.getComponents().get("myButton").getButton().getTags(), containsInAnyOrder("foo", "bar"));
		assertThat(properties.getComponents().get("myButton").getButton().getGpio().getPin(), is(27));
		assertThat(properties.getComponents().get("myButton").getButton().getGpio().getReference(), is(ReferenceMode.GND));

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

		assertThat(properties.getComponents().get("myPotentiometer"), notNullValue());
		assertThat(properties.getComponents().get("myPotentiometer").getPotentiometer(), notNullValue());
		assertThat(properties.getComponents().get("myPotentiometer").getPotentiometer().isEnabled(), is(true));
		assertThat(properties.getComponents().get("myPotentiometer").getPotentiometer().getMin(), is(0));
		assertThat(properties.getComponents().get("myPotentiometer").getPotentiometer().getMax(), is(255));
		assertThat(properties.getComponents().get("myPotentiometer").getPotentiometer().getI2c(), notNullValue());
		assertThat(properties.getComponents().get("myPotentiometer").getPotentiometer().getI2c().getBus(), is(1));
		assertThat(properties.getComponents().get("myPotentiometer").getPotentiometer().getI2c().getAddress(), is(72));

		assertThat(properties.getComponents().get("myLcd"), notNullValue());
		assertThat(properties.getComponents().get("myLcd").getLcd(), notNullValue());
		assertThat(properties.getComponents().get("myLcd").getLcd().isEnabled(), is(true));
		assertThat(properties.getComponents().get("myLcd").getLcd().getRows(), is(2));
		assertThat(properties.getComponents().get("myLcd").getLcd().getColumns(), is(20));
		assertThat(properties.getComponents().get("myLcd").getLcd().getI2c(), notNullValue());
		assertThat(properties.getComponents().get("myLcd").getLcd().getI2c().getBus(), is(1));
		assertThat(properties.getComponents().get("myLcd").getLcd().getI2c().getAddress(), is(72));
		assertThat(properties.getComponents().get("myLcd").getLcd().getHd44780(), notNullValue());
		assertThat(properties.getComponents().get("myLcd").getLcd().getHd44780().getRsPin(), is(17));
		assertThat(properties.getComponents().get("myLcd").getLcd().getHd44780().getEPin(), is(27));
		assertThat(properties.getComponents().get("myLcd").getLcd().getHd44780().getD4Pin(), is(25));
		assertThat(properties.getComponents().get("myLcd").getLcd().getHd44780().getD5Pin(), is(24));
		assertThat(properties.getComponents().get("myLcd").getLcd().getHd44780().getD6Pin(), is(23));
		assertThat(properties.getComponents().get("myLcd").getLcd().getHd44780().getD7Pin(), is(18));
		assertThat(properties.getComponents().get("myLcd").getLcd().getPcf8574(), notNullValue());
		assertThat(properties.getComponents().get("myLcd").getLcd().getPcf8574().getBus(), is(1));
		assertThat(properties.getComponents().get("myLcd").getLcd().getPcf8574().getAddress(), is(72));

		assertThat(properties.getComponents().get("myTermistor"), notNullValue());
		assertThat(properties.getComponents().get("myTermistor").getTermistor(), notNullValue());
		assertThat(properties.getComponents().get("myTermistor").getTermistor().isEnabled(), is(true));
		assertThat(properties.getComponents().get("myTermistor").getTermistor().getResistance(), is(10000));
		assertThat(properties.getComponents().get("myTermistor").getTermistor().getSupplyVoltage(), is(5.0));
		assertThat(properties.getComponents().get("myTermistor").getTermistor().getReferenceTemp(), is(25.0));
		assertThat(properties.getComponents().get("myTermistor").getTermistor().getDacBits(), is(8));
		assertThat(properties.getComponents().get("myTermistor").getTermistor().getBeta(), is(3950.0));
		assertThat(properties.getComponents().get("myTermistor").getTermistor().getI2c().getBus(), is(1));
		assertThat(properties.getComponents().get("myTermistor").getTermistor().getI2c().getAddress(), is(72));

		context.close();
	}

	@Test
	public void testDeviceProperties() {
		SpringApplication app = new SpringApplication(TestConfiguration.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		ConfigurableApplicationContext context = app
				.run(new String[] { "--spring.config.name=IotConfigurationPropertiesTests2" });
		IotConfigurationProperties properties = context.getBean(IotConfigurationProperties.class);
		assertThat(properties, notNullValue());

		context.close();
	}

	@Configuration
	@EnableConfigurationProperties({ IotConfigurationProperties.class })
	protected static class TestConfiguration {
	}
}
