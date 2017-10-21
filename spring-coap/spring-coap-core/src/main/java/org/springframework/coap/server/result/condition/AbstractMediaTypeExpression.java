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
package org.springframework.coap.server.result.condition;

import org.springframework.coap.MediaType;
import org.springframework.coap.server.ServerCoapExchange;

abstract class AbstractMediaTypeExpression implements Comparable<AbstractMediaTypeExpression>, MediaTypeExpression {

	private final MediaType mediaType;

	private final boolean isNegated;

	AbstractMediaTypeExpression(String expression) {
		if (expression.startsWith("!")) {
			this.isNegated = true;
			expression = expression.substring(1);
		}
		else {
			this.isNegated = false;
		}
		this.mediaType = MediaType.parseMediaType(expression);
	}

	AbstractMediaTypeExpression(MediaType mediaType, boolean negated) {
		this.mediaType = mediaType;
		this.isNegated = negated;
	}

	@Override
	public MediaType getMediaType() {
		return this.mediaType;
	}

	@Override
	public boolean isNegated() {
		return this.isNegated;
	}

	@Override
	public int compareTo(AbstractMediaTypeExpression other) {
		return MediaType.SPECIFICITY_COMPARATOR.compare(this.getMediaType(), other.getMediaType());
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.isNegated) {
			builder.append('!');
		}
		builder.append(this.mediaType.toString());
		return builder.toString();
	}

	public final boolean match(ServerCoapExchange exchange) {
		try {
			boolean match = matchMediaType(exchange);
			return (!this.isNegated == match);
		}
		catch (Exception ex) {
			return false;
		}

//		catch (NotAcceptableStatusException ex) {
//			return false;
//		}
//		catch (UnsupportedMediaTypeStatusException ex) {
//			return false;
//		}
	}

	protected abstract boolean matchMediaType(ServerCoapExchange exchange)
			throws RuntimeException/*NotAcceptableStatusException, UnsupportedMediaTypeStatusException*/;
}
