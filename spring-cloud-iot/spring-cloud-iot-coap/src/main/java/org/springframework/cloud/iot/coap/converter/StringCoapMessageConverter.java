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
package org.springframework.cloud.iot.coap.converter;

import java.nio.charset.Charset;

import org.springframework.cloud.iot.coap.CoapInputMessage;
import org.springframework.cloud.iot.coap.CoapOutputMessage;

public class StringCoapMessageConverter extends AbstractCoapMessageConverter<String> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("ISO-8859-1");
	private final Charset defaultCharset;

	public StringCoapMessageConverter() {
		this(DEFAULT_CHARSET);
	}

	public StringCoapMessageConverter(Charset defaultCharset) {
		super(0);
		this.defaultCharset = defaultCharset;
	}

	@Override
	public String read(Class<? extends String> clazz, CoapInputMessage inputMessage) {
		if (inputMessage.getBody() != null) {
			return new String(inputMessage.getBody(), defaultCharset);
		} else {
			return null;
		}
	}

	@Override
	public void write(String t, CoapOutputMessage outputMessage) {
		outputMessage.setRequestPayload(t.getBytes(defaultCharset));
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return String.class == clazz;
	}
}
