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
package demo.gamebuttons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * {@link Component @Component} which is getting component scanned and
 * automatically created as a bean. Sole purpose of this class is to handle
 * connected game score display.
 *
 * @author Janne Valkealahti
 *
 */
@Component
public class DisplayController {

	private static final Logger log = LoggerFactory.getLogger(DisplayController.class);

	/**
	 * Sets the text
	 *
	 * @param text the new text
	 */
	public void setScore(String text) {
		log.info("Score {}", text);
	}
}