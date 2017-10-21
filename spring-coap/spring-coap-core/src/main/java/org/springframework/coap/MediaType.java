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

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

import org.springframework.util.InvalidMimeTypeException;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.MimeType.SpecificityComparator;

/**
 *
 * @author Janne Valkealahti
 *
 */
public class MediaType extends MimeType implements Serializable {

	private static final String PARAM_QUALITY_FACTOR = "q";

	public final static MediaType APPLICATION_JSON;
	public final static String APPLICATION_JSON_VALUE = "application/json";
	public final static MediaType TEXT_PLAIN;
	public final static String TEXT_PLAIN_VALUE = "text/plain";

	static {
		APPLICATION_JSON = valueOf(APPLICATION_JSON_VALUE);
		TEXT_PLAIN = valueOf(TEXT_PLAIN_VALUE);
	}

	public MediaType(String type) {
		super(type);
	}

	/**
	 * Parse the given String value into a {@code MediaType} object,
	 * with this method name following the 'valueOf' naming convention
	 * (as supported by {@link org.springframework.core.convert.ConversionService}.
	 *
	 * @param value the string to parse
	 * @see #parseMediaType(String)
	 * @return the parsed media type
	 */
	public static MediaType valueOf(String value) {
		return parseMediaType(value);
	}

	public static MediaType parseMediaType(String mediaType) {
		MimeType type;
		try {
			type = MimeTypeUtils.parseMimeType(mediaType);
		}
		catch (InvalidMimeTypeException ex) {
			throw new RuntimeException(ex);
//			throw new InvalidMediaTypeException(ex);
		}
		try {
//			return new MediaType(type.getType(), type.getSubtype(), type.getParameters());
			return new MediaType(type.getType());
		}
		catch (IllegalArgumentException ex) {
			throw new RuntimeException(ex);
//			throw new InvalidMediaTypeException(mediaType, ex.getMessage());
		}
	}

	/**
	 * Return the quality value, as indicated by a {@code q} parameter, if any.
	 * Defaults to {@code 1.0}.
	 * @return the quality factory
	 */
	public double getQualityValue() {
		String qualityFactory = getParameter(PARAM_QUALITY_FACTOR);
		return (qualityFactory != null ? Double.parseDouble(unquote(qualityFactory)) : 1D);
	}

	public static final Comparator<MediaType> SPECIFICITY_COMPARATOR = new SpecificityComparator<MediaType>() {

		@Override
		protected int compareParameters(MediaType mediaType1, MediaType mediaType2) {
			double quality1 = mediaType1.getQualityValue();
			double quality2 = mediaType2.getQualityValue();
			int qualityComparison = Double.compare(quality2, quality1);
			if (qualityComparison != 0) {
				return qualityComparison;  // audio/*;q=0.7 < audio/*;q=0.3
			}
			return super.compareParameters(mediaType1, mediaType2);
		}
	};

	public static List<MediaType> parseMediaTypes(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
