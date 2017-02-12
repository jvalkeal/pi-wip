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
		CoapTemplate template = new CoapTemplate();
		String object = template.getForObject(uri, String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("hello"));
	}

	@Test
	public void testGetForObjectByteArray() throws URISyntaxException {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource1", null, null);
		CoapTemplate template = new CoapTemplate();
		byte[] bytes = template.getForObject(uri, byte[].class);
		assertThat(bytes, notNullValue());
		String object = new String(bytes);
		assertThat(object, is("hello"));
	}

	@Test
	public void testPostForObject() throws URISyntaxException {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource4", null, null);
		CoapTemplate template = new CoapTemplate();
		String object = template.postForObject(uri, "hello", String.class);
		assertThat(object, notNullValue());
		assertThat(object, is("echo:hello"));
	}

	@Test
	public void testObserveForObject() throws Exception {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		URI uri = new URI("coap", null, "localhost", 5683, "/testresource3", null, null);
		CoapTemplate template = new CoapTemplate();
		Flux<String> flux = template.observeForObject(uri, String.class);
		ArrayList<String> rr = new ArrayList<>();
		flux.take(3).doOnNext(s -> {
			System.out.println("WWW2 " + s);
			rr.add(s);
		}).subscribe();
		Thread.sleep(5000);
		assertThat(rr.size(), is(3));
	}

	@Test
	public void testPostForObjectJson() throws URISyntaxException {
		context.register(TestCoapServerConfiguration.class);
		context.refresh();

		Bean1 beanOut = new Bean1(10, "jack");

		URI uri = new URI("coap", null, "localhost", 5683, "/echo", null, null);
		CoapTemplate template = new CoapTemplate();
		Bean1 beanIn = template.postForObject(uri, beanOut, Bean1.class);
		assertThat(beanIn, notNullValue());
		assertThat(beanIn.age, is(beanOut.age));
		assertThat(beanIn.name, is(beanOut.name));
	}


	public static class Bean1 {
		private Integer age;
		private String name;
		public Bean1() {
		}
		public Bean1(Integer age, String name) {
			this.age = age;
			this.name = name;
		}
		public Integer getAge() {
			return age;
		}
		public void setAge(Integer age) {
			this.age = age;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
	}

	@Override
	protected AnnotationConfigApplicationContext buildContext() {
		return new AnnotationConfigApplicationContext();
	}
}
