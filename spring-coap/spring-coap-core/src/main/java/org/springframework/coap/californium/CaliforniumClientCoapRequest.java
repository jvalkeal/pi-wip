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
package org.springframework.coap.californium;

import java.util.List;
import java.util.Map.Entry;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.springframework.coap.CoapHeaders;
import org.springframework.coap.CoapMethod;
import org.springframework.coap.CoapStatus;
import org.springframework.coap.client.ClientCoapRequest;
import org.springframework.coap.client.ClientCoapResponse;
import org.springframework.coap.client.DefaultClientCoapResponse;
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
	private final CoapHeaders coapHeaders;
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
		this(coapClient, coapMethod, null);
	}

	/**
	 * Instantiates a new californium client coap request.
	 *
	 * @param coapClient the coap client
	 * @param coapMethod the coap method
	 * @param coapHeaders the coap headers
	 */
	public CaliforniumClientCoapRequest(CoapClient coapClient, CoapMethod coapMethod, CoapHeaders coapHeaders) {
		Assert.notNull(coapClient, "CoapClient must be set");
		Assert.notNull(coapMethod, "CoapMethod must be set");
		this.coapClient = coapClient;
		this.coapMethod = coapMethod;
		this.coapHeaders = coapHeaders != null ? coapHeaders : new CoapHeaders();
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
	public CoapHeaders getHeaders() {
		return coapHeaders;
	}

	@Override
	public ClientCoapResponse execute() {
		CoapResponse response = null;

		Request request = null;

		if (coapMethod == CoapMethod.GET) {
			request = Request.newGet();
			if (contentFormat != null) {
				request.getOptions().setAccept(contentFormat);
			}
		} else if (coapMethod == CoapMethod.POST) {
			request = Request.newPost();
			request.setPayload(requestPayload);
			request.getOptions().setContentFormat(contentFormat);
			if (accept != null) {
				request.getOptions().setAccept(accept);
			}
		} else if (coapMethod == CoapMethod.DELETE) {
			request = Request.newDelete();
		} else if (coapMethod == CoapMethod.PUT) {
			request = Request.newPut();
		} else {
			// well, should not actually happen
			throw new IllegalArgumentException("Unsupported coap method, was [" + coapMethod + "]");
		}

		if (coapHeaders != null) {
			OptionSet options = request.getOptions();
			for (Entry<Integer, List<byte[]>> entry : coapHeaders.entrySet()) {
				for (byte[] optionValue : entry.getValue()) {
					options.addOption(new Option(entry.getKey(), optionValue));
				}
			}
		}

		response = coapClient.advanced(request);
		return new DefaultClientCoapResponse(response.getPayload(), CoapStatus.valueOf(response.advanced().getRawCode()));
	}

	@Override
	public byte[] getBody() {
		return requestPayload;
	}
}
