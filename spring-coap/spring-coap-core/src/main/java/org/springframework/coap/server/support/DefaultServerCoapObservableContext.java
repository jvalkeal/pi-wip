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
package org.springframework.coap.server.support;

import org.springframework.coap.server.ServerCoapObservableContext;

import reactor.core.Disposable;
import reactor.core.publisher.Flux;

public class DefaultServerCoapObservableContext implements ServerCoapObservableContext {

	private Flux<Object> source;
	private Object result;
	private Disposable disposable;

	public DefaultServerCoapObservableContext(Flux<Object> source) {
		this.source = source;
	}

	@Override
	public void setObservableSource(Flux<Object> source) {
		this.source = source;
	}

	@Override
	public Flux<Object> getObservableSource() {
		return source;
	}

	@Override
	public void setResult(Object result) {
		this.result = result;
	}

	@Override
	public Object getResult() {
		return result;
	}

	@Override
	public void setDisposable(Disposable disposable) {
		this.disposable = disposable;
	}

	@Override
	public Disposable getDisposable() {
		return disposable;
	}
}
