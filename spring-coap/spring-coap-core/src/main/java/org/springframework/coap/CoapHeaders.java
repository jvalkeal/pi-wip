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

import org.springframework.util.LinkedMultiValueMap;

public class CoapHeaders extends LinkedMultiValueMap<Integer, byte[]> {

	private static final long serialVersionUID = 9213168835890681490L;

//	   The range of 2048..64999 is
//	   for all other options including private or vendor-specific ones,
//	   which undergo a Designated Expert review to help ensure that the
//	   option semantics are defined correctly.

}
