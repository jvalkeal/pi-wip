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
import java.util.Set;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.IotConfigurationProperties.ComponentType;
import org.springframework.cloud.iot.boot.IotConfigurationProperties.NumberingScheme;
import org.springframework.cloud.iot.boot.RaspberryConfigurationProperties;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.component.IncrementalRotary;
import org.springframework.cloud.iot.pi4j.Pi4jButton;
import org.springframework.cloud.iot.pi4j.Pi4jIncrementalRotary;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiPin;

@Configuration
public class ComponentsConfiguration extends AbstractConfigurationSupport implements ImportBeanDefinitionRegistrar {

	private final static String BEAN_PREFIX = "GPIO_";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		IotConfigurationProperties iotProperties = buildIotProperties();
		RaspberryConfigurationProperties raspberryProperties = buildRaspberryProperties();

		for (Entry<String, ComponentType> e : iotProperties.getComponents().entrySet()) {
			String name = e.getKey();
			ComponentType type = e.getValue();

			if (type.getIncrementalRotary() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jGpioIncrementalRotaryComponentGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(type.getIncrementalRotary().getIncrementSteps()));
				bdb.addConstructorArgValue(String.valueOf(type.getIncrementalRotary().getGpio().getLeftPin()));
				bdb.addConstructorArgValue(String.valueOf(type.getIncrementalRotary().getGpio().getRightPin()));
				bdb.addConstructorArgValue(raspberryProperties.getNumberingScheme());
				registry.registerBeanDefinition(BEAN_PREFIX + name, bdb.getBeanDefinition());
			} else if (type.getButton() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jGpioButtonComponentGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(type.getButton().getGpio().getPin()));
				bdb.addConstructorArgValue(raspberryProperties.getNumberingScheme());
				registry.registerBeanDefinition(BEAN_PREFIX + name, bdb.getBeanDefinition());
			}
		}

	}

	public static class Pi4jGpioIncrementalRotaryComponentGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private Integer incrementSteps;
		private String leftPinName;
		private String rightPinName;
		private NumberingScheme numberingScheme;
		private Pi4jIncrementalRotary object;

		public Pi4jGpioIncrementalRotaryComponentGpioFactoryBean(Integer incrementSteps, String leftPinName, String rightPinName,
				NumberingScheme numberingScheme) {
			this.incrementSteps = incrementSteps;
			this.leftPinName = leftPinName;
			this.rightPinName = rightPinName;
			this.numberingScheme = numberingScheme;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin leftPin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + leftPinName)
					: RaspiPin.getPinByName("GPIO " + leftPinName);
			Pin rightPin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + rightPinName)
					: RaspiPin.getPinByName("GPIO " + rightPinName);
			GpioPinDigitalInput left = gpioController.provisionDigitalInputPin(leftPin);
			GpioPinDigitalInput right = gpioController.provisionDigitalInputPin(rightPin);
			object = new Pi4jIncrementalRotary(incrementSteps, left, right);
			if (object instanceof InitializingBean) {
				((InitializingBean)object).afterPropertiesSet();
			}

		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return IncrementalRotary.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

	public static class Pi4jGpioButtonComponentGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private String pinName;
		private NumberingScheme numberingScheme;
		private Pi4jButton object;

		public Pi4jGpioButtonComponentGpioFactoryBean(String pinName, NumberingScheme numberingScheme) {
			this.pinName = pinName;
			this.numberingScheme = numberingScheme;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin pin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + pinName)
					: RaspiPin.getPinByName("GPIO " + pinName);
			GpioPinDigitalInput input = gpioController.provisionDigitalInputPin(pin, PinPullResistance.PULL_UP);
			object = new Pi4jButton(input);
			if (object instanceof InitializingBean) {
				((InitializingBean)object).afterPropertiesSet();
			}

		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return Button.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

}
