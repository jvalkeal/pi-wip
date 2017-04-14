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
package org.springframework.cloud.iot.stream.binder.coap;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.cloud.iot.integration.coap.outbound.CoapOutboundGateway;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageHandler;

/**
 * Binder implementation for XBee.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapMessageChannelBinder extends
		AbstractMessageChannelBinder<ConsumerProperties, ProducerProperties, ProvisioningProvider<ConsumerProperties, ProducerProperties>>
		implements BeanFactoryAware {

	private BeanFactory beanFactory;
	private CoapOutboundGateway gateway = null;

	/**
	 * Instantiates a new xbee message channel binder.
	 *
	 * @param provisioningProvider the provisioning provider
	 */
	public CoapMessageChannelBinder(ProvisioningProvider<ConsumerProperties, ProducerProperties> provisioningProvider) {
		super(false, new String[0], provisioningProvider);
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ProducerProperties producerProperties) throws Exception {
		return getOrBuildGateway();
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
			ConsumerProperties properties) throws Exception {
		return getOrBuildGateway();
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	private CoapOutboundGateway getOrBuildGateway() {
		if (gateway == null) {
			gateway = new CoapOutboundGateway("coap://localhost:5683/spring-integration-coap");
			gateway.setBeanFactory(beanFactory);
			gateway.setRequiresReply(true);
			gateway.setExpectedResponseType(String.class);
		}
		return gateway;
	}
}
