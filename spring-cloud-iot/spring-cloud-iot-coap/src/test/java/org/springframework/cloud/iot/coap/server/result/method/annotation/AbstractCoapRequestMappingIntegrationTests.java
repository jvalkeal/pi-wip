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
package org.springframework.cloud.iot.coap.server.result.method.annotation;

import java.net.URI;

import org.junit.After;
import org.junit.Before;
import org.springframework.cloud.iot.coap.californium.CoapTemplate;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Base class for tests for request mapping tests which uses a real
 * running coap server.
 *
 * @author Janne Valkealahti
 *
 */
public abstract class AbstractCoapRequestMappingIntegrationTests {

	private ApplicationContext applicationContext;

	@Before
	public void setup() throws Exception {
		this.applicationContext = initApplicationContext();
	}

	@After
	public void tearDown() throws Exception {
		if (this.applicationContext instanceof ConfigurableApplicationContext) {
			((ConfigurableApplicationContext)this.applicationContext).close();
		}
	}

	protected abstract ApplicationContext initApplicationContext();

	protected ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}

	protected <T> T performGet(String url, Class<T> type) throws Exception {
		CoapTemplate template = new CoapTemplate();
		URI uri = new URI("coap://localhost:5683" + url);
		return template.getForObject(uri, type);
	}

}
