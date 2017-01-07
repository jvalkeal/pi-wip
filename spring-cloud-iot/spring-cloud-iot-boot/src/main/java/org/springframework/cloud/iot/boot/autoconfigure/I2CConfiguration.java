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

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.GpioConfigurationProperties.PinComponentType;
import org.springframework.cloud.iot.boot.I2cConfigurationProperties;
import org.springframework.cloud.iot.boot.I2cConfigurationProperties.AddressType;
import org.springframework.cloud.iot.boot.I2cConfigurationProperties.LcdType;
import org.springframework.cloud.iot.boot.I2cConfigurationProperties.TermistorType;
import org.springframework.cloud.iot.boot.IotConfigurationProperties.Addresses;
import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.cloud.iot.pi4j.Pi4jPCF8574Lcd;
import org.springframework.cloud.iot.pi4j.Pi4jPCF8591TemperatureSensor;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;
import com.pi4j.io.i2c.I2CBus;

/**
 * Central I2C {@link Configuration} registering devices as beans.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
public class I2CConfiguration extends AbstractConfigurationSupport implements ImportBeanDefinitionRegistrar {

	private final Logger log = LoggerFactory.getLogger(I2CConfiguration.class);
	private final static String BEAN_PREFIX = "I2C_";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		I2cConfigurationProperties i2cProperties = buildI2cProperties();
		for (Entry<Integer, AddressType> entry : i2cProperties.getAddresses().entrySet()) {
			Integer address = entry.getKey();
			AddressType type = entry.getValue();

			if (type.getTermistor() != null) {
				TermistorType t = type.getTermistor();
				Termistor termistor = getTermistor(type.getBus(), address, t.getSupplyVoltage(), t.getDacBits(), t.getResistance(),
						t.getBeta(), t.getReferenceTemp());
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jPCF8591TemperatureSensor.class);
				bdb.addConstructorArgValue(null);
				bdb.addConstructorArgValue(termistor);
				registry.registerBeanDefinition(BEAN_PREFIX + address, bdb.getBeanDefinition());
			}

			if (type.getLcd() != null) {
				LcdType t = type.getLcd();
				I2CLcdDisplay lcd = getI2CLcdDisplay(type.getBus(), address, t.getRows(), t.getColums());
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jPCF8574Lcd.class);
				bdb.addConstructorArgValue(lcd);
				registry.registerBeanDefinition(BEAN_PREFIX + address, bdb.getBeanDefinition());
			}
		}
	}

//	public static class I2CFactoryBean implements FactoryBean<Object>, InitializingBean {
//
//		private Object device;
//		private Class<?> clazz;
//
//		public I2CFactoryBean(Object device, Class<?> clazz) {
//			this.device = device;
//			this.clazz = clazz;
//		}
//
//		@Override
//		public void afterPropertiesSet() throws Exception {
//			if (device instanceof InitializingBean) {
//				((InitializingBean)device).afterPropertiesSet();
//			}
//		}
//
//		@Override
//		public Object getObject() throws Exception {
//			return device;
//		}
//
//		@Override
//		public Class<?> getObjectType() {
//			return clazz;
//		}
//
//		@Override
//		public boolean isSingleton() {
//			return true;
//		}
//	}

}
