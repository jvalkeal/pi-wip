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

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.cloud.iot.integration.coap.converter.CoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.converter.StringCoapMessageConverter;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;

public class CoapTemplate implements CoapOperations {

	private final CoapClient client;
	private final List<CoapMessageConverter<?>> messageConverters = new ArrayList<CoapMessageConverter<?>>();

	public CoapTemplate(URI uri) {
		this.client = new CoapClient(uri);
		this.messageConverters.add(new StringCoapMessageConverter());
	}

	@Override
	public <T> T getForObject(Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		return doExecute(CoapMethod.GET, null, responseExtractor);
	}

	@Override
	public <T> T postForObject(Object request, Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		return doExecute(CoapMethod.POST, request, responseExtractor);
	}

	@Override
	public <T> Flux<T> observeForObject(Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		return doObserve(responseExtractor);
	}

	public List<CoapMessageConverter<?>> getMessageConverters() {
		return messageConverters;
	}

	protected <T> Flux<T> doObserve(ResponseExtractor<T> responseExtractor) {

		return Flux.<T>create(emitter -> {
			CoapObserveRelation observe = client.observe(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					final byte[] payload = response.getPayload();
					System.out.println("WWW1 " + new String(payload));
					ClientCoapResponse r = new DefaultClientCoapResponse(payload);
					emitter.next(responseExtractor.extractData(r));
				}

				@Override
				public void onError() {
				}
			});

			emitter.setCancellation(() -> {
				observe.proactiveCancel();
			});

		});

	}

	protected <T> T doExecute(CoapMethod method, Object request, ResponseExtractor<T> responseExtractor) {
		Assert.notNull(method, "'method' must not be null");
		ClientCoapResponse response = null;

		if (method == CoapMethod.GET) {
			CoapResponse r = client.get();
			final byte[] payload = r.getPayload();
			response = new DefaultClientCoapResponse(payload);
		} else if (method == CoapMethod.POST) {

			byte[] payload = null;
			for (CoapMessageConverter<?> converter : getMessageConverters()) {
				if (converter.canWrite(request.getClass(), 0)) {
					payload = ((CoapMessageConverter<Object>)converter).write(request, 0);
				}
			}
			CoapResponse r = client.post(payload, 0);
			response = new DefaultClientCoapResponse(r.getPayload());
		}

		if (responseExtractor != null) {
			return responseExtractor.extractData(response);
		} else {
			return null;
		}
	}
}