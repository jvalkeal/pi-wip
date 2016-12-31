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
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.IotConfigurationProperties.Pins;
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.pi4j.Pi4jDimmedLed;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.pi4j.io.gpio.GpioController;
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
		IotConfigurationProperties iotConfigurationProperties = buildProperties();
		if (iotConfigurationProperties.getPins() != null) {
			for (Entry<String, Pins> entry : iotConfigurationProperties.getPins().entrySet()) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(GpioFactoryBean.class);
				bdb.addConstructorArgValue(entry.getKey());
				registry.registerBeanDefinition(BEAN_PREFIX + entry.getKey(), bdb.getBeanDefinition());
			}
		}

	}

	public static class GpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private String pinName;
		private Pi4jDimmedLed object;

		public GpioFactoryBean(String pinName) {
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
