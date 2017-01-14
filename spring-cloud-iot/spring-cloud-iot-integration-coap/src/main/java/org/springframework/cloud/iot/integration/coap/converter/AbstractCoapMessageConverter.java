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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.util.Assert;

public abstract class AbstractCoapMessageConverter<T> implements CoapMessageConverter<T> {

	private List<Integer> supportedContentFormats = Collections.emptyList();

	protected AbstractCoapMessageConverter(Integer... supportedContentFormats) {
		setSupportedContentFormatTypes(Arrays.asList(supportedContentFormats));
	}

	public void setSupportedContentFormatTypes(List<Integer> supportedContentFormats) {
		Assert.notEmpty(supportedContentFormats, "'supportedContentFormats' must not be empty");
		this.supportedContentFormats = new ArrayList<>(supportedContentFormats);
	}

	@Override
	public List<Integer> getSupportedContentFormats() {
		return Collections.unmodifiableList(supportedContentFormats);
	}

	@Override
	public boolean canRead(Class<?> clazz, Integer contentFormat) {
		return supports(clazz) && canRead(contentFormat);
	}

	@Override
	public boolean canWrite(Class<?> clazz, Integer contentFormat) {
		return supports(clazz) && canWrite(contentFormat);
	}

	protected boolean canRead(Integer contentFormat) {
		if (contentFormat == null) {
			return true;
		}
		return getSupportedContentFormats().contains(contentFormat);
	}

	protected boolean canWrite(Integer contentFormat) {
		if (contentFormat == null) {
			return true;
		}
		return getSupportedContentFormats().contains(contentFormat);
	}

	protected abstract boolean supports(Class<?> clazz);

}
