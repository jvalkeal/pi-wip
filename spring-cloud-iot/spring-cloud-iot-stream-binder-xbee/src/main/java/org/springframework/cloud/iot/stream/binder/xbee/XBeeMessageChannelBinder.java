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
package org.springframework.cloud.iot.stream.binder.xbee;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.cloud.iot.integration.xbee.inbound.XBeeInboundChannelAdapter;
import org.springframework.cloud.iot.integration.xbee.outbound.XBeeOutboundGateway;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.Assert;

import com.digi.xbee.api.XBeeDevice;

/**
 * Binder implementation for XBee.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeMessageChannelBinder extends
		AbstractMessageChannelBinder<ConsumerProperties, ProducerProperties, ProvisioningProvider<ConsumerProperties, ProducerProperties>>
		implements BeanFactoryAware {

	private final XBeeDevice xbeeDevice;
	private BeanFactory beanFactory;

	/**
	 * Instantiates a new xbee message channel binder.
	 *
	 * @param provisioningProvider the provisioning provider
	 * @param xbeeDevice the xbee device
	 */
	public XBeeMessageChannelBinder(ProvisioningProvider<ConsumerProperties, ProducerProperties> provisioningProvider, XBeeDevice xbeeDevice) {
		super(false, new String[0], provisioningProvider);
		Assert.notNull(xbeeDevice, "'xbeeDevice' must be set");
		this.xbeeDevice = xbeeDevice;
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ProducerProperties producerProperties) throws Exception {
		XBeeOutboundGateway gateway = new XBeeOutboundGateway(xbeeDevice);
		gateway.setBeanFactory(beanFactory);
		return gateway;
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
			ConsumerProperties properties) throws Exception {
		XBeeInboundChannelAdapter adapter = new XBeeInboundChannelAdapter(xbeeDevice);
		adapter.setBeanFactory(beanFactory);
		return adapter;
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

}
