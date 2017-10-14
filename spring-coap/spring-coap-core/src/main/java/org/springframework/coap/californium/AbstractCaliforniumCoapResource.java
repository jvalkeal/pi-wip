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
package org.springframework.coap.californium;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.Resource;

/**
 * Base implementation of a {@link CoapResource}.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class AbstractCaliforniumCoapResource extends CoapResource {

	/**
	 * Instantiates a new abstract coap resource.
	 *
	 * @param name the name of a resource
	 */
	public AbstractCaliforniumCoapResource(String name) {
		super(name);
	}

	@Override
	public Resource getChild(String name) {
		return this;
	}
}
