/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.iot.coap.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

/**
 * Annotation for mapping coap requests onto specific handler classes and/or
 * handler methods.
 *
 * @author Janne Valkealahti
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CoapRequestMapping {

	/**
	 * Assign a name to this mapping.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used on both levels, a combined name is derived by concatenation
	 * with "#" as separator.
	 *
	 * @return the name
	 */
	String name() default "";

	/**
	 * The primary mapping expressed by this annotation.
	 * <p>This is an alias for {@link #path}. For example
	 * {@code @RequestMapping("/foo")} is equivalent to
	 * {@code @RequestMapping(path="/foo")}.
	 * <p><b>Supported at the type level as well as at the method level!</b>
	 * When used at the type level, all method-level mappings inherit
	 * this primary mapping, narrowing it for a specific handler method.
	 *
	 * @return the values
	 */
	@AliasFor("path")
	String[] value() default {};

	/**
	 * Alias for {@link #value()}.
	 *
	 * @see #value()
	 * @return the paths
	 */
	@AliasFor("value")
	String[] path() default {};
}
