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
package org.springframework.cloud.iot.coap.server;

import org.springframework.cloud.iot.coap.CoapInputMessage;
import org.springframework.cloud.iot.coap.CoapMethod;
import org.springframework.cloud.iot.coap.server.support.RequestPath;

public interface ServerCoapRequest extends CoapInputMessage {

	/**
	 * Returns a structured representation of the request path including the
	 * context path + path within application portions, path segments with
	 * encoded and decoded values, and path parameters.
	 *
	 * @return the request path
	 */
	RequestPath getPath();

	int getContentFormat();

	CoapMethod getMethod();
}
