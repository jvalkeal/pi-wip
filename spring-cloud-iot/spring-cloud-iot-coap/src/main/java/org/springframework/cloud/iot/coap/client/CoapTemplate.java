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

import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapObserveRelation;
import org.eclipse.californium.core.CoapResponse;
import org.springframework.cloud.iot.coap.CoapEntity;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.CoapResponseEntity;
import org.springframework.cloud.iot.coap.converter.ByteArrayCoapMessageConverter;
import org.springframework.cloud.iot.coap.converter.CoapMessageConverter;
import org.springframework.cloud.iot.coap.converter.GsonCoapMessageConverter;
import org.springframework.cloud.iot.coap.converter.StringCoapMessageConverter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.util.DefaultUriBuilderFactory;
import org.springframework.web.util.UriBuilderFactory;

import reactor.core.publisher.Flux;

/**
 * Central class communicating coap endpoints from a client.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapTemplate implements CoapOperations {

	private final Log logger = LogFactory.getLog(CoapTemplate.class);
	private final List<CoapMessageConverter<?>> messageConverters = new ArrayList<CoapMessageConverter<?>>();
	private UriBuilderFactory uriBuilderFactory = new DefaultUriBuilderFactory();

	private static final boolean gsonPresent =
			ClassUtils.isPresent("com.google.gson.Gson", CoapTemplate.class.getClassLoader());
	/**
	 * Instantiates a new coap template.
	 */
	public CoapTemplate() {
		this.messageConverters.add(new ByteArrayCoapMessageConverter());
		this.messageConverters.add(new StringCoapMessageConverter());

		if (gsonPresent) {
			this.messageConverters.add(new GsonCoapMessageConverter());
		}
	}

	@Override
	public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		RequestCallback requestCallback = new AcceptHeaderRequestCallback(responseType);
		return execute(url, CoapMethod.GET, requestCallback, responseExtractor);
	}

	@Override
	public <T> T getForObject(URI url, Class<T> responseType) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		RequestCallback requestCallback = new AcceptHeaderRequestCallback(responseType);
		return execute(url, CoapMethod.GET, requestCallback, responseExtractor);
	}

	@Override
	public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		RequestCallback requestCallback = new RequestBodyRequestCallback(request, responseType);
		return execute(url, CoapMethod.POST, requestCallback, responseExtractor);
	}

	@Override
	public <T> T postForObject(URI url, Object request, Class<T> responseType) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		RequestCallback requestCallback = new RequestBodyRequestCallback(request, responseType);
		return execute(url, CoapMethod.POST, requestCallback, responseExtractor);
	}

	@Override
	public <T> Flux<T> observeForObject(String url, Class<T> responseType, Object... uriVariables) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		return observe(url, responseExtractor, uriVariables);
	}

	@Override
	public <T> Flux<T> observeForObject(URI url, Class<T> responseType) {
		CoapMessageConverterExtractor<T> responseExtractor =
				new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
		return observe(url, responseExtractor);
	}

	@Override
	public <T> CoapResponseEntity<T> exchange(URI url, CoapMethod method, CoapEntity<?> requestEntity, Class<T> responseType) {
		RequestCallback requestCallback = coapEntityCallback(requestEntity, responseType);
		ResponseExtractor<CoapResponseEntity<T>> responseExtractor = new ResponseEntityResponseExtractor<>(responseType);
		return execute(url, method, requestCallback, responseExtractor);
	}

	/**
	 * Gets the message converters.
	 *
	 * @return the message converters
	 */
	public List<CoapMessageConverter<?>> getMessageConverters() {
		return messageConverters;
	}

	/**
	 * Gets the uri builder factory.
	 *
	 * @return the uri builder factory
	 */
	public UriBuilderFactory getUriBuilderFactory() {
		return uriBuilderFactory;
	}

	/**
	 * Sets the uri builder factory.
	 *
	 * @param uriBuilderFactory the new uri builder factory
	 */
	public void setUriBuilderFactory(UriBuilderFactory uriBuilderFactory) {
		Assert.notNull(uriBuilderFactory, "'uriBuilderFactory' must not be null");
		this.uriBuilderFactory = uriBuilderFactory;
	}

	public <T> T execute(String url, CoapMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor,
			Object... urlVariables) {
		URI expanded = getUriBuilderFactory().expand(url, urlVariables);
		return doExecute(expanded, method, requestCallback, responseExtractor);
	}

	public <T> T execute(URI url, CoapMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
		return doExecute(url, method, requestCallback, responseExtractor);
	}

	public <T> Flux<T> observe(String url, ResponseExtractor<T> responseExtractor, Object... urlVariables) {
		URI expanded = getUriBuilderFactory().expand(url, urlVariables);
		return doObserve(expanded, responseExtractor);
	}

	public <T> Flux<T> observe(URI url, ResponseExtractor<T> responseExtractor) {
		return doObserve(url, responseExtractor);
	}

	protected <T> Flux<T> doObserve(URI url, ResponseExtractor<T> responseExtractor) {
		final CoapClient client = new CoapClient(url);
		return Flux.<T>create(emitter -> {
			CoapObserveRelation observe = client.observe(new CoapHandler() {
				@Override
				public void onLoad(CoapResponse response) {
					final byte[] payload = response.getPayload();
					ClientCoapResponse r = new DefaultClientCoapResponse(payload, null);
					emitter.next(responseExtractor.extractData(r));
				}

				@Override
				public void onError() {
					// request either timeout'd or was rejected, complete stream
					emitter.error(new CoapClientException("Request for url [" + url + "] either timeout'd or was rejected"));
				}
			});

			emitter.setCancellation(() -> {
				logger.debug("Emitter cancellation, proactive cancel for coap observer");
				observe.proactiveCancel();
			});

		});

	}

	protected <T> RequestCallback coapEntityCallback(Object requestBody, Type responseType) {
		return new CoapEntityRequestCallback(requestBody, responseType);
	}

	protected <T> T doExecute(URI url, CoapMethod method, RequestCallback requestCallback, ResponseExtractor<T> responseExtractor) {
		Assert.notNull(method, "'method' must not be null");
		ClientCoapResponse response = null;

		ClientCoapRequest request = createRequest(url, method);
		if (requestCallback != null) {
			requestCallback.doWithRequest(request);
		}

		response = request.execute();
		if (responseExtractor != null) {
			return responseExtractor.extractData(response);
		} else {
			return null;
		}
	}

	private ClientCoapRequest createRequest(URI url, CoapMethod method) {
		final CoapClient client = new CoapClient(url);
		CaliforniumClientCoapRequest request = new CaliforniumClientCoapRequest(client, method);
		return request;
	}

	private class CoapEntityRequestCallback extends AcceptHeaderRequestCallback {

		private final CoapEntity<?> requestEntity;

		public CoapEntityRequestCallback(Object requestBody) {
			this(requestBody, null);
		}

		private CoapEntityRequestCallback(Object requestBody, Type responseType) {
			super(responseType);
			if (requestBody instanceof CoapEntity) {
				this.requestEntity = (CoapEntity<?>) requestBody;
			}
			else if (requestBody != null) {
				this.requestEntity = new CoapEntity<>(requestBody);
			}
			else {
				this.requestEntity = CoapEntity.EMPTY;
			}
		}

		@Override
		public void doWithRequest(ClientCoapRequest request) {
			super.doWithRequest(request);

			Object requestBody = this.requestEntity.getBody();

			for (CoapMessageConverter<?> converter : getMessageConverters()) {
				if (converter.canWrite(requestBody.getClass(), null)) {
					((CoapMessageConverter<Object>)converter).write(requestBody, request);
					request.setContentFormat(converter.getSupportedContentFormat());
					return;
				}
			}

			String message = "Could not write request: no suitable CoapMessageConverter found for request type [" +
					requestBody.getClass() + "]";
			throw new CoapClientException(message);
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
					((CoapMessageConverter<Object>)converter).write(requestBody, request);
					request.setContentFormat(converter.getSupportedContentFormat());
					return;
				}
			}

			String message = "Could not write request: no suitable CoapMessageConverter found for request type [" +
					requestBody.getClass() + "]";
			throw new CoapClientException(message);
		}
	}

	/**
	 * Response extractor for {@link CoapEntity}.
	 */
	private class ResponseEntityResponseExtractor<T> implements ResponseExtractor<CoapResponseEntity<T>> {

		private final CoapMessageConverterExtractor<T> delegate;

		public ResponseEntityResponseExtractor(Type responseType) {
			if (responseType != null && Void.class != responseType) {
				this.delegate = new CoapMessageConverterExtractor<>(responseType, getMessageConverters());
			}
			else {
				this.delegate = null;
			}
		}

		@Override
		public CoapResponseEntity<T> extractData(ClientCoapResponse response) {
			if (this.delegate != null) {
				T body = this.delegate.extractData(response);
				return new CoapResponseEntity<>(body, response.getStatusCode());
			}
			else {
//				return new ResponseEntity<>(response.getHeaders(), response.getStatusCode());
				return new CoapResponseEntity<>(response.getStatusCode());
			}
		}
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
							request.setAccept(supportedContentFormat);
							return;
						}
					}
				}
			}
		}
	}
}
