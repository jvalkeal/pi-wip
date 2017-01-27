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

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.cloud.iot.integration.coap.converter.ByteArrayCoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.converter.CoapMessageConverter;
import org.springframework.cloud.iot.integration.coap.converter.StringCoapMessageConverter;
import org.springframework.util.Assert;

import reactor.core.publisher.Flux;

/**
 * Central class communicating coap endpoints from a client.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapTemplate implements CoapOperations {

	private final CoapClient client;
	private final List<CoapMessageConverter<?>> messageConverters = new ArrayList<CoapMessageConverter<?>>();

	public CoapTemplate(URI uri) {
		this.client = new CoapClient(uri);
		this.messageConverters.add(new StringCoapMessageConverter());
		this.messageConverters.add(new ByteArrayCoapMessageConverter());
	}

	@Override
	public <T> T getForObject(Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		RequestCallback requestCallback = new AcceptHeaderRequestCallback(responseType);
		return doExecute(CoapMethod.GET, requestCallback, responseExtractor);
	}

	@Override
	public <T> T postForObject(Object request, Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		RequestCallback requestCallback = new RequestBodyRequestCallback(request, responseType);
		return doExecute(CoapMethod.POST, requestCallback, responseExtractor);
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

	protected <T> T doExecute(CoapMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
		Assert.notNull(method, "'method' must not be null");
		ClientCoapResponse response = null;

		ClientCoapRequest rr = createRequest(method);
		if (requestCallback != null) {
			requestCallback.doWithRequest(rr);
		}

		response = rr.execute();

		if (responseExtractor != null) {
			return responseExtractor.extractData(response);
		} else {
			return null;
		}

	}

	private ClientCoapRequest createRequest(CoapMethod method) {
		CaliforniumClientCoapRequest request = new CaliforniumClientCoapRequest(client, method);
		return request;
	}

	private class AcceptHeaderRequestCallback implements RequestCallback {

		private final Type responseType;

		public AcceptHeaderRequestCallback(Type responseType) {
			this.responseType = responseType;
		}

		@Override
		public void doWithRequest(ClientCoapRequest request) {
			if (this.responseType != null) {
				Class<?> responseClass = null;
				if (this.responseType instanceof Class) {
					responseClass = (Class<?>) this.responseType;
				}
				for (CoapMessageConverter<?> converter : getMessageConverters()) {
					if (responseClass != null) {
						if (converter.canRead(responseClass, null)) {
							Integer supportedContentFormat = converter.getSupportedContentFormat();
							request.setContentFormat(supportedContentFormat);
							return;
						}
					}
				}
			}
		}
	}

	private class RequestBodyRequestCallback extends AcceptHeaderRequestCallback {

		private final Object requestBody;

		public RequestBodyRequestCallback(Object requestBody, Type responseType) {
			super(responseType);
			this.requestBody = requestBody;
		}

		@SuppressWarnings("unchecked")
		@Override
		public void doWithRequest(ClientCoapRequest request) {
			super.doWithRequest(request);

			for (CoapMessageConverter<?> converter : getMessageConverters()) {
				if (converter.canWrite(requestBody.getClass(), null)) {
					byte[] payload = ((CoapMessageConverter<Object>)converter).write(requestBody);
					request.setRequestPayload(payload);
					return;
				}
			}
		}
	}
}
