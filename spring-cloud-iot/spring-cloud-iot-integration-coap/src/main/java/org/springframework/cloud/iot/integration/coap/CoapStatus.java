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
package org.springframework.cloud.iot.integration.coap;

import org.eclipse.californium.core.coap.MessageFormatException;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

public enum CoapStatus {

	_UNKNOWN_SUCCESS_CODE(2, 0),
	CREATED(2, 1),
	DELETED(2, 2),
	VALID(2, 3),
	CHANGED(2, 4),
	CONTENT(2, 5),
	CONTINUE(2, 31),

	BAD_REQUEST(4, 0),
	UNAUTHORIZED(4, 1),
	BAD_OPTION(4, 2),
	FORBIDDEN(4, 3),
	NOT_FOUND(4, 4),
	METHOD_NOT_ALLOWED(4, 5),
	NOT_ACCEPTABLE(4, 6),
	REQUEST_ENTITY_INCOMPLETE(4, 8),
	PRECONDITION_FAILED(4, 12),
	REQUEST_ENTITY_TOO_LARGE(4, 13),
	UNSUPPORTED_CONTENT_FORMAT(4, 15),

	INTERNAL_SERVER_ERROR(5, 0),
	NOT_IMPLEMENTED(5, 1),
	BAD_GATEWAY(5, 2),
	SERVICE_UNAVAILABLE(5, 3),
	GATEWAY_TIMEOUT(5, 4),
	PROXY_NOT_SUPPORTED(5, 5);

	public final int value;
	public final int codeClass;
	public final int codeDetail;

	private CoapStatus(final int codeClass, final int codeDetail) {
		this.codeClass = codeClass;
		this.codeDetail = codeDetail;
		this.value = codeClass << 5 | codeDetail;
	}

	public static CoapStatus valueOf(final int value) {
		int codeClass = getCodeClass(value);
		int codeDetail = getCodeDetail(value);
		switch (codeClass) {
		case 2:
			return valueOfSuccessCode(codeDetail);
		case 4:
			return valueOfClientErrorCode(codeDetail);
		case 5:
			return valueOfServerErrorCode(codeDetail);
		default:
			throw new MessageFormatException(String.format("Not a CoAP response code: %s", formatCode(codeClass, codeDetail)));
		}
	}


	private static CoapStatus valueOfSuccessCode(final int codeDetail) {
		switch(codeDetail) {
		case 1: return CREATED;
		case 2: return DELETED;
		case 3: return VALID;
		case 4: return CHANGED;
		case 5: return CONTENT;
		case 31: return CONTINUE;
		default:
			return _UNKNOWN_SUCCESS_CODE;
		}
	}

	private static CoapStatus valueOfClientErrorCode(final int codeDetail) {
		switch(codeDetail) {
		case 0: return BAD_REQUEST;
		case 1: return UNAUTHORIZED;
		case 2: return BAD_OPTION;
		case 3: return FORBIDDEN;
		case 4: return NOT_FOUND;
		case 5: return METHOD_NOT_ALLOWED;
		case 6: return NOT_ACCEPTABLE;
		case 8: return REQUEST_ENTITY_INCOMPLETE;
		case 12: return PRECONDITION_FAILED;
		case 13: return REQUEST_ENTITY_TOO_LARGE;
		case 15: return UNSUPPORTED_CONTENT_FORMAT;
		default:
			return BAD_REQUEST;
		}
	}

	private static CoapStatus valueOfServerErrorCode(final int codeDetail) {
		switch(codeDetail) {
		case 0: return INTERNAL_SERVER_ERROR;
		case 1: return NOT_IMPLEMENTED;
		case 2: return BAD_GATEWAY;
		case 3: return SERVICE_UNAVAILABLE;
		case 4: return GATEWAY_TIMEOUT;
		case 5: return PROXY_NOT_SUPPORTED;
		default:
			return INTERNAL_SERVER_ERROR;
		}
	}

	private static String formatCode(final int codeClass, final int codeDetail) {
		return String.format("%d.%02d", codeClass, codeDetail);
	}

	/**
	 * Gets the code class of a given CoAP code.
	 *
	 * @param code the code.
	 * @return the value represented by the three most significant bits of the code.
	 */
	public static int getCodeClass(final int code) {
		return (code & 0b11100000) >> 5;
	}

	/**
	 * Gets the code detail of a given CoAP code.
	 *
	 * @param code the code.
	 * @return the value represented by the five least significant bits of the code.
	 */
	public static int getCodeDetail(final int code) {
		return code & 0b00011111;
	}



}
