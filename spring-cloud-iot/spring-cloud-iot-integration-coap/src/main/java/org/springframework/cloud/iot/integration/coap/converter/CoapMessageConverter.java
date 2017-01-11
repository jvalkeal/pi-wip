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
package org.springframework.cloud.iot.integration.coap.converter;

import java.util.List;

public interface CoapMessageConverter<T> {

	boolean canRead(Class<?> clazz, int contentFormat);

	boolean canWrite(Class<?> clazz, int contentFormat);

	List<Integer> getSupportedContentFormats();

	T read(Class<? extends T> clazz, byte[] message) throws Exception;

	void write(T t, int contentFormat, byte[] message) throws Exception;

}
