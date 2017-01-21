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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.junit.Test;
import org.springframework.cloud.iot.integration.coap.AbstractCoapTests;
import org.springframework.cloud.iot.integration.coap.TestCoapServerConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import reactor.core.publisher.Flux;

/**
 * Tests for {@link CoapTemplate}.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapTemplateTests extends AbstractCoapTests {

	@Test
	public void testGetForObject() throws URISyntaxException {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource1", null, null);
		CoapTemplate template = new CoapTemplate(uri);
		String object = template.getForObject(String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello"));
	}

	@Test
	public void testPostForObject() throws URISyntaxException {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource4", null, null);
		CoapTemplate template = new CoapTemplate(uri);
		String object = template.postForObject("hello", String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("echo:hello"));
	}

	@Test
	public void testObserveForObject() throws Exception {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource3", null, null);
		CoapTemplate template = new CoapTemplate(uri);
		Flux<String> flux = template.observeForObject(String.class);
		ArrayList<String> rr = new ArrayList<>();
		flux.take(3).doOnNext(s -> {
			System.out.println("WWW2 " + s);
			rr.add(s);
		}).subscribe();
		Thread.sleep(5000);
		assertThat(rr.size(), is(3));
	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}
}