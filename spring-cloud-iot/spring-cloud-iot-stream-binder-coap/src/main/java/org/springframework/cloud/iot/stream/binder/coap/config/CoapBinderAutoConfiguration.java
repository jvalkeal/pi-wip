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
package org.springframework.cloud.iot.stream.binder.coap.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.iot.stream.binder.coap.CoapMessageChannelBinder;
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapBinderConfigurationProperties;
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapConsumerProperties;
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapExtendedBindingProperties;
import org.springframework.cloud.iot.stream.binder.coap.properties.CoapProducerProperties;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.ExtendedConsumerProperties;
import org.springframework.cloud.stream.binder.ExtendedProducerProperties;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Auto-config for CoAP binder.
 *
 * @author Janne Valkealahti
 *
 */
@Configuration
@ConditionalOnMissingBean(Binder.class)
@EnableConfigurationProperties({CoapBinderConfigurationProperties.class, CoapExtendedBindingProperties.class})
public class CoapBinderAutoConfiguration {

	@Autowired
	private CoapBinderConfigurationProperties configurationProperties;

	@Autowired
	private CoapExtendedBindingProperties coapExtendedBindingProperties;

	@Bean
	public CoapProvisioningProvider coapProvisioningProvider() {
		return new CoapProvisioningProvider(configurationProperties);
	}

	@Bean
	public CoapMessageChannelBinder coapMessageChannelBinder(
			ProvisioningProvider<ExtendedConsumerProperties<CoapConsumerProperties>, ExtendedProducerProperties<CoapProducerProperties>> provisioningProvider) {
		CoapMessageChannelBinder coapMessageChannelBinder = new CoapMessageChannelBinder(provisioningProvider);
		coapMessageChannelBinder.setExtendedBindingProperties(coapExtendedBindingProperties);
		coapMessageChannelBinder.setBinderProperties(configurationProperties);
		return coapMessageChannelBinder;
	}
}
