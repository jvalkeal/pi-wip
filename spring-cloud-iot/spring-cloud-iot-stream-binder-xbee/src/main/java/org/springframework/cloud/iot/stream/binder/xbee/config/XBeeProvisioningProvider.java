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
package org.springframework.cloud.iot.stream.binder.xbee.config;

import org.springframework.cloud.iot.stream.binder.xbee.properties.XBeeBinderConfigurationProperties;
import org.springframework.cloud.iot.stream.binder.xbee.properties.XBeeConsumerProperties;
import org.springframework.cloud.iot.stream.binder.xbee.properties.XBeeProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningException;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;

/**
 * {@link ProvisioningProvider} for coap binder.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeProvisioningProvider implements
		ProvisioningProvider<ExtendedConsumerProperties<XBeeConsumerProperties>, ExtendedProducerProperties<XBeeProducerProperties>> {

	private final XBeeBinderConfigurationProperties configurationProperties;

	public XBeeProvisioningProvider(XBeeBinderConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	@Override
	public ProducerDestination provisionProducerDestination(String name,
			ExtendedProducerProperties<XBeeProducerProperties> properties) throws ProvisioningException {
		return new XBeeProducerDestination();
	}

	@Override
	public ConsumerDestination provisionConsumerDestination(String name, String group,
			ExtendedConsumerProperties<XBeeConsumerProperties> properties) throws ProvisioningException {
		return new XBeeConsumerDestination();
	}

	private final static class XBeeProducerDestination implements ProducerDestination {

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getNameForPartition(int partition) {
			return null;
		}
	}

	private final static class XBeeConsumerDestination implements ConsumerDestination {

		@Override
		public String getName() {
			return null;
		}
	}
}
