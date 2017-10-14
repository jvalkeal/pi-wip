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
package org.springframework.coap.server.result.method.annotation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.springframework.coap.method.ResolvableMethod;
import org.springframework.coap.server.MockServerCoapExchange;
import org.springframework.coap.server.MockServerCoapRequest;
import org.springframework.coap.server.ServerCoapExchange;
import org.springframework.coap.server.result.method.annotation.ServerCoapExchangeArgumentResolver;
import org.springframework.core.MethodParameter;

import reactor.core.publisher.Mono;

public class ServerCoapExchangeArgumentResolverTests {

	private final ServerCoapExchangeArgumentResolver resolver =
			new ServerCoapExchangeArgumentResolver();

	private final MockServerCoapExchange exchange = MockServerCoapExchange.from(
			MockServerCoapRequest.get("/path").build());

	private ResolvableMethod testMethod = ResolvableMethod.on(getClass()).named("handle").build();

	public void handle(
			ServerCoapExchange exchange) {}

	@Test
	public void supportsParameter() throws Exception {
		assertThat(this.resolver.supportsParameter(this.testMethod.arg(ServerCoapExchange.class))).isTrue();
	}

	@Test
	public void resolveArgument() throws Exception {
		testResolveArgument(this.testMethod.arg(ServerCoapExchange.class), this.exchange);
	}

	private void testResolveArgument(MethodParameter parameter, Object expected) {
		Mono<Object> mono = this.resolver.resolveArgument(parameter, this.exchange);
		assertThat(exchange).isEqualTo(mono.block());
	}
}
