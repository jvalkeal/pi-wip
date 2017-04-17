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

import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.iot.stream.binder.xbee.XBeeMessageChannelBinder;
import org.springframework.cloud.iot.xbee.XBeeReceiver;
import org.springframework.cloud.iot.xbee.XBeeSender;
import org.springframework.cloud.stream.binder.Binder;
import org.springframework.cloud.stream.binder.ConsumerProperties;
import org.springframework.cloud.stream.binder.ProducerProperties;
import org.springframework.cloud.stream.provisioning.ProvisioningProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(Binder.class)
@ConditionalOnBean(XBeeSender.class)
public class XBeeBinderAutoConfiguration {

	@Bean
	public XBeeProvisioningProvider provisioningProvider() {
		return new XBeeProvisioningProvider();
	}

	@Bean
	public XBeeMessageChannelBinder xbeeMessageChannelBinder(
			ProvisioningProvider<ConsumerProperties, ProducerProperties> provisioningProvider, XBeeSender xbeeSender,
			XBeeReceiver xbeeReceiver) {
		return new XBeeMessageChannelBinder(provisioningProvider, xbeeSender, xbeeReceiver);
	}
}
