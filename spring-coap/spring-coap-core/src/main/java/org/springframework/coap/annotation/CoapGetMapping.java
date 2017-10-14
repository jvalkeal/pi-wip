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
package org.springframework.coap.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.coap.CoapMethod;
import org.springframework.core.annotation.AliasFor;

/**
 * Annotation for mapping COAP {@code GET} requests onto specific handler
 * methods.
 *
 * @author Janne Valkealahti
 *
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@CoapRequestMapping(method = CoapMethod.GET)
public @interface CoapGetMapping {

	/**
	 * Alias for {@link CoapRequestMapping#name}.
	 *
	 * @return the name
	 */
	@AliasFor(annotation = CoapRequestMapping.class)
	String name() default "";

	/**
	 * Alias for {@link CoapRequestMapping#value}.
	 *
	 * @return the values
	 */
	@AliasFor(annotation = CoapRequestMapping.class)
	String[] value() default {};

	/**
	 * Alias for {@link CoapRequestMapping#path}.
	 *
	 * @return the paths
	 */
	@AliasFor(annotation = CoapRequestMapping.class)
	String[] path() default {};

	/**
	 * Alias for {@link CoapRequestMapping#params}.
	 *
	 * @return the params
	 */
	@AliasFor(annotation = CoapRequestMapping.class)
	String[] params() default {};

	/**
	 * Alias for {@link CoapRequestMapping#headers}.
	 *
	 * @return the headers
	 */
	@AliasFor(annotation = CoapRequestMapping.class)
	String[] headers() default {};

	/**
	 * Alias for {@link CoapRequestMapping#consumes}.
	 *
	 * @return the produces
	 */
	@AliasFor(annotation = CoapRequestMapping.class)
	String[] consumes() default {};

	/**
	 * Alias for {@link CoapRequestMapping#produces}.
	 *
	 * @return the produces
	 */
	@AliasFor(annotation = CoapRequestMapping.class)
	String[] produces() default {};
}
