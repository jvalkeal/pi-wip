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
package org.springframework.coap.server;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;

import org.junit.Test;
import org.springframework.coap.AbstractCoapTests;
import org.springframework.coap.CoapServerFactoryBean;
import org.springframework.coap.TestCoapServerConfiguration;
import org.springframework.coap.californium.CoapTemplate;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Tests for {@link CoapServerFactoryBean} and other generic concepts.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapServerTests extends AbstractCoapTests {

	@Test
	public void testResourceAcceptsAllNestedPaths() throws Exception {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource2", null, null);
		CoapTemplate template = new CoapTemplate();
		String object = template.getForObject(uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello2"));

		uri = new URI("coap", null, "localhost", 5683, "/testresource2/wildcard", null, null);
		template = new CoapTemplate();
		object = template.getForObject(uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello2"));

		uri = new URI("coap", null, "localhost", 5683, "/testresource2/*", null, null);
		template = new CoapTemplate();
		object = template.getForObject(uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello2"));

		uri = new URI("coap", null, "localhost", 5683, "/testresource2/*/*/*/*/xxx", null, null);
		template = new CoapTemplate();
		object = template.getForObject(uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello2"));
	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}
}
