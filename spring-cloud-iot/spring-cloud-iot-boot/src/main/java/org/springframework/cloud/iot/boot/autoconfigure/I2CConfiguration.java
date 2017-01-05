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

import java.io.IOException;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.IotConfigurationProperties.Addresses;
import org.springframework.cloud.iot.component.TemperatureSensor;
import org.springframework.cloud.iot.pi4j.Pi4jPCF8574Lcd;
import org.springframework.cloud.iot.pi4j.Pi4jPCF8591TemperatureSensor;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;
import com.pi4j.component.temperature.impl.Tmp102;
import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CFactory.UnsupportedBusNumberException;

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
		IotConfigurationProperties properties = buildIotProperties();
		if (properties.getI2C() == null || properties.getI2C().getAddresses() == null) {
			return;
		}
		for (Entry<Integer, Addresses> entry : properties.getI2C().getAddresses().entrySet()) {
			if (entry.getValue().getType().equals("temperature")) {
//				Tmp102 tmp102 = null;
//				try {
//					tmp102 = new Tmp102(I2CBus.BUS_1, entry.getKey());
//				} catch (UnsupportedBusNumberException e) {
//					e.printStackTrace();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				if (tmp102 != null) {
//					BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(I2CFactoryBean.class);
//					bdb.addConstructorArgValue(new Pi4jPCF8591TemperatureSensor(null, tmp102));
//					bdb.addConstructorArgValue(TemperatureSensor.class);
//					registry.registerBeanDefinition(BEAN_PREFIX + entry.getKey(), bdb.getBeanDefinition());
//				}

				Termistor termistor = null;
				try {
					termistor = new Termistor(I2CBus.BUS_1, entry.getKey());
				} catch (UnsupportedBusNumberException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (termistor != null) {
					BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(I2CFactoryBean.class);
					bdb.addConstructorArgValue(new Pi4jPCF8591TemperatureSensor(null, termistor));
					bdb.addConstructorArgValue(TemperatureSensor.class);
					registry.registerBeanDefinition(BEAN_PREFIX + entry.getKey(), bdb.getBeanDefinition());
				}

//				Termistor termistor = new Termistor(I2CBus.BUS_1, entry.getKey());
			} else if (entry.getValue().getType().equals("lcd")) {
				I2CLcdDisplay lcd = null;
				try {
					lcd = new I2CLcdDisplay(properties.getDevice().getLcd().getRows(), properties.getDevice().getLcd().getColums(),
							entry.getValue().getBus(), entry.getKey(), 3, 0, 1, 2, 7, 6, 5, 4);
				} catch (Exception e) {
					log.error("Error creating I2CLcdDisplay", e);
					e.printStackTrace();
				}
				if (lcd != null) {
					BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jPCF8574Lcd.class);
					bdb.addConstructorArgValue(lcd);
					registry.registerBeanDefinition(BEAN_PREFIX + entry.getKey(), bdb.getBeanDefinition());
				}
			}
		}
	}

	public static class I2CFactoryBean implements FactoryBean<Object>, InitializingBean {

		private Object device;
		private Class<?> clazz;

		public I2CFactoryBean(Object device, Class<?> clazz) {
			this.device = device;
			this.clazz = clazz;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			if (device instanceof InitializingBean) {
				((InitializingBean)device).afterPropertiesSet();
			}
		}

		@Override
		public Object getObject() throws Exception {
			return device;
		}

		@Override
		public Class<?> getObjectType() {
			return clazz;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

}
