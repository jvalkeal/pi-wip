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

import org.springframework.cloud.iot.integration.coap.client.CoapMethod;
import org.springframework.cloud.iot.integration.coap.client.CoapOperations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.integration.expression.ExpressionUtils;
import org.springframework.integration.expression.ValueExpression;
import org.springframework.integration.handler.AbstractReplyProducingMessageHandler;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.MessagingException;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Inbound gateway using {@link CoapOperations}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapOutboundGateway extends AbstractReplyProducingMessageHandler {

	private final CoapOperations coapOperations;

	private volatile StandardEvaluationContext evaluationContext;
	private volatile Expression expectedResponseTypeExpression;
	private volatile Expression coapMethodExpression = new ValueExpression<>(CoapMethod.POST);

	public CoapOutboundGateway(CoapOperations coapOperations) {
		this.coapOperations = coapOperations;
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
//			return coapOperations.getForObject((Class<?>) expectedResponseType);
			return coapOperations.postForObject(requestMessage.getPayload(), (Class<?>) expectedResponseType);
		} catch (MessagingException e) {
			throw e;
		} catch (Exception e) {
			throw new MessageHandlingException(requestMessage, "COAP request execution failed", e);
		}

	}

	public void setExpectedResponseType(Class<?> expectedResponseType) {
		Assert.notNull(expectedResponseType, "'expectedResponseType' must not be null");
		this.expectedResponseTypeExpression = new ValueExpression<Class<?>>(expectedResponseType);
	}

	public void setExpectedResponseTypeExpression(Expression expectedResponseTypeExpression) {
		this.expectedResponseTypeExpression = expectedResponseTypeExpression;
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

}
