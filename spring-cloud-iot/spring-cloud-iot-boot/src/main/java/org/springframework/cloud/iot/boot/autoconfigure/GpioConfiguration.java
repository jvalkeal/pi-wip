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

import java.util.Map.Entry;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.iot.boot.GpioConfigurationProperties;
import org.springframework.cloud.iot.boot.GpioConfigurationProperties.PinType;
import org.springframework.cloud.iot.boot.IotConfigurationProperties.NumberingScheme;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.RaspberryConfigurationProperties;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.component.Relay;
import org.springframework.cloud.iot.pi4j.Pi4jButton;
import org.springframework.cloud.iot.pi4j.Pi4jDimmedLed;
import org.springframework.cloud.iot.pi4j.Pi4jGpioRelayComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.pi4j.component.button.impl.GpioButtonComponent;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Central GPIO {@link Configuration} registering devices as beans.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
public class GpioConfiguration extends AbstractConfigurationSupport implements ImportBeanDefinitionRegistrar {

	private final static String BEAN_PREFIX = "GPIO_";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		RaspberryConfigurationProperties raspberryProperties = buildRaspberryProperties();
		GpioConfigurationProperties gpioProperties = buildGpioProperties();

	}
}
