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

import org.springframework.cloud.iot.coap.CoapStatus;

public class DefaultClientCoapResponse implements ClientCoapResponse {

	private final byte[] payload;
	private final CoapStatus status;

	public DefaultClientCoapResponse(byte[] payload, CoapStatus status) {
		this.payload = payload;
		this.status = status;
	}

//	public DefaultClientCoapResponse(byte[] payload) {
//		this.payload = payload;
//	}

	@Override
	public byte[] getBody() {
		return payload;
	}

	@Override
	public CoapStatus getStatusCode() {
		return status;
	}
}
