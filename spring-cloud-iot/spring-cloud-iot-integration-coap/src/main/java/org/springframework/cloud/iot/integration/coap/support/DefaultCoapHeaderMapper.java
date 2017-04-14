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
package org.springframework.cloud.iot.integration.coap.support;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.iot.coap.CoapHeaders;
import org.springframework.core.convert.ConversionService;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.integration.support.utils.IntegrationUtils;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.StringUtils;

public class DefaultCoapHeaderMapper implements HeaderMapper<CoapHeaders>, BeanFactoryAware, InitializingBean {

	private volatile BeanFactory beanFactory;
	private volatile ConversionService conversionService;

	@Override
	public void afterPropertiesSet() throws Exception {
		if (this.beanFactory != null) {
			this.conversionService = IntegrationUtils.getConversionService(this.beanFactory);
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	@Override
	public void fromHeaders(MessageHeaders headers, CoapHeaders coapHeaders) {
		String gatewayService = headers.get("gatewayService", String.class);
		if (StringUtils.hasText(gatewayService)) {
			coapHeaders.add(9999, gatewayService.getBytes());
		}
	}

	@Override
	public Map<String, Object> toHeaders(CoapHeaders source) {
		return null;
	}

	public static DefaultCoapHeaderMapper outboundMapper() {
		DefaultCoapHeaderMapper mapper = new DefaultCoapHeaderMapper();
		return mapper;
	}
}
