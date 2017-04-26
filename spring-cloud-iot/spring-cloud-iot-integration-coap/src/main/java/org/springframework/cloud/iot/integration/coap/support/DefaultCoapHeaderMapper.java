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

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cloud.iot.coap.CoapHeaders;
import org.springframework.core.convert.ConversionService;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.integration.support.utils.IntegrationUtils;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

public class DefaultCoapHeaderMapper implements HeaderMapper<CoapHeaders>, BeanFactoryAware, InitializingBean {

	private static final Log logger = LogFactory.getLog(DefaultCoapHeaderMapper.class);
	private volatile BeanFactory beanFactory;
	private volatile ConversionService conversionService;
	private volatile String[] outboundHeaderNames = new String[0];
	private volatile String[] inboundHeaderNames = new String[0];

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
		if (logger.isDebugEnabled()) {
			logger.debug(MessageFormat.format("outboundHeaderNames={0}",
					CollectionUtils.arrayToList(this.outboundHeaderNames)));
		}

		for (Entry<String, Object> entry : headers.entrySet()) {
			String name = entry.getKey();
			String lowerName = name.toLowerCase();
		}

		String gatewayService = headers.get("iotGatewayServiceRoute", String.class);
		if (StringUtils.hasText(gatewayService)) {
			coapHeaders.add(9999, gatewayService.getBytes());
		}
	}

	@Override
	public Map<String, Object> toHeaders(CoapHeaders coapHeaders) {
		Map<String, Object> target = new HashMap<String, Object>();
		for (Entry<Integer, List<byte[]>> entry : coapHeaders.entrySet()) {
			if (ObjectUtils.nullSafeEquals(entry.getKey(), 9999)) {
				List<byte[]> values = entry.getValue();
				if (values != null && values.size() == 1) {
					target.put("iotGatewayServiceRoute", new String(values.get(0)));
				}
			} else {
				target.put(entry.getKey().toString(), entry.getValue());
			}
		}
		return target;
	}

	public static DefaultCoapHeaderMapper outboundMapper() {
		DefaultCoapHeaderMapper mapper = new DefaultCoapHeaderMapper();
		return mapper;
	}

	public static DefaultCoapHeaderMapper inboundMapper() {
		DefaultCoapHeaderMapper mapper = new DefaultCoapHeaderMapper();
		return mapper;
	}
}
