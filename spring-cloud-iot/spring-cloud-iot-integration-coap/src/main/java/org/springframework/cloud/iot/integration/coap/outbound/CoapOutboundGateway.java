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
package org.springframework.cloud.iot.integration.coap.outbound;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.cloud.iot.coap.CoapEntity;
import org.springframework.cloud.iot.coap.CoapHeaders;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.CoapResponseEntity;
import org.springframework.cloud.iot.coap.californium.CoapTemplate;
import org.springframework.cloud.iot.coap.client.CoapOperations;
import org.springframework.cloud.iot.integration.coap.support.DefaultCoapHeaderMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.expression.Expression;
import org.springframework.expression.common.LiteralExpression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.expression.ExpressionEvalMap;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.expression.FunctionExpression;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.integration.mapping.HeaderMapper;
import org.springframework.integration.support.AbstractIntegrationMessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.MessagingException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Outbound gateway using {@link CoapOperations}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapOutboundGateway extends AbstractReplyProducingMessageHandler {

	private final CoapOperations coapOperations;
	private volatile boolean extractPayload = true;
	private volatile boolean expectReply = true;
	private volatile boolean extractPayloadExplicitlySet = false;
	private volatile StandardEvaluationContext evaluationContext;
	private volatile Expression expectedResponseTypeExpression;
	private volatile Expression coapMethodExpression = new ValueExpression<>(CoapMethod.POST);
	private final Map<String, Expression> uriVariableExpressions = new HashMap<>();
	private final Expression uriExpression;
	private volatile Expression uriVariablesExpression;
	private volatile boolean encodeUri = true;
	private volatile HeaderMapper<CoapHeaders> headerMapper = DefaultCoapHeaderMapper.outboundMapper();

	public CoapOutboundGateway(URI uri) {
		this(new ValueExpression<URI>(uri), null);
	}

	public CoapOutboundGateway(String uri) {
		this(new LiteralExpression(uri), null);
	}

	public CoapOutboundGateway(Function<Message<?>, ?> uriFunction) {
		this(new FunctionExpression<>(uriFunction), null);
	}

	public CoapOutboundGateway(Expression uriExpression) {
		this(uriExpression, null);
	}

	public CoapOutboundGateway(Expression uriExpression, CoapOperations coapOperations) {
		Assert.notNull(uriExpression, "URI Expression is required");
		this.uriExpression = uriExpression;
		this.coapOperations = coapOperations != null ? coapOperations : new CoapTemplate();
	}

	@Override
	protected void doInit() {
		this.evaluationContext = ExpressionUtils.createStandardEvaluationContext(this.getBeanFactory());
	}

	@Override
	protected Object handleRequestMessage(Message<?> requestMessage) {
		try {
			CoapMethod coapMethod = determineCoapMethod(requestMessage);
			Object expectedResponseType = determineExpectedResponseType(requestMessage);
			CoapEntity<?> coapRequest = this.generateCoapRequest(requestMessage, coapMethod);
			URI uri = generateUri(requestMessage);
			CoapResponseEntity<?> coapResponse = coapOperations.exchange(uri, coapMethod, coapRequest, (Class<?>) expectedResponseType);
			return getReply(coapResponse);
		} catch (MessagingException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageHandlingException(requestMessage, "COAP request execution failed", e);
		}
	}

	@Override
	public String getComponentType() {
		return (this.expectReply) ? "coap:outbound-gateway" : "coap:outbound-channel-adapter";
	}

	/**
	 * Specify whether the outbound message's payload should be extracted
	 * when preparing the request body. Otherwise the Message instance itself
	 * is serialized. The default value is {@code true}.
	 *
	 * @param extractPayload true if the payload should be extracted.
	 */
	public void setExtractPayload(boolean extractPayload) {
		this.extractPayload = extractPayload;
		this.extractPayloadExplicitlySet = true;
	}

	/**
	 * Gets if a reply Message is expected.
	 *
	 * @return whether a reply Message is expected.
	 */
	public boolean getExpectReply() {
		return this.expectReply;
	}

	/**
	 * Specify whether a reply Message is expected. If not, this handler will
	 * simply return null for a successful response or throw an Exception for a
	 * non-successful response. The default is true.
	 *
	 * @param expectReply true if a reply is expected.
	 */
	public void setExpectReply(boolean expectReply) {
		this.expectReply = expectReply;
	}

	public void setExpectedResponseType(Class<?> expectedResponseType) {
		Assert.notNull(expectedResponseType, "'expectedResponseType' must not be null");
		this.expectedResponseTypeExpression = new ValueExpression<Class<?>>(expectedResponseType);
	}

	public void setExpectedResponseTypeExpression(Expression expectedResponseTypeExpression) {
		this.expectedResponseTypeExpression = expectedResponseTypeExpression;
	}

	/**
	 * Set the Map of URI variable expressions to evaluate against the outbound message
	 * when replacing the variable placeholders in a URI template.
	 *
	 * @param uriVariableExpressions The URI variable expressions.
	 */
	public void setUriVariableExpressions(Map<String, Expression> uriVariableExpressions) {
		synchronized (this.uriVariableExpressions) {
			this.uriVariableExpressions.clear();
			this.uriVariableExpressions.putAll(uriVariableExpressions);
		}
	}

	/**
	 * Set the {@link Expression} to evaluate against the outbound message; the expression
	 * must evaluate to a Map of URI variable expressions to evaluate against the outbound message
	 * when replacing the variable placeholders in a URI template.
	 *
	 * @param uriVariablesExpression The URI variables expression.
	 */
	public void setUriVariablesExpression(Expression uriVariablesExpression) {
		this.uriVariablesExpression = uriVariablesExpression;
	}

	/**
	 * Specify whether the real URI should be encoded after <code>uriVariables</code>
	 * expanding and before send request via {@link CoapTemplate}. The default value is <code>true</code>.
	 *
	 * @param encodeUri true if the URI should be encoded.
	 *
	 * @see UriComponentsBuilder
	 */
	public void setEncodeUri(boolean encodeUri) {
		this.encodeUri = encodeUri;
	}

	/**
	 * Set the {@link HeaderMapper} to use when mapping between COAP headers and
	 * MessageHeaders.
	 *
	 * @param headerMapper The header mapper.
	 */
	public void setHeaderMapper(HeaderMapper<CoapHeaders> headerMapper) {
		Assert.notNull(headerMapper, "headerMapper must not be null");
		this.headerMapper = headerMapper;
	}

	private URI generateUri(Message<?> requestMessage) {
		Object uri = this.uriExpression.getValue(this.evaluationContext, requestMessage);
		Assert.state(uri instanceof String || uri instanceof URI,
				"'uriExpression' evaluation must result in a 'String' or 'URI' instance, not: "
						+ (uri == null ? "null" : uri.getClass()));
		Map<String, ?> uriVariables = determineUriVariables(requestMessage);
		UriComponentsBuilder uriComponentsBuilder = uri instanceof String
				? UriComponentsBuilder.fromUriString((String) uri)
				: UriComponentsBuilder.fromUri((URI) uri);
		UriComponents uriComponents = uriComponentsBuilder.buildAndExpand(uriVariables);
		try {
			return this.encodeUri ? uriComponents.toUri() : new URI(uriComponents.toUriString());
		}
		catch (URISyntaxException e) {
			throw new MessageHandlingException(requestMessage, "Invalid URI [" + uri + "]", e);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, ?> determineUriVariables(Message<?> requestMessage) {
		Map<String, ?> expressions;

		if (this.uriVariablesExpression != null) {
			Object expressionsObject = this.uriVariablesExpression.getValue(this.evaluationContext, requestMessage);
			Assert.state(expressionsObject instanceof Map,
					"The 'uriVariablesExpression' evaluation must result in a 'Map'.");
			expressions = (Map<String, ?>) expressionsObject;
		}
		else {
			expressions = this.uriVariableExpressions;
		}

		return ExpressionEvalMap.from(expressions)
				.usingEvaluationContext(this.evaluationContext)
				.withRoot(requestMessage)
				.build();

	}

	private CoapMethod determineCoapMethod(Message<?> requestMessage) {
		Object coapMethod = this.coapMethodExpression.getValue(this.evaluationContext, requestMessage);
		Assert.state(coapMethod != null && (coapMethod instanceof String || coapMethod instanceof CoapMethod),
				"'coapMethodExpression' evaluation must result in an 'CoapMethod' enum or its String representation, " + "not: "
						+ (coapMethod == null ? "null" : coapMethod.getClass()));
		if (coapMethod instanceof CoapMethod) {
			return (CoapMethod) coapMethod;
		} else {
			try {
				return CoapMethod.valueOf((String) coapMethod);
			} catch (Exception e) {
				throw new IllegalStateException("The 'coapMethodExpression' returned an invalid COAP Method value: " + coapMethod);
			}
		}
	}

	private CoapEntity<?> generateCoapRequest(Message<?> message, CoapMethod httpMethod) throws Exception {
		Assert.notNull(message, "message must not be null");
		return (this.extractPayload) ? this.createCoapEntityFromPayload(message, httpMethod)
				: this.createCoapEntityFromMessage(message, httpMethod);
	}

	private Object determineExpectedResponseType(Message<?> requestMessage) throws Exception {
		Object expectedResponseType = null;
		if (this.expectedResponseTypeExpression != null) {
			expectedResponseType = this.expectedResponseTypeExpression.getValue(this.evaluationContext, requestMessage);
		}
		if (expectedResponseType != null) {
			Assert.state(expectedResponseType instanceof Class<?>
							|| expectedResponseType instanceof String
							|| expectedResponseType instanceof ParameterizedTypeReference,
					"'expectedResponseType' can be an instance of 'Class<?>', 'String' or 'ParameterizedTypeReference<?>'; "
					+ "evaluation resulted in a" + expectedResponseType.getClass() + ".");
			if (expectedResponseType instanceof String && StringUtils.hasText((String) expectedResponseType)) {
				expectedResponseType = ClassUtils.forName((String) expectedResponseType,
						getApplicationContext().getClassLoader());
			}
		}
		return expectedResponseType;
	}

	private CoapEntity<?> createCoapEntityFromPayload(Message<?> message, CoapMethod coapMethod) {
		Object payload = message.getPayload();
		if (payload instanceof CoapEntity<?>) {
			// payload is already an CoapEntity, just return it as-is
			return (CoapEntity<?>) payload;
		}

		CoapHeaders coapHeaders = this.mapHeaders(message);

		if (!shouldIncludeRequestBody(coapMethod)) {
			return new CoapEntity<Object>();
		}
		return new CoapEntity<Object>(payload, coapHeaders);
	}

	protected CoapHeaders mapHeaders(Message<?> message) {
		CoapHeaders coapHeaders = new CoapHeaders();
		this.headerMapper.fromHeaders(message.getHeaders(), coapHeaders);
		return coapHeaders;
	}

	private CoapEntity<?> createCoapEntityFromMessage(Message<?> message, CoapMethod coapMethod) {
		if (shouldIncludeRequestBody(coapMethod)) {
			return new CoapEntity<Object>(message);
		}
		return new CoapEntity<Object>();
	}

	private boolean shouldIncludeRequestBody(CoapMethod coapMethod) {
		return !CoapMethod.GET.equals(coapMethod);
	}

	/**
	 * Builds a reply from a coap response.
	 *
	 * @param coapResponse the coap response
	 * @return the reply object
	 */
	private Object getReply(CoapResponseEntity<?> coapResponse) {
		if (this.expectReply) {
			AbstractIntegrationMessageBuilder<?> replyBuilder = null;
			if (coapResponse.getBody() != null) {
				Object responseBody = coapResponse.getBody();
				replyBuilder = (responseBody instanceof Message<?>) ?
						this.getMessageBuilderFactory().fromMessage((Message<?>) responseBody) : this.getMessageBuilderFactory().withPayload(responseBody);
			} else {
				replyBuilder = this.getMessageBuilderFactory().withPayload(coapResponse);
			}
			return replyBuilder.build();
		}
		return null;
	}

}
