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
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.component.Relay;
import org.springframework.cloud.iot.pi4j.Pi4jDimmedLed;
import org.springframework.cloud.iot.pi4j.Pi4jGpioRelayComponent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
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
		GpioConfigurationProperties gpioProperties = buildGpioProperties();

		for (Entry<Integer, PinType> entry : gpioProperties.getPins().entrySet()) {
			Integer pin = entry.getKey();
			PinType type = entry.getValue();

			if (type.getDimmedLed() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jDimmedLedGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(pin));
				registry.registerBeanDefinition(BEAN_PREFIX + pin, bdb.getBeanDefinition());
			}

			if (type.getRelay() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jGpioRelayComponentGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(pin));
				registry.registerBeanDefinition(BEAN_PREFIX + pin, bdb.getBeanDefinition());
			}
		}
	}

	public static class Pi4jGpioRelayComponentGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private String pinName;
		private Pi4jGpioRelayComponent object;

		public Pi4jGpioRelayComponentGpioFactoryBean(String pinName) {
			this.pinName = pinName;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin pin = RaspiPin.getPinByName("GPIO " + pinName);
			GpioPinDigitalOutput output = gpioController.provisionDigitalOutputPin(pin);
			object = new Pi4jGpioRelayComponent(output);
		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return Relay.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

	public static class Pi4jDimmedLedGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private String pinName;
		private Pi4jDimmedLed object;

		public Pi4jDimmedLedGpioFactoryBean(String pinName) {
			this.pinName = pinName;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin pin = RaspiPin.getPinByName("GPIO " + pinName);
			GpioPinPwmOutput output = gpioController.provisionSoftPwmOutputPin(pin, 0);
			object = new Pi4jDimmedLed(output);
		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return DimmedLed.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

}
