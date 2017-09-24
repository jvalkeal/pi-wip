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
package org.springframework.cloud.iot.coap.server.support;

import java.util.Arrays;

import org.springframework.cloud.iot.coap.CoapHeaders;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.server.ServerCoapRequest;

public class GenericServerCoapRequest implements ServerCoapRequest {

	private final byte[] body;
	private final CoapHeaders coapHeaders;
	private CoapMethod method;
	private int contentFormat = -1;
	private String uriPath;

	public GenericServerCoapRequest(byte[] body, CoapHeaders coapHeaders) {
		this.body = body;
		this.coapHeaders = coapHeaders;
	}

	@Override
	public byte[] getBody() {
		return body;
	}

	@Override
	public int getContentFormat() {
		return contentFormat;
	}

	public void setContentFormat(int contentFormat) {
		this.contentFormat = contentFormat;
	}

	@Override
	public RequestPath getPath() {
		return null;
	}

	@Override
	public String getUriPath() {
		return uriPath;
	}

	public void setUriPath(String uriPath) {
		this.uriPath = uriPath;
	}

	public void setMethod(CoapMethod method) {
		this.method = method;
	}

	@Override
	public CoapMethod getMethod() {
		return method;
	}

	@Override
	public CoapHeaders getHeaders() {
		return coapHeaders;
	}

	@Override
	public String toString() {
		return "GenericServerCoapRequest [body=" + Arrays.toString(body) + ", coapHeaders=" + coapHeaders + ", method="
				+ method + ", contentFormat=" + contentFormat + "]";
	}
}
