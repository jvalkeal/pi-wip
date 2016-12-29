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

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultBeanNameGenerator;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.cloud.iot.boot.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.IotConfigurationProperties.Pins;
import org.springframework.cloud.iot.component.ColorLed;
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.pi4j.Pi4jDimmedLed;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.validation.BindException;

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
public class GpioConfiguration implements ImportBeanDefinitionRegistrar, BeanFactoryAware, EnvironmentAware {

	private final static String BEAN_PREFIX = "GPIO_";
	private BeanFactory beanFactory;
	private ConfigurableEnvironment environment;
	private final BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		Assert.isInstanceOf(ListableBeanFactory.class, beanFactory,
				"beanFactory must be of type ListableBeanFactory but was " + beanFactory);
		this.beanFactory = beanFactory;
	}

	@Override
	public void setEnvironment(Environment environment) {
		Assert.isInstanceOf(ConfigurableEnvironment.class, environment,
				"environment must be of type ConfigurableEnvironment but was " + environment);
		this.environment = (ConfigurableEnvironment)environment;
	}

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		IotConfigurationProperties iotConfigurationProperties = new IotConfigurationProperties();
		PropertiesConfigurationFactory<Object> factory = new PropertiesConfigurationFactory<Object>(iotConfigurationProperties);
		factory.setTargetName("spring.cloud.iot");
		factory.setPropertySources(environment.getPropertySources());

		try {
			factory.bindPropertiesToTarget();
		} catch (BindException e) {
			e.printStackTrace();
		}

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
