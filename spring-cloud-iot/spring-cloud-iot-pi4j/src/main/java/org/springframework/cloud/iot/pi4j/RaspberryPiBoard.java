package org.springframework.cloud.iot.pi4j;

import org.springframework.cloud.iot.Board;

import com.pi4j.system.SystemInfo;

public class RaspberryPiBoard implements Board {

	@Override
	public Float getTemperature() {
		try {
			return SystemInfo.getCpuTemperature();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
