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
package org.springframework.cloud.iot.coap;

import java.lang.reflect.Type;
import java.net.URI;

/**
 * Extension of {@link CoapEntity}.
 *
 * @author Janne Valkealahti
 *
 * @param <T> the type of a entity body
 */
public class CoapRequestEntity<T> extends CoapEntity<T> {

	private final CoapMethod method;

	private final URI url;

	private final Type type;

	public CoapRequestEntity(CoapMethod method, URI url, Type type) {
		super();
		this.method = method;
		this.url = url;
		this.type = type;
	}

}
