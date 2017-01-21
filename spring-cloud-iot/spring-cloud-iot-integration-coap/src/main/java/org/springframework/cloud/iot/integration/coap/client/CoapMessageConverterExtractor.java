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
import java.lang.reflect.Type;
import java.util.List;

import org.springframework.cloud.iot.integration.coap.converter.CoapMessageConverter;

public class CoapMessageConverterExtractor<T> implements ResponseExtractor<T> {

	private final List<CoapMessageConverter<?>> messageConverters;
	private final Class<T> responseClass;

	public CoapMessageConverterExtractor(Type responseType, List<CoapMessageConverter<?>> messageConverters) {
		this.messageConverters = messageConverters;
		this.responseClass = (responseType instanceof Class) ? (Class<T>) responseType : null;
	}

	@Override
	public T extractData(ClientCoapResponse response) {

		for (CoapMessageConverter<?> converter : messageConverters) {

			if (responseClass != null) {
				if (converter.canRead(this.responseClass, 0)) {
					return (T) converter.read((Class)responseClass, response.getBody());
				}
			}

		}

		return null;
	}

}
