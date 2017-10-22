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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
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

	/**
	 * Public constant media type that includes all media ranges (i.e. "&#42;/&#42;").
	 */
	public static final MediaType ALL;

	/**
	 * A String equivalent of {@link MediaType#ALL}.
	 */
	public static final String ALL_VALUE = "*/*";

	public final static MediaType APPLICATION_JSON;
	public final static String APPLICATION_JSON_VALUE = "application/json";
	public final static MediaType TEXT_PLAIN;
	public final static String TEXT_PLAIN_VALUE = "text/plain";

	static {
		ALL = valueOf(TEXT_PLAIN_VALUE);
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
			throw new InvalidMediaTypeException(ex);
		}
		try {
//			return new MediaType(type.getType(), type.getSubtype(), type.getParameters());
			return new MediaType(type.getType());
		}
		catch (IllegalArgumentException ex) {
			throw new InvalidMediaTypeException(mediaType, ex.getMessage());
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

	/**
	 * Indicate whether this {@code MediaType} includes the given media type.
	 * <p>For instance, {@code text/*} includes {@code text/plain} and {@code text/html}, and {@code application/*+xml}
	 * includes {@code application/soap+xml}, etc. This method is <b>not</b> symmetric.
	 *
	 * @param other the reference media type with which to compare
	 * @return {@code true} if this media type includes the given media type; {@code false} otherwise
	 */
	public boolean includes(@Nullable MediaType other) {
		return super.includes(other);
	}

	/**
	 * Indicate whether this {@code MediaType} is compatible with the given media type.
	 * <p>For instance, {@code text/*} is compatible with {@code text/plain}, {@code text/html}, and vice versa.
	 * In effect, this method is similar to {@link #includes(MediaType)}, except that it <b>is</b> symmetric.
	 *
	 * @param other the reference media type with which to compare
	 * @return {@code true} if this media type is compatible with the given media type; {@code false} otherwise
	 */
	public boolean isCompatibleWith(@Nullable MediaType other) {
		return super.isCompatibleWith(other);
	}

	/**
	 * Sorts the given list of {@code MediaType} objects by quality value.
	 * <p>Given two media types:
	 * <ol>
	 * <li>if the two media types have different {@linkplain #getQualityValue() quality value}, then the media type
	 * with the highest quality value is ordered before the other.</li>
	 * <li>if either media type has a {@linkplain #isWildcardType() wildcard type}, then the media type without the
	 * wildcard is ordered before the other.</li>
	 * <li>if the two media types have different {@linkplain #getType() types}, then they are considered equal and
	 * remain their current order.</li>
	 * <li>if either media type has a {@linkplain #isWildcardSubtype() wildcard subtype}, then the media type without
	 * the wildcard is sorted before the other.</li>
	 * <li>if the two media types have different {@linkplain #getSubtype() subtypes}, then they are considered equal
	 * and remain their current order.</li>
	 * <li>if the two media types have a different amount of {@linkplain #getParameter(String) parameters}, then the
	 * media type with the most parameters is ordered before the other.</li>
	 * </ol>
	 * @param mediaTypes the list of media types to be sorted
	 * @see #getQualityValue()
	 */
	public static void sortByQualityValue(List<MediaType> mediaTypes) {
		Assert.notNull(mediaTypes, "'mediaTypes' must not be null");
		if (mediaTypes.size() > 1) {
			Collections.sort(mediaTypes, QUALITY_VALUE_COMPARATOR);
		}
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

	/**
	 * Comparator used by {@link #sortByQualityValue(List)}.
	 */
	public static final Comparator<MediaType> QUALITY_VALUE_COMPARATOR = (mediaType1, mediaType2) -> {
		double quality1 = mediaType1.getQualityValue();
		double quality2 = mediaType2.getQualityValue();
		int qualityComparison = Double.compare(quality2, quality1);
		if (qualityComparison != 0) {
			return qualityComparison;  // audio/*;q=0.7 < audio/*;q=0.3
		}
		else if (mediaType1.isWildcardType() && !mediaType2.isWildcardType()) { // */* < audio/*
			return 1;
		}
		else if (mediaType2.isWildcardType() && !mediaType1.isWildcardType()) { // audio/* > */*
			return -1;
		}
		else if (!mediaType1.getType().equals(mediaType2.getType())) { // audio/basic == text/html
			return 0;
		}
		else { // mediaType1.getType().equals(mediaType2.getType())
			if (mediaType1.isWildcardSubtype() && !mediaType2.isWildcardSubtype()) { // audio/* < audio/basic
				return 1;
			}
			else if (mediaType2.isWildcardSubtype() && !mediaType1.isWildcardSubtype()) { // audio/basic > audio/*
				return -1;
			}
			else if (!mediaType1.getSubtype().equals(mediaType2.getSubtype())) { // audio/basic == audio/wave
				return 0;
			}
			else {
				int paramsSize1 = mediaType1.getParameters().size();
				int paramsSize2 = mediaType2.getParameters().size();
				return (paramsSize2 < paramsSize1 ? -1 : (paramsSize2 == paramsSize1 ? 0 : 1)); // audio/basic;level=1 < audio/basic
			}
		}
	};

	public static List<MediaType> parseMediaTypes(String value) {
		// TODO Auto-generated method stub
		return null;
	}

}
