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
package org.springframework.cloud.iot.integration.coap.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.cloud.iot.integration.coap.converter.CoapMessageConverter;

public class CoapTemplate implements CoapOperations {

	private final CoapClient client;
	private final List<CoapMessageConverter<?>> messageConverters = new ArrayList<CoapMessageConverter<?>>();

	public CoapTemplate(URI uri) {
		this.client = new CoapClient(uri);
	}

	@Override
	public <T> T getForObject(Class<T> responseType, Object... uriVariables) {
		CoapResponse response = client.get();
		byte[] payload = response.getPayload();
		return null;
	}

	protected <T> T doExecute() {
		return null;
	}
}
