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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.springframework.cloud.iot.coap.CoapHeaders;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.CoapStatus;
import org.springframework.cloud.iot.coap.californium.CaliforniumCoapServerFactory;
import org.springframework.cloud.iot.coap.converter.ByteArrayCoapMessageConverter;
import org.springframework.cloud.iot.coap.converter.CoapMessageConverter;
import org.springframework.cloud.iot.coap.converter.StringCoapMessageConverter;
import org.springframework.cloud.iot.coap.server.CoapServer;
import org.springframework.cloud.iot.coap.server.CoapServerHandler;
import org.springframework.cloud.iot.coap.server.ServerCoapRequest;
import org.springframework.cloud.iot.coap.server.ServerCoapResponse;
import org.springframework.cloud.iot.coap.support.GenericServerCoapResponse;
import org.springframework.cloud.iot.integration.coap.support.DefaultCoapHeaderMapper;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.MessageTimeoutException;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.gateway.MessagingGatewaySupport;
import org.springframework.integration.mapping.HeaderMapper;
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
	private volatile HeaderMapper<CoapHeaders> headerMapper = DefaultCoapHeaderMapper.inboundMapper();


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
		this(null, expectReply);
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
	 * @param coapServer the coap server instance
	 * @param expectReply true if a reply is expected from the downstream flow.
	 */
	public CoapInboundGateway(CoapServer coapServer, boolean expectReply) {
		super(expectReply);
		this.coapServer = coapServer;
		this.expectReply = expectReply;
		this.defaultMessageConverters.add(new StringCoapMessageConverter());
		this.defaultMessageConverters.add(new ByteArrayCoapMessageConverter());
	}

	@Override
	protected void onInit() throws Exception {
		super.onInit();

		Map<String, CoapServerHandler> mappings = new HashMap<>();
		mappings.put(coapResourceName, new InboundHandlingCoapServerHandler());

		// no ref to existing coap server, fall back to create a new
		// using californium
		if (coapServer == null) {
			CaliforniumCoapServerFactory factory = new CaliforniumCoapServerFactory();
			factory.setHandlerMappings(mappings);
			coapServer = factory.getCoapServer();
		} else {
			coapServer.addHandlerMappings(mappings);
		}

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
		return coapServer.getPort();
	}

	/**
	 * Set the {@link HeaderMapper} to use when mapping between COAP headers and MessageHeaders.
	 *
	 * @param headerMapper The header mapper.
	 */
	public void setHeaderMapper(HeaderMapper<CoapHeaders> headerMapper) {
		Assert.notNull(headerMapper, "headerMapper must not be null");
		this.headerMapper = headerMapper;
	}

	@Override
	protected void doStart() {
		coapServer.start();
	}

	@Override
	protected void doStop() {
		coapServer.stop();
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

	private Message<?> doHandleRequest(ServerCoapRequest request) {
		Object requestBody = null;

		if (request.getBody() != null) {

		}

		if (isReadable(request)) {
			requestBody = this.extractRequestBody(request);
		}

		StandardEvaluationContext evaluationContext = this.createEvaluationContext();

		MultiValueMap<String, String> requestParams = new LinkedMultiValueMap<String, String>();

		Map<String, Object> headers = this.headerMapper.toHeaders(request.getHeaders());
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
			messageBuilder = this.getMessageBuilderFactory().fromMessage((Message<?>) payload)
					.copyHeadersIfAbsent(headers);
		} else {
			messageBuilder = this.getMessageBuilderFactory().withPayload(payload).copyHeaders(headers);
		}

		Message<?> message = messageBuilder
				.setHeader("uri-path", request.getUriPath())
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
	private Object extractRequestBody(ServerCoapRequest request) {
		int contentFormat = request.getContentFormat();
		Class<?> expectedType = this.requestPayloadType;
		if (expectedType == null) {
			expectedType = (MediaTypeRegistry.isPrintable(contentFormat)) ? String.class : byte[].class;
		}

		for (CoapMessageConverter converter : getMessageConverters()) {
			if (converter.canRead(expectedType, contentFormat)) {
				return converter.read(expectedType, request);
			}
		}

		throw new MessagingException("Could not convert request: no suitable CoapMessageConverter found for expected type ["
				+ expectedType.getName() + "] and content format [" + contentFormat + "]");
	}

	private boolean isReadable(ServerCoapRequest request) {
		return request.getMethod() == CoapMethod.POST;
	}

	@SuppressWarnings("unchecked")
	private void convertMessage(Object requestBody, ServerCoapResponse response) {
		for (CoapMessageConverter<?> converter : getMessageConverters()) {
			if (converter.canWrite(requestBody.getClass(), null)) {
				((CoapMessageConverter<Object>)converter).write(requestBody, response);
				return;
			}
		}
	}

	private class InboundHandlingCoapServerHandler implements CoapServerHandler {

		@Override
		public ServerCoapResponse handle(ServerCoapRequest request) {
			logger.debug("Received ServerCoapRequest " + request);
			GenericServerCoapResponse response = new GenericServerCoapResponse();
			System.out.println("XXX5 " + request.getHeaders());

			Message<?> responseMessage;
			try {
				responseMessage = doHandleRequest(request);
				logger.debug("Response message from doHandleRequest " + responseMessage);
				if (responseMessage != null) {
					convertMessage(responseMessage.getPayload(), response);
				}
				response.setStatus(CoapStatus.CREATED);
			} catch (Exception e) {
				logger.error("Unable to handle request", e);
				response.setStatus(CoapStatus.INTERNAL_SERVER_ERROR);
			}
			return response;
		}
	}
}
