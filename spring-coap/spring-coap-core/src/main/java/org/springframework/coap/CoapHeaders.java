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
package org.springframework.coap;

import java.util.ArrayList;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StringUtils;

/**
 * Represents COAP request and response headers, mapping integer header names to a list of byte[] values.
 *
 * @author Janne Valkealahti
 *
 */
public class CoapHeaders extends LinkedMultiValueMap<Integer, byte[]> {

	private static final long serialVersionUID = 9213168835890681490L;
	public static final String ACCEPT = "Accept";
	public static final int ACCEPT_VALUE = 17;
	public static final String CONTENT_FORMAT = "Content-Format";
	public static final int CONTENT_FORMAT_VALUE = 12;

	@Nullable
	public MediaType getContentFormat() {
		byte[] contentFormat = getFirst(CONTENT_FORMAT_VALUE);
		return contentFormat.length > 0 ? MediaType.parseMediaType(new String(contentFormat)) : null;
//		return (StringUtils.hasLength(value) ? MediaType.parseMediaType(value) : null);
	}

	public List<MediaType> getAccept() {
		byte[] accept = getFirst(ACCEPT_VALUE);
		ArrayList<MediaType> accepts = new ArrayList<>();
		if (accept.length > 0) {
			accepts.add(MediaType.parseMediaType(new String(accept)));
		}
		return accepts;
	}
//	   The range of 2048..64999 is
//	   for all other options including private or vendor-specific ones,
//	   which undergo a Designated Expert review to help ensure that the
//	   option semantics are defined correctly.

	/**
	 * Return the first header value for the given header name, if any.
	 * @param headerName the header name
	 * @return the first header value, or {@code null} if none
	 */
//	@Override
//	@Nullable
//	public String getFirst(String headerName) {
//		List<String> headerValues = this.headers.get(headerName);
//		return (headerValues != null ? headerValues.get(0) : null);
//	}


}
