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
package demo.coapserver;

import org.springframework.coap.MediaType;
import org.springframework.coap.annotation.CoapController;
import org.springframework.coap.annotation.CoapGetMapping;
import org.springframework.coap.annotation.CoapResponseBody;

@CoapController
public class MediaTypes {

	@CoapGetMapping(path = "/mediatypes/consume", consumes = MediaType.TEXT_PLAIN_VALUE)
	@CoapResponseBody
	public String consumeTextPlain() {
		return "Hello from consumeTextPlain";
	}

	@CoapGetMapping(path = "/mediatypes/consume", consumes = MediaType.APPLICATION_JSON_VALUE)
	@CoapResponseBody
	public String consumeApplicationJson() {
		return "Hello from consumeApplicationJson";
	}

	@CoapGetMapping(path = "/mediatypes/produce", produces = MediaType.TEXT_PLAIN_VALUE)
	@CoapResponseBody
	public String produceTextPlain() {
		return "Hello from produceTextPlain";
	}

	@CoapGetMapping(path = "/mediatypes/produce", produces = MediaType.APPLICATION_JSON_VALUE)
	@CoapResponseBody
	public String produceApplicationJson() {
		return "Hello from produceApplicationJson";
	}

}
