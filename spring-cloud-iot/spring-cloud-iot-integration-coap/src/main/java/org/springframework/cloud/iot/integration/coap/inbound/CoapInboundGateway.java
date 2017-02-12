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
import java.util.Arrays;
import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.springframework.cloud.iot.integration.coap.CoapInputMessage;
import org.springframework.cloud.iot.integration.coap.client.CoapMethod;
import org.springframework.cloud.iot.integration.coap.converter.ByteArrayCoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.converter.CoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.converter.StringCoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.server.AbstractCoapResource;
import org.springframework.cloud.iot.integration.coap.server.CaliforniumServerCoapResponse;
import org.springframework.cloud.iot.integration.coap.server.CoapServerFactoryBean;
import org.springframework.cloud.iot.integration.coap.server.ServerCoapResponse;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.gateway.MessagingGatewaySupport;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Inbound gateway using Californium {@link CoapServer}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapInboundGateway extends MessagingGatewaySupport {

	private static final List<CoapMethod> nonReadableBodyCoapMethods = Arrays.asList(CoapMethod.GET);

	private CoapServerFactoryBean factory;
	private String coapResourceName = "spring-integration-coap";
	private String coapResourceTitle = "Spring Integration Inbound Gateway";
	private int coapServerPort = 5683;
	private final List<CoapMessageConverter<?>> defaultMessageConverters = new ArrayList<CoapMessageConverter<?>>();
	private volatile List<CoapMessageConverter<?>> messageConverters = new ArrayList<CoapMessageConverter<?>>();
	private final boolean expectReply;
	private volatile Expression payloadExpression;
	private volatile boolean mergeWithDefaultConverters = false;
	private volatile boolean convertersMerged;
	private volatile Expression statusCodeExpression;
	private volatile Class<?> requestPayloadType = null;
	private List<CoapMethod> allowedMethods = Arrays.asList(CoapMethod.POST);
	private volatile EvaluationContext evaluationContext;
	private CoapServer coapServer;

	/**
	 * Construct a gateway that will wait for the {@link #setReplyTimeout(long)
	 * replyTimeout} for a reply; if the timeout is exceeded a '5.00
	 * INTERNAL_SERVER_ERROR' status code is returned. This can be modified
	 * using the {@link #setStatusCodeExpression(Expression)
	 * statusCodeExpression}.
	 *
	 * @see #setReplyTimeout(long)
	 * @see #setStatusCodeExpression(Expression)
	 */
	public CoapInboundGateway() {
		this(true);
	}

	/**
	 * Construct a gateway. If 'expectReply' is true it will wait for the
	 * {@link #setReplyTimeout(long) replyTimeout} for a reply; if the timeout
	 * is exceeded a '5.00 INTERNAL_SERVER_ERROR' status code is returned. This
	 * can be modified using the {@link #setStatusCodeExpression(Expression)
	 * statusCodeExpression}. If 'false', a 2.01 CREATED status will be
	 * returned; this can also be modified using
	 * {@link #setStatusCodeExpression(Expression) statusCodeExpression}.
	 *
	 * @param expectReply true if a reply is expected from the downstream flow.
	 *
	 * @see #setReplyTimeout(long)
	 * @see #setStatusCodeExpression(Expression)
	 */
	public CoapInboundGateway(boolean expectReply) {
		super(expectReply);
		this.expectReply = expectReply;
		this.defaultMessageConverters.add(new StringCoapMessageConverter());
		this.defaultMessageConverters.add(new ByteArrayCoapMessageConverter());
	}

	@Override
	protected void onInit() throws Exception {
		super.onInit();
		factory = new CoapServerFactoryBean();
		factory.setPort(coapServerPort);
		List<Resource> coapResources = new ArrayList<>();
		coapResources.add(new InboundHandlingResource());
		factory.setCoapResources(coapResources);
		factory.afterPropertiesSet();
		coapServer = factory.getObject();

		if (this.messageConverters.size() == 0 || (this.mergeWithDefaultConverters && !this.convertersMerged)) {
			this.messageConverters.addAll(this.defaultMessageConverters);
		}
		this.validateSupportedMethods();

		if (this.statusCodeExpression != null) {
			this.evaluationContext = createEvaluationContext();
		}
	}

	/**
	 * Gets the listening coap server port. Returns a port coap
	 * server binded to meaning if random port was requested, real
	 * local port is returned. Returns -1 if port is unknown.
	 *
	 * @return the listening coap server port
	 * @see #setCoapServerPort(int)
	 */
	public int getListeningCoapServerPort() {
		if (coapServer == null) {
			return -1;
		}
		List<Endpoint> endpoints = coapServer.getEndpoints();
		for (Endpoint ep : endpoints) {
			return ep.getAddress().getPort();
		}
		return -1;
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
		return (this.expectReply) ? "coap:inbound-gateway" : "coap:inbound-channel-adapter";
	}

	/**
	 * Sets the coap server port. To get random port use value 0.
	 *
	 * @param coapServerPort the new coap server port
	 * @see #getListeningCoapServerPort()
	 */
	public void setCoapServerPort(int coapServerPort) {
		this.coapServerPort = coapServerPort;
	}

	/**
	 * Sets the coap resource name.
	 *
	 * @param coapResourceName the new coap resource name
	 */
	public void setCoapResourceName(String coapResourceName) {
		Assert.hasText(coapResourceName, "'coapResourceName' cannot be empty");
		this.coapResourceName = coapResourceName;
	}

	/**
	 * Flag which determines if the default converters should be available after
	 * custom converters.
	 *
	 * @param mergeWithDefaultConverters true to merge, false to replace.
	 */
	public void setMergeWithDefaultConverters(boolean mergeWithDefaultConverters) {
		this.mergeWithDefaultConverters = mergeWithDefaultConverters;
	}

	/**
	 * Set the message body converters to use. These converters are used to
	 * convert from and to COAP requests and responses.
	 *
	 * @param messageConverters The message converters.
	 */
	public void setMessageConverters(List<CoapMessageConverter<?>> messageConverters) {
		Assert.noNullElements(messageConverters.toArray(), "'messageConverters' must not contain null entries");
		List<CoapMessageConverter<?>> localConverters = new ArrayList<CoapMessageConverter<?>>(messageConverters);
		if (this.mergeWithDefaultConverters) {
			localConverters.addAll(this.defaultMessageConverters);
			this.convertersMerged = true;
		}
		this.messageConverters = localConverters;
	}

	/**
	 * Gets the message converters.
	 *
	 * @return the message converters
	 */
	protected List<CoapMessageConverter<?>> getMessageConverters() {
		return messageConverters;
	}

	/**
	 * Specify the {@link Expression} to resolve a status code for Response to
	 * override the default '2.01 CREATED' or '5.00 INTERNAL_SERVER_ERROR' for a
	 * timeout.
	 * <p>
	 * The {@link #statusCodeExpression} is applied only for the one-way {@code
	 * <http:inbound-channel-adapter/>} or when no reply (timeout) is received
	 * for a gateway. The {@code <http:inbound-gateway/>} (or whenever
	 * {@link #CoapInboundGateway(boolean) expectReply} is true)
	 * resolves a status from the
	 * {@link org.springframework.cloud.iot.integration.coap.CoapHeaders#STATUS_CODE}
	 * reply {@link Message} header.
	 *
	 * @param statusCodeExpression The status code Expression.
	 * @see #setReplyTimeout(long)
	 * @see CoapInboundGateway#CoapInboundGateway(boolean)
	 */
	public void setStatusCodeExpression(Expression statusCodeExpression) {
		this.statusCodeExpression = statusCodeExpression;
	}

	protected StandardEvaluationContext createEvaluationContext() {
		return ExpressionUtils.createStandardEvaluationContext(this.getBeanFactory());
	}

	private void validateSupportedMethods() {
		if (this.requestPayloadType != null
				&& CollectionUtils.containsAny(nonReadableBodyCoapMethods, Arrays.asList(this.allowedMethods))) {
			if (logger.isWarnEnabled()) {
				logger.warn("The 'requestPayloadType' attribute will have no relevance for one " + "of the specified COAP methods '"
						+ nonReadableBodyCoapMethods + "'");
			}
		}
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
				logger.info("reply message " + reply);
			} catch (MessageTimeoutException e) {
				logger.error("reply error", e);
			}
		} else {
			this.send(message);
		}
		return reply;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private Object extractRequestBody(CoapExchange exchange) {
		int contentFormat = exchange.getRequestOptions().getContentFormat();
		Class<?> expectedType = this.requestPayloadType;
		if (expectedType == null) {
			expectedType = (MediaTypeRegistry.isPrintable(contentFormat)) ? String.class : byte[].class;
		}

		for (CoapMessageConverter converter : getMessageConverters()) {
			if (converter.canRead(expectedType, contentFormat)) {

				CoapInputMessage xxx = new CoapInputMessage() {

					@Override
					public byte[] getBody() {
						return exchange.getRequestPayload();
					}
				};

				return converter.read(expectedType, xxx);
			}
		}

		throw new MessagingException("Could not convert request: no suitable CoapMessageConverter found for expected type ["
				+ expectedType.getName() + "] and content format [" + contentFormat + "]");
	}

	private MultiValueMap<String, String> convertOptionSet(OptionSet requestOptions) {
		List<String> uriQuery = requestOptions.getUriQuery();
		MultiValueMap<String, String> convertedMap = new LinkedMultiValueMap<String, String>(uriQuery.size());
		return convertedMap;
	}

	private boolean isReadable(CoapExchange exchange) {
		return exchange.getRequestCode() == Code.POST;
	}

	private class InboundHandlingResource extends AbstractCoapResource {

		private List<CoapMethod> allowedMethods = Arrays.asList(CoapMethod.POST);

		public InboundHandlingResource() {
			super(coapResourceName);
			getAttributes().setTitle(coapResourceTitle);
		}

		@Override
		public void handleGET(CoapExchange exchange) {
			if (isMethodAllowed(exchange)) {
				handleRequest(this, exchange);
			} else {
				super.handleGET(exchange);
			}
		}

		@Override
		public void handlePOST(CoapExchange exchange) {
			if (isMethodAllowed(exchange)) {
				handleRequest(this, exchange);
			} else {
				super.handlePOST(exchange);
			}
		}

		@Override
		public void handlePUT(CoapExchange exchange) {
			if (isMethodAllowed(exchange)) {
				handleRequest(this, exchange);
			} else {
				super.handlePUT(exchange);
			}
		}

		@Override
		public void handleDELETE(CoapExchange exchange) {
			if (isMethodAllowed(exchange)) {
				handleRequest(this, exchange);
			} else {
				super.handleDELETE(exchange);
			}
		}

		private void handleRequest(CoapResource resource, CoapExchange exchange) {
			byte[] responseContent = null;
			Message<?> responseMessage;
			try {
				responseMessage = doHandleRequest(exchange);
				CaliforniumServerCoapResponse response = new CaliforniumServerCoapResponse();
				if (responseMessage != null) {
					convertMessage(responseMessage.getPayload(), response);
				}
				exchange.respond(ResponseCode.CREATED, response.getRequestPayload());
			} catch (Exception e) {
				exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
			}
		}

		private void convertMessage(Object requestBody, ServerCoapResponse response) {
			for (CoapMessageConverter<?> converter : getMessageConverters()) {
				if (converter.canWrite(requestBody.getClass(), null)) {
					((CoapMessageConverter<Object>)converter).write(requestBody, response);
					return;
				}
			}
		}

		private boolean isMethodAllowed(CoapExchange exchange) {
			return CollectionUtils.containsInstance(allowedMethods, CoapMethod.resolve(exchange.getRequestCode().name()));
		}
	}
}
