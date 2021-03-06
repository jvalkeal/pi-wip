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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.cloud.iot.component.IotProperties;
import org.springframework.cloud.iot.component.IotTags;
import org.springframework.cloud.iot.event.IotEventPublisher;

/**
 * Support class for iot components being aware of higher level context
 * components like {@code IotEventPublisher} and {@code BeanFactory}.
 *
 * @author Janne Valkealahti
 *
 */
public class IotObjectSupport extends LifecycleObjectSupport implements BeanFactoryAware, IotTags, IotProperties {

	private static final Logger log = LoggerFactory.getLogger(IotObjectSupport.class);

	private volatile BeanFactory beanFactory;
	private volatile IotEventPublisher iotEventPublisher;
	private final Collection<String> tags = new ArrayList<>();
	private final Map<String, Object> properties = new HashMap<>();

	/** Flag for application context events */
	private boolean contextEventsEnabled = true;

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void setTags(Collection<String> tags) {
		this.tags.clear();
		if (tags != null) {
			this.tags.addAll(tags);
		}
	}

	@Override
	public Collection<String> getTags() {
		return tags;
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		this.properties.clear();
		if (properties != null) {
			this.properties.putAll(properties);
		}
	}

	@Override
	public Map<String, Object> getProperties() {
		return properties;
	}

	protected BeanFactory getBeanFactory() {
		return beanFactory;
	}

	public void setIotEventPublisher(IotEventPublisher iotEventPublisher) {
		this.iotEventPublisher = iotEventPublisher;
	}

	public boolean isContextEventsEnabled() {
		return contextEventsEnabled;
	}

	protected IotEventPublisher getIotEventPublisher() {
		if(log.isTraceEnabled()) {
			log.trace("getIotEventPublisher {} {}", iotEventPublisher, getBeanFactory());
		}
		if(iotEventPublisher == null && getBeanFactory() != null) {
			if(log.isTraceEnabled()) {
				log.trace("getting iotEventPublisher service from bean factory " + getBeanFactory());
			}
			iotEventPublisher = IotContextUtils.getEventPublisher(getBeanFactory());
			if(log.isTraceEnabled()) {
				log.trace("iotEventPublisher from context is {}", iotEventPublisher);
			}
		}
		return iotEventPublisher;
	}
}
