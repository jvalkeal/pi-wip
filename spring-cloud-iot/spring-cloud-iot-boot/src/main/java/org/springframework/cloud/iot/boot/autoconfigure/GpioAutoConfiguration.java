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

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.boot.condition.ConditionalOnIot;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties.NumberingScheme;
import org.springframework.cloud.iot.boot.properties.RaspberryConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.RaspiGpioProvider;
import com.pi4j.io.gpio.RaspiPinNumberingScheme;

/**
 * Auto-configuration for GPIO system.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnIot
//@EnableConfigurationProperties(RaspberryConfigurationProperties.class)
//@EnableGpio
public class GpioAutoConfiguration {

	public static String BEAN_NAME_GPIOCONTROLLER = "gpioController";

//	public GpioAutoConfiguration(RaspberryConfigurationProperties properties) {
//	}

//	@Bean(destroyMethod = "shutdown")
//	public GpioController gpioController(RaspberryConfigurationProperties raspberryProperties) {
//		if (raspberryProperties.getNumberingScheme() == NumberingScheme.BROADCOM) {
//			GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
//		}
//		GpioController gpio = GpioFactory.getInstance();
//		return gpio;
//	}
	@Bean(destroyMethod = "shutdown")
	public GpioController gpioController() {
		GpioFactory.setDefaultProvider(new RaspiGpioProvider(RaspiPinNumberingScheme.BROADCOM_PIN_NUMBERING));
		GpioController gpio = GpioFactory.getInstance();
		return gpio;
	}
}
