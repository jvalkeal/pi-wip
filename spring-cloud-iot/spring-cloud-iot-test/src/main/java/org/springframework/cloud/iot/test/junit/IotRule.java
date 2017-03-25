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
package org.springframework.cloud.iot.test.junit;

import org.junit.rules.TestRule;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import com.pi4j.system.SystemInfo;
import com.pi4j.system.SystemInfo.BoardType;

/**
 * Rule skipping tests if iot environment is not available. Essentially this
 * means that test is not run on embedded device.
 *
 * @author Janne Valkealahti
 *
 */
public class IotRule extends TestWatcher implements TestRule {

	@Override
	public Statement apply(Statement base, Description description) {

		try {
			BoardType boardType = SystemInfo.getBoardType();
			if (boardType == BoardType.UNKNOWN) {
				throw new RuntimeException();
			}
		} catch (Exception e) {
			return super.apply(new Statement() {
				@Override
				public void evaluate() throws Throwable {
				}
			}, Description.EMPTY);
		}
		return super.apply(base, description);
	}
}
