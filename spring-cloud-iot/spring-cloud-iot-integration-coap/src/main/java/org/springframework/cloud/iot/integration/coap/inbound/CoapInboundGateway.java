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
package org.springframework.cloud.iot.integration.coap.inbound;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.cloud.iot.integration.coap.converter.CoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.converter.StringCoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.server.AbstractCoapResource;
import org.springframework.cloud.iot.integration.coap.server.CoapServerFactoryBean;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.gateway.MessagingGatewaySupport;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Inbound gateway using Californium {@link CoapServer}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapInboundGateway extends MessagingGatewaySupport {

	private CoapServerFactoryBean factory;
	private String coapResourceName = "spring-integration-coap";
	private final List<CoapMessageConverter<?>> messageConverters = new ArrayList<CoapMessageConverter<?>>();
	private final boolean expectReply;
	private volatile Expression payloadExpression;

	public CoapInboundGateway() {
		this.messageConverters.add(new StringCoapMessageConverter());
		this.expectReply = true;
	}

	@Override
	protected void onInit() throws Exception {
		super.onInit();
		factory = new CoapServerFactoryBean();
		List<Resource> coapResources = new ArrayList<>();
		coapResources.add(new InboundHandlingResource());
		factory.setCoapResources(coapResources);
		factory.afterPropertiesSet();
		factory.getObject();
	}

	@Override
	protected void doStart() {
	}

	@Override
	protected void doStop() {
		if (factory != null) {
			try {
				factory.destroy();
			} catch (Exception e) {
				logger.info("Error stopping server", e);
			}
		}
	}

	@Override
	public String getComponentType() {
		return "coap:inbound-gateway";
	}

	public List<CoapMessageConverter<?>> getMessageConverters() {
		return messageConverters;
	}

	public void setCoapResourceName(String coapResourceName) {
		Assert.hasText(coapResourceName, "'coapResourceName' cannot be empty");
		this.coapResourceName = coapResourceName;
	}

	private Message<?> doOnMessage(Message<?> message) {
		Message<?> reply = this.sendAndReceiveMessage(message);
		return reply;
	}

	private void xxx() {
		for (CoapMessageConverter converter : this.getMessageConverters()) {
//			if (converter.canRead(clazz, contentFormat)) {
//			}
		}
	}

	private boolean isReadable(CoapExchange exchange) {
		return exchange.getRequestCode() == Code.POST;
	}

	private Message<?> doHandleRequest(CoapExchange exchange) {
		Object requestBody = null;

		if (isReadable(exchange)) {
			requestBody = this.extractRequestBody(exchange);
		}

		StandardEvaluationContext evaluationContext = this.createEvaluationContext();

		OptionSet requestOptions = exchange.getRequestOptions();
		MultiValueMap<String, String> requestParams = this.convertOptionSet(requestOptions);

		Object payload = null;

		if (this.payloadExpression != null) {
			payload = this.payloadExpression.getValue(evaluationContext);
		}

		if (payload == null) {
			if (requestBody != null) {
				payload = requestBody;
			} else {
				payload = requestParams;
			}
		}


		AbstractIntegrationMessageBuilder<?> messageBuilder = null;

		if (payload instanceof Message<?>) {
			messageBuilder = this.getMessageBuilderFactory().fromMessage((Message<?>) payload);
		} else {
			messageBuilder = this.getMessageBuilderFactory().withPayload(payload);
		}

		Message<?> message = messageBuilder
				.setHeader("uri-path", exchange.getRequestOptions().getUriPathString())
				.build();

		Message<?> reply = null;
		if (this.expectReply) {
			try {
				reply = this.sendAndReceiveMessage(message);
			} catch (MessageTimeoutException e) {

			}
		} else {
			this.send(message);
		}
		return reply;
	}

	protected StandardEvaluationContext createEvaluationContext() {
		return ExpressionUtils.createStandardEvaluationContext(this.getBeanFactory());
	}

	private Object extractRequestBody(CoapExchange exchange) {
		int contentFormat = exchange.getRequestOptions().getContentFormat();
		return exchange.getRequestText();
	}

	private MultiValueMap<String, String> convertOptionSet(OptionSet requestOptions) {
		List<String> uriQuery = requestOptions.getUriQuery();
		MultiValueMap<String, String> convertedMap = new LinkedMultiValueMap<String, String>(uriQuery.size());

//		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
//			String[] values = entry.getValue();
//
//			for (String value : values) {
//				convertedMap.add(entry.getKey(), value);
//			}
//		}
		return convertedMap;
	}

	private class InboundHandlingResource extends AbstractCoapResource {

		public InboundHandlingResource() {
			super(coapResourceName);
			getAttributes().setTitle("Spring Integration Inbound Gateway");
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			Message<?> reply = doHandleRequest(exchange);
//			Message<?> reply = doOnMessage(getMessageBuilderFactory().withPayload("hello").build());
			exchange.respond(ResponseCode.CREATED, reply.getPayload().toString());
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			Message<?> reply = doHandleRequest(exchange);
//			Message<?> reply = doOnMessage(getMessageBuilderFactory().withPayload("hello").build());
			exchange.respond(ResponseCode.CREATED, reply.getPayload().toString());
		}

		@Override
		public void handlePUT(CoapExchange exchange) {
			Message<?> reply = doOnMessage(getMessageBuilderFactory().withPayload("hello").build());
			exchange.respond(ResponseCode.CREATED, reply.getPayload().toString());
		}

		@Override
		public void handleDELETE(CoapExchange exchange) {
			Message<?> reply = doOnMessage(getMessageBuilderFactory().withPayload("hello").build());
			exchange.respond(ResponseCode.CREATED, reply.getPayload().toString());
		}
	}
}
