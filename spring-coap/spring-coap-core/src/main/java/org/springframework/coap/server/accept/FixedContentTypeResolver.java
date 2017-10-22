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
package org.springframework.coap.server.accept;

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.coap.MediaType;
import org.springframework.coap.server.ServerCoapExchange;

public class FixedContentTypeResolver implements RequestedContentTypeResolver {

	private static final Log logger = LogFactory.getLog(FixedContentTypeResolver.class);


	private final List<MediaType> mediaTypes;


	/**
	 * Constructor with a single default {@code MediaType}.
	 *
	 * @param mediaType the media type
	 */
	public FixedContentTypeResolver(MediaType mediaType) {
		this(Collections.singletonList(mediaType));
	}

	/**
	 * Constructor with an ordered List of default {@code MediaType}'s to return
	 * for use in applications that support a variety of content types.
	 * <p>Consider appending {@link MediaType#ALL} at the end if destinations
	 * are present which do not support any of the other default media types.
	 *
	 * @param mediaTypes the media types
	 */
	public FixedContentTypeResolver(List<MediaType> mediaTypes) {
		this.mediaTypes = Collections.unmodifiableList(mediaTypes);
	}


	/**
	 * Return the configured list of media types.
	 *
	 * @return the configured list of media types
	 */
	public List<MediaType> getContentTypes() {
		return this.mediaTypes;
	}


	@Override
	public List<MediaType> resolveMediaTypes(ServerCoapExchange exchange) {
		if (logger.isDebugEnabled()) {
			logger.debug("Requested media types: " + this.mediaTypes);
		}
		return this.mediaTypes;
	}

}
