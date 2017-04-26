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
import org.springframework.cloud.iot.integration.xbee.inbound.XBeeInboundGateway;
import org.springframework.cloud.iot.integration.xbee.outbound.XBeeOutboundGateway;
import org.springframework.cloud.iot.stream.binder.xbee.properties.XBeeBinderConfigurationProperties;
import org.springframework.cloud.iot.stream.binder.xbee.properties.XBeeBinderConfigurationProperties.Mode;
import org.springframework.cloud.iot.stream.binder.xbee.properties.XBeeConsumerProperties;
import org.springframework.cloud.iot.stream.binder.xbee.properties.XBeeExtendedBindingProperties;
import org.springframework.cloud.iot.stream.binder.xbee.properties.XBeeProducerProperties;
import org.springframework.cloud.iot.xbee.XBeeReceiver;
import org.springframework.cloud.iot.xbee.XBeeSender;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.handler.BridgeHandler;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.util.Assert;

/**
 * Binder implementation for XBee.
 *
 * @author Janne Valkealahti
 *
 */
public class XBeeMessageChannelBinder extends
		AbstractMessageChannelBinder<ExtendedConsumerProperties<XBeeConsumerProperties>, ExtendedProducerProperties<XBeeProducerProperties>, ProvisioningProvider<ExtendedConsumerProperties<XBeeConsumerProperties>, ExtendedProducerProperties<XBeeProducerProperties>>>
		implements BeanFactoryAware, ExtendedPropertiesBinder<MessageChannel, XBeeConsumerProperties, XBeeProducerProperties> {

	private final XBeeSender xbeeSender;
	private final XBeeReceiver xbeeReceiver;
	private BeanFactory beanFactory;
	private MessageHandler messageHandler = null;
	private MessageProducer messageProducer = null;
	private XBeeExtendedBindingProperties extendedBindingProperties = new XBeeExtendedBindingProperties();
	private XBeeBinderConfigurationProperties configurationProperties;

	/**
	 * Instantiates a new XBee message channel binder.
	 *
	 * @param provisioningProvider the provisioning provider
	 * @param xbeeSender the xbee sender
	 * @param xbeeReceiver the xbee receiver
	 */
	public XBeeMessageChannelBinder(
			ProvisioningProvider<ExtendedConsumerProperties<XBeeConsumerProperties>, ExtendedProducerProperties<XBeeProducerProperties>> provisioningProvider,
			XBeeSender xbeeSender, XBeeReceiver xbeeReceiver) {
		super(false, new String[0], provisioningProvider);
		Assert.notNull(xbeeSender, "'xbeeSender' must be set");
		Assert.notNull(xbeeReceiver, "'xbeeReceiver' must be set");
		this.xbeeSender = xbeeSender;
		this.xbeeReceiver = xbeeReceiver;

	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ExtendedProducerProperties<XBeeProducerProperties> producerProperties) throws Exception {
		logger.info("Creating producer messagehandler for XBee");
		getOrBuildGateway();
		return messageHandler;
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
			ExtendedConsumerProperties<XBeeConsumerProperties> consumerProperties) throws Exception {
		logger.info("Creating consumer endpoint for XBee");
		getOrBuildGateway();
		return messageProducer;
	}

	@Override
	public XBeeProducerProperties getExtendedProducerProperties(String channelName) {
		return this.extendedBindingProperties.getExtendedProducerProperties(channelName);
	}

	@Override
	public XBeeConsumerProperties getExtendedConsumerProperties(String channelName) {
		return this.extendedBindingProperties.getExtendedConsumerProperties(channelName);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void setBinderProperties(XBeeBinderConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	public void setExtendedBindingProperties(XBeeExtendedBindingProperties XBeeExtendedBindingProperties) {
		this.extendedBindingProperties = XBeeExtendedBindingProperties;
	}

	private void getOrBuildGateway() {
		if (configurationProperties.getMode() == Mode.OUTBOUND_GATEWAY) {
			XBeeOutboundGateway gateway = new XBeeOutboundGateway(xbeeSender, xbeeReceiver);
			gateway.setBeanFactory(beanFactory);
			gateway.setOutputChannelName("iotGatewayClientReply");
			messageHandler = gateway;
			messageProducer = gateway;
		} else if (configurationProperties.getMode() == Mode.INBOUND_GATEWAY) {
			XBeeInboundGateway xbeeInboundGateway = new XBeeInboundGateway(xbeeReceiver, xbeeSender);
			xbeeInboundGateway.setBeanFactory(beanFactory);
			xbeeInboundGateway.afterPropertiesSet();
			xbeeInboundGateway.start();
			BridgeHandler delegate = new BridgeHandler();
			delegate.setBeanFactory(beanFactory);
			xbeeInboundGateway.setRequestChannelName("iotGatewayServer");
			xbeeInboundGateway.setReplyChannelName("iotGatewayServerReply");
			this.messageHandler = delegate;
			this.messageProducer = delegate;
		}
	}
}
