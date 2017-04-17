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
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapBinderConfigurationProperties;
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapBinderConfigurationProperties.Mode;
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapConsumerProperties;
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapExtendedBindingProperties;
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapProducerProperties;
import org.springframework.cloud.stream.binder.AbstractMessageChannelBinder;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.binder.ExtendedPropertiesBinder;
import org.springframework.cloud.stream.provisioning.ConsumerDestination;
import org.springframework.cloud.stream.provisioning.ProducerDestination;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

/**
 * Binder implementation for CoAP.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapMessageChannelBinder extends
		AbstractMessageChannelBinder<ExtendedConsumerProperties<CoapConsumerProperties>, ExtendedProducerProperties<CoapProducerProperties>, ProvisioningProvider<ExtendedConsumerProperties<CoapConsumerProperties>, ExtendedProducerProperties<CoapProducerProperties>>>
		implements BeanFactoryAware, ExtendedPropertiesBinder<MessageChannel, CoapConsumerProperties, CoapProducerProperties> {

	private BeanFactory beanFactory;
	private CoapOutboundGateway gateway = null;
	private CoapExtendedBindingProperties extendedBindingProperties = new CoapExtendedBindingProperties();
	private CoapBinderConfigurationProperties configurationProperties;

	/**
	 * Instantiates a new coap message channel binder.
	 *
	 * @param provisioningProvider the provisioning provider
	 */
	public CoapMessageChannelBinder(ProvisioningProvider<ExtendedConsumerProperties<CoapConsumerProperties>, ExtendedProducerProperties<CoapProducerProperties>> provisioningProvider) {
		super(false, new String[0], provisioningProvider);
	}

	@Override
	protected MessageHandler createProducerMessageHandler(ProducerDestination destination,
			ExtendedProducerProperties<CoapProducerProperties> producerProperties) throws Exception {
		logger.info("Creating producer messagehandler for coap");
		return getOrBuildGateway();
	}

	@Override
	protected MessageProducer createConsumerEndpoint(ConsumerDestination destination, String group,
			ExtendedConsumerProperties<CoapConsumerProperties> consumerProperties) throws Exception {
		logger.info("Creating consumer endpoint for coap");
		return getOrBuildGateway();
	}

	@Override
	public CoapProducerProperties getExtendedProducerProperties(String channelName) {
		return this.extendedBindingProperties.getExtendedProducerProperties(channelName);
	}

	@Override
	public CoapConsumerProperties getExtendedConsumerProperties(String channelName) {
		return this.extendedBindingProperties.getExtendedConsumerProperties(channelName);
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	public void setBinderProperties(CoapBinderConfigurationProperties configurationProperties) {
		this.configurationProperties = configurationProperties;
	}

	public void setExtendedBindingProperties(CoapExtendedBindingProperties coapExtendedBindingProperties) {
		this.extendedBindingProperties = coapExtendedBindingProperties;
	}

	private CoapOutboundGateway getOrBuildGateway() {
		if (configurationProperties.getMode() == Mode.OUTBOUND_GATEWAY) {
			if (gateway == null) {
				gateway = new CoapOutboundGateway("coap://localhost:5683/spring-integration-coap");
				gateway.setBeanFactory(beanFactory);
				gateway.setRequiresReply(true);
				gateway.setOutputChannelName("iotGatewayClientReply");
				gateway.setExpectedResponseType(byte[].class);
			}
		}
		return gateway;
	}
}
