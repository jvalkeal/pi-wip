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
import org.eclipse.californium.core.CoapResponse;
import org.springframework.cloud.iot.integration.coap.converter.CoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.converter.StringCoapMessageConverter;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;
import reactor.core.publisher.UnicastProcessor;

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
		return doExecute(CoapMethod.GET, responseExtractor);
	}

	@Override
	public <T> T postForObject(Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		return doExecute(CoapMethod.POST, responseExtractor);
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

		final UnicastProcessor<T> processor = UnicastProcessor.create();

		client.observe(new CoapHandler() {

			@Override
			public void onLoad(CoapResponse response) {
				System.out.println("EEEE1");
				final byte[] payload = response.getPayload();
				ClientCoapResponse r = new ClientCoapResponse() {
					@Override
					public byte[] getBody() {
						return payload;
					}
				};

				try {
					processor.onNext(responseExtractor.extractData(r));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onError() {
				System.out.println("EEEE2");
			}
		});

		return processor;
	}

	protected <T> T doExecute(CoapMethod method, ResponseExtractor<T> responseExtractor) {
		Assert.notNull(method, "'method' must not be null");
		ClientCoapResponse response = null;

		if (method == CoapMethod.GET) {
			CoapResponse r = client.get();
			final byte[] payload = r.getPayload();
			response = new ClientCoapResponse() {
				@Override
				public byte[] getBody() {
					return payload;
				}
			};
		} else if (method == CoapMethod.POST) {
			CoapResponse r = client.post(new byte[0], 0);
			final byte[] payload = r.getPayload();
			response = new ClientCoapResponse() {
				@Override
				public byte[] getBody() {
					return payload;
				}
			};
		}

		try {
			if (responseExtractor != null) {
				return responseExtractor.extractData(response);
			} else {
				return null;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
