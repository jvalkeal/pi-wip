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

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.iot.pi4j.Pi4jTemperatureSensor;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import com.pi4j.component.temperature.TemperatureSensor;
import com.pi4j.io.w1.W1Master;

/**
 * Central W1 {@link Configuration} registering devices as beans.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
public class W1Configuration implements ImportBeanDefinitionRegistrar, EnvironmentAware {

	private final static String BEAN_PREFIX = "W1_";

	@Override
	public void setEnvironment(Environment environment) {
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		W1Master w1Master = new W1Master();
		for (TemperatureSensor device : w1Master.getDevices(TemperatureSensor.class)) {
			BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jTemperatureSensor.class);
			bdb.addConstructorArgValue(device.getName().trim());
			bdb.addConstructorArgValue(device);
			registry.registerBeanDefinition(BEAN_PREFIX + device.getName().trim(), bdb.getBeanDefinition());
		}
	}
}
