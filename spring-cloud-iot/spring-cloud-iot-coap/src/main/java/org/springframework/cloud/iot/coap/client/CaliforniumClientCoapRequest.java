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
package org.springframework.cloud.iot.coap.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.CoapStatus;
import org.springframework.util.Assert;

/**
 * {@link ClientCoapRequest} using Californium {@link CoapClient}.
 *
 * @author Janne Valkealahti
 *
 */
public class CaliforniumClientCoapRequest implements ClientCoapRequest {

	private final CoapClient coapClient;
	private final CoapMethod coapMethod;
	private Integer contentFormat;
	private Integer accept;
	private byte[] requestPayload;

	/**
	 * Instantiates a new californium client coap request.
	 *
	 * @param coapClient the coap client
	 * @param coapMethod the coap method
	 */
	public CaliforniumClientCoapRequest(CoapClient coapClient, CoapMethod coapMethod) {
		Assert.notNull(coapClient, "CoapClient must be set");
		Assert.notNull(coapMethod, "CoapMethod must be set");
		this.coapClient = coapClient;
		this.coapMethod = coapMethod;
	}

	@Override
	public void setContentFormat(Integer contentFormat) {
		this.contentFormat = contentFormat;
	}

	@Override
	public void setAccept(Integer accept) {
		this.accept = accept;
	}

	@Override
	public void setRequestPayload(byte[] requestPayload) {
		this.requestPayload = requestPayload;
	}

	@Override
	public ClientCoapResponse execute() {
		CoapResponse response = null;
		if (coapMethod == CoapMethod.GET) {
			if (contentFormat != null) {
				response = coapClient.get(contentFormat);
			} else {
				response = coapClient.get();
			}
		} else if (coapMethod == CoapMethod.POST) {
			if (accept != null) {
				response = coapClient.post(requestPayload, contentFormat, accept);
			} else {
				response = coapClient.post(requestPayload, contentFormat);
			}
		} else if (coapMethod == CoapMethod.DELETE) {
			response = coapClient.delete();
		} else if (coapMethod == CoapMethod.PUT) {
			response = coapClient.put(requestPayload, contentFormat);
		} else {
			// well, should not actually happen
			throw new IllegalArgumentException("Unsupported coap method, was [" + coapMethod + "]");
		}
		return new DefaultClientCoapResponse(response.getPayload(), CoapStatus.valueOf(response.advanced().getRawCode()));
	}
}
