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

/**
 * Exceptions thrown by a coap server.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapServerException extends RuntimeException {

	private static final long serialVersionUID = -1847306814419948739L;

	/**
	 * Instantiates a new coap server exception.
	 *
	 * @param message the message
	 */
	public CoapServerException(String message) {
		super(message);
	}

	/**
	 * Instantiates a new coap server exception.
	 *
	 * @param message the message
	 * @param cause the cause
	 */
	public CoapServerException(String message, Throwable cause) {
		super(message, cause);
	}
}
