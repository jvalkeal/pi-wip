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
package org.springframework.cloud.iot.coap.support;

import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.server.ServerCoapRequest;

public class GenericServerCoapRequest implements ServerCoapRequest {

	private final byte[] body;
	private CoapMethod method;
	private int contentFormat = -1;

	public GenericServerCoapRequest(byte[] body) {
		this.body = body;
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
	public String getUriPath() {
		return null;
	}

	public void setMethod(CoapMethod method) {
		this.method = method;
	}

	@Override
	public CoapMethod getMethod() {
		return method;
	}

}
