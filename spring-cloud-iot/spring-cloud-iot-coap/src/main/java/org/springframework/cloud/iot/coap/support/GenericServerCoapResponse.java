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

import org.springframework.cloud.iot.coap.server.ServerCoapResponse;

public class GenericServerCoapResponse implements ServerCoapResponse {

	private Integer contentFormat;
	private Integer accept;
	private byte[] requestPayload;

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

	public Integer getContentFormat() {
		return contentFormat;
	}

	public Integer getAccept() {
		return accept;
	}

	public byte[] getRequestPayload() {
		return requestPayload;
	}

	@Override
	public byte[] getBody() {
		return requestPayload;
	}
}
