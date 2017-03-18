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
package demo.sunfoundersensor10passive;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.iot.component.sound.PassiveBuzzer;
import org.springframework.cloud.iot.pi4j.support.Pi4jUtils;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
public class Application {

	// Frequency of Low C notes
	private int[] CL = new int[] { 0, 131, 147, 165, 175, 196, 211, 248 };

	// Frequency of Middle C notes
	private int[] CM = new int[] { 0, 262, 294, 330, 350, 393, 441, 495 };

	// Frequency of High C notes
	private int[] CH = new int[] { 0, 525, 589, 661, 700, 786, 882, 990 };

	// Notes of song1
	private int[] song_1 = new int[] {
			CM[3], CM[5], CM[6], CM[3], CM[2], CM[3], CM[5], CM[6],
			CH[1], CM[6], CM[5], CM[1], CM[3], CM[2], CM[2], CM[3],
			CM[5], CM[2], CM[3], CM[3], CL[6], CL[6], CL[6], CM[1],
			CM[2], CM[3], CM[2], CL[7], CL[6], CM[1], CL[5] };

	// Beats of song 1, 1 means 1/8 beats
	private int[] beat_1 = new int[] {
			1, 1, 3, 1, 1, 3, 1, 1,
			1, 1, 1, 1, 1, 1, 3, 1,
			1, 3, 1, 1, 1, 1, 1, 1,
			1, 2, 1, 1, 1, 1, 1, 1,
			1, 1, 3 };

	// Notes of song2
	private int[] song_2 = new int[]{
			CM[1], CM[1], CM[1], CL[5], CM[3], CM[3], CM[3], CM[1],
			CM[1], CM[3], CM[5], CM[5], CM[4], CM[3], CM[2], CM[2],
			CM[3], CM[4], CM[4], CM[3], CM[2], CM[3], CM[1], CM[1],
			CM[3], CM[2], CL[5], CL[7], CM[2], CM[1] };

	// Beats of song 2, 1 means 1/8 beats
	private int[] beat_2 = new int[] {
			1, 1, 2, 2, 1, 1, 2, 2,
			1, 1, 2, 2, 1, 1, 3, 1,
			1, 2, 2, 1, 1, 2, 2, 1,
			1, 2, 2, 1, 1, 3 };


	@Autowired
	private PassiveBuzzer buzzer;

	@Scheduled(fixedRate=1000)
	public void play() {
		for (int i = 0; i < song_1.length; i++) {
			// ChangeFrequency(song_1[i])
			Pi4jUtils.delay(song_1[i] / 2);
		}
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
