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
package org.springframework.cloud.iot.boot.autoconfigure.component;

import java.util.Collection;

import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties.NumberingScheme;
import org.springframework.cloud.iot.component.Taggable;
import org.springframework.context.SmartLifecycle;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiPin;

/**
 * Base factory creating gpio components.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the type of component
 */
public abstract class AbstractComponentGpioFactoryBean<T extends SmartLifecycle> extends AbstractFactoryBean<T>
		implements SmartLifecycle {

	private final GpioController gpioController;
	private final NumberingScheme numberingScheme;
	private final Class<T> clazz;
	private T lifecycle;
	private Collection<String> tags;

	public AbstractComponentGpioFactoryBean(GpioController gpioController, NumberingScheme numberingScheme,
			Class<T> clazz) {
		this(gpioController, numberingScheme, null, clazz);
	}

	public AbstractComponentGpioFactoryBean(GpioController gpioController, NumberingScheme numberingScheme, Collection<String> tags,
			Class<T> clazz) {
		this.gpioController = gpioController;
		this.numberingScheme = numberingScheme;
		this.tags = tags;
		this.clazz = clazz;
	}

	@Override
	public Class<T> getObjectType() {
		return clazz;
	}

	@Override
	final protected T createInstance() throws Exception {
		lifecycle = createInstanceInternal();
		if (lifecycle instanceof BeanFactoryAware) {
			((BeanFactoryAware)lifecycle).setBeanFactory(getBeanFactory());
		}
		if (lifecycle instanceof InitializingBean) {
			((InitializingBean)lifecycle).afterPropertiesSet();
		}
		if (lifecycle instanceof Taggable) {
			((Taggable)lifecycle).setTags(tags);
		}
		return lifecycle;
	}

	@Override
	public void start() {
		lifecycle.start();
	}

	@Override
	public void stop() {
		lifecycle.stop();
	}

	@Override
	public boolean isRunning() {
		return lifecycle.isRunning();
	}

	@Override
	public int getPhase() {
		return 0;
	}

	@Override
	public boolean isAutoStartup() {
		return lifecycle.isAutoStartup();
	}

	@Override
	public void stop(Runnable callback) {
		lifecycle.stop(callback);
	}

	@Override
	protected void destroyInstance(T instance) throws Exception {
		if (instance instanceof DisposableBean) {
			((DisposableBean)instance).destroy();
		}
	}

	/**
	 * Creates the instance internal. Actual instance is created here
	 * as {@link #createInstance()} is marked final to store instance
	 * in this class order to handle its {@link SmartLifecycle}.
	 *
	 * @return the instance
	 * @throws Exception the exception
	 */
	abstract protected T createInstanceInternal() throws Exception;

	/**
	 * Resolve {@link Pin} based on pin name.
	 *
	 * @param pinName the pin name
	 * @return the pin
	 */
	protected Pin resolvePin(String pinName) {
		return numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + pinName)
				: RaspiPin.getPinByName("GPIO " + pinName);
	}

	/**
	 * Gets the gpio controller.
	 *
	 * @return the gpio controller
	 */
	protected GpioController getGpioController() {
		return gpioController;
	}
}
