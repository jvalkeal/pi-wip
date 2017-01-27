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
package org.springframework.cloud.iot;

import java.io.IOException;
import java.rmi.RemoteException;

import org.springframework.dao.UncategorizedDataAccessException;

/**
 * General exception indicating a problem in components interacting with IoT.
 * Main point of wrapping native IoT exceptions inside this is to have common
 * Spring dao exception hierarchy.
 *
 * @author Janne Valkealahti
 *
 */
public class IotSystemException extends UncategorizedDataAccessException {

	private static final long serialVersionUID = 7925336771888200719L;

	/**
	 * Constructs IotSystemException from {@link IOException}.
	 *
	 * @param e the {@link RemoteException}
	 */
	public IotSystemException(IOException e) {
		super(e.getMessage(), e);
	}

	/**
	 * Constructs a generic IotSystemException.
	 *
	 * @param message the message
	 * @param e the exception
	 */
	public IotSystemException(String message, Exception e) {
		super(message, e);
	}

	/**
	 * Constructs a generic IotSystemException.
	 *
	 * @param message the message
	 * @param cause the throwable cause
	 */
	public IotSystemException(String message, Throwable cause) {
		super(message, cause);
	}
}
