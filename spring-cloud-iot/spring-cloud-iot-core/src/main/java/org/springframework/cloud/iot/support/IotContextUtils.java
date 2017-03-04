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
package org.springframework.cloud.iot.support;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.cloud.iot.IotSystemConstants;
import org.springframework.cloud.iot.event.IotEventPublisher;
import org.springframework.util.Assert;

public class IotContextUtils {

	/**
	 * Return the {@link IotEventPublisher} bean whose name is
	 * "iotEventPublisher" if available.
	 *
	 * @param beanFactory BeanFactory for lookup, must not be null.
	 * @return state machine event publisher
	 */
	public static IotEventPublisher getEventPublisher(BeanFactory beanFactory) {
		return getBeanOfType(beanFactory, IotSystemConstants.DEFAULT_ID_EVENT_PUBLISHER,
				IotEventPublisher.class);
	}

	/**
	 * Gets a bean from a factory with a given name and type.
	 *
	 * @param beanFactory the bean factory
	 * @param beanName the bean name
	 * @param type the type as of a class
	 * @return Bean known to a bean factory, null if not found.
	 */
	private static <T> T getBeanOfType(BeanFactory beanFactory, String beanName, Class<T> type) {
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		if (!beanFactory.containsBean(beanName)) {
			return null;
		}
		return beanFactory.getBean(beanName, type);
	}
}
