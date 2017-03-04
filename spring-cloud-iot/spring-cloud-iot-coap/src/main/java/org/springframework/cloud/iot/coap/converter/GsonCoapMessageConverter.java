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

import java.lang.reflect.Type;
import java.nio.charset.Charset;

import org.springframework.cloud.iot.coap.CoapInputMessage;
import org.springframework.cloud.iot.coap.CoapOutputMessage;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.Assert;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

public class GsonCoapMessageConverter extends AbstractCoapMessageConverter<Object> {

	public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
	private Gson gson = new Gson();
	private String jsonPrefix;

	public GsonCoapMessageConverter() {
		super(0);
	}

	@Override
	public Object read(Class<? extends Object> clazz, CoapInputMessage inputMessage) {
		return read(clazz, clazz, inputMessage);
	}

	public Object read(Type type, Class<? extends Object> clazz, CoapInputMessage inputMessage) {
		TypeToken<?> token = getTypeToken(type);
		return readTypeToken(token, inputMessage);
	}

	@Override
	public void write(Object t, CoapOutputMessage outputMessage) {
		write(t, t.getClass(), outputMessage);
	}

	public void write(Object t, Type type, CoapOutputMessage outputMessage) {
		writeInternal(t, type, outputMessage);
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		return true;
	}

	protected TypeToken<?> getTypeToken(Type type) {
		return TypeToken.get(type);
	}

	private Object readTypeToken(TypeToken<?> token, CoapInputMessage inputMessage) {
		byte[] body = inputMessage.getBody();
		try {
			return this.gson.fromJson(new String(body), token.getType());
		}
		catch (JsonParseException ex) {
			throw new HttpMessageNotReadableException("Could not read JSON: " + ex.getMessage(), ex);
		}
	}

	/**
	 * Set the {@code Gson} instance to use.
	 * If not set, a default {@link Gson#Gson() Gson} instance is used.
	 * <p>Setting a custom-configured {@code Gson} is one way to take further
	 * control of the JSON serialization process.
	 *
	 * @param gson the new gson instance
	 */
	public void setGson(Gson gson) {
		Assert.notNull(gson, "'gson' is required");
		this.gson = gson;
	}

	/**
	 * Gets the configured {@code Gson} instance for this converter.
	 *
	 * @return the gson instance
	 */
	public Gson getGson() {
		return this.gson;
	}

	/**
	 * Specify a custom prefix to use for JSON output. Default is none.
	 *
	 * @param jsonPrefix the prefix to use
	 * @see #setPrefixJson
	 */
	public void setJsonPrefix(String jsonPrefix) {
		this.jsonPrefix = jsonPrefix;
	}

	/**
	 * Indicate whether the JSON output by this view should be prefixed with ")]}', ".
	 * Default is {@code false}.
	 * <p>Prefixing the JSON string in this manner is used to help prevent JSON
	 * Hijacking. The prefix renders the string syntactically invalid as a script
	 * so that it cannot be hijacked.
	 * This prefix should be stripped before parsing the string as JSON.
	 *
	 * @param prefixJson enable prefix
	 * @see #setJsonPrefix
	 */
	public void setPrefixJson(boolean prefixJson) {
		this.jsonPrefix = (prefixJson ? ")]}', " : null);
	}

	protected void writeInternal(Object o, Type type, CoapOutputMessage outputMessage) {
		StringBuilder buf = new StringBuilder();
		if (this.jsonPrefix != null) {
			buf.append(jsonPrefix);
		}
		if (type != null) {
			this.gson.toJson(o, type, buf);
		}
		else {
			this.gson.toJson(o, buf);
		}
		outputMessage.setRequestPayload(buf.toString().getBytes());
	}

}
