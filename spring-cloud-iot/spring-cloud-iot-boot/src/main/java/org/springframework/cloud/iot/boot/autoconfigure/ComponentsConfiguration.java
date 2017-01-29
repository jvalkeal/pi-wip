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
package org.springframework.cloud.iot.boot.autoconfigure;

import java.util.Map.Entry;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.RaspberryConfigurationProperties;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties.ComponentType;
import org.springframework.cloud.iot.boot.properties.IotConfigurationProperties.NumberingScheme;
import org.springframework.cloud.iot.component.Button;
import org.springframework.cloud.iot.component.DimmedLed;
import org.springframework.cloud.iot.component.HumiditySensor;
import org.springframework.cloud.iot.component.IncrementalRotary;
import org.springframework.cloud.iot.component.Relay;
import org.springframework.cloud.iot.component.ShiftRegister;
import org.springframework.cloud.iot.pi4j.Pi4jButton;
import org.springframework.cloud.iot.pi4j.Pi4jDimmedLed;
import org.springframework.cloud.iot.pi4j.Pi4jGpioRelayComponent;
import org.springframework.cloud.iot.pi4j.Pi4jIncrementalRotary;
import org.springframework.cloud.iot.pi4j.Pi4jPCF8574Lcd;
import org.springframework.cloud.iot.pi4j.Pi4jPCF8591Potentiometer;
import org.springframework.cloud.iot.pi4j.Pi4jPCF8591TemperatureSensor;
import org.springframework.cloud.iot.pi4j.Pi4jShiftRegister;
import org.springframework.cloud.iot.pi4j.component.Pi4jDHT11HumiditySensor;
import org.springframework.cloud.iot.pi4j.support.Termistor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import com.pi4j.component.lcd.impl.I2CLcdDisplay;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalMultipurpose;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.GpioPinPwmOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinMode;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.RaspiBcmPin;
import com.pi4j.io.gpio.RaspiPin;

@Configuration
public class ComponentsConfiguration extends AbstractConfigurationSupport implements ImportBeanDefinitionRegistrar {

	private final static String BEAN_PREFIX_GPIO = "GPIO_";
	private final static String BEAN_PREFIX_I2C = "I2C_";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		IotConfigurationProperties iotProperties = buildIotProperties();
		RaspberryConfigurationProperties raspberryProperties = buildRaspberryProperties();

		for (Entry<String, ComponentType> e : iotProperties.getComponents().entrySet()) {
			String name = e.getKey();
			ComponentType type = e.getValue();

			if (type.getIncrementalRotary() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jGpioIncrementalRotaryComponentGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(type.getIncrementalRotary().getIncrementSteps()));
				bdb.addConstructorArgValue(String.valueOf(type.getIncrementalRotary().getGpio().getLeftPin()));
				bdb.addConstructorArgValue(String.valueOf(type.getIncrementalRotary().getGpio().getRightPin()));
				bdb.addConstructorArgValue(raspberryProperties.getNumberingScheme());
				registry.registerBeanDefinition(BEAN_PREFIX_GPIO + name, bdb.getBeanDefinition());
			} else if (type.getButton() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jGpioButtonComponentGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(type.getButton().getGpio().getPin()));
				bdb.addConstructorArgValue(raspberryProperties.getNumberingScheme());
				registry.registerBeanDefinition(BEAN_PREFIX_GPIO + name, bdb.getBeanDefinition());
			} else if (type.getRelay() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jGpioRelayComponentGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(type.getRelay().getGpio().getPin()));
				bdb.addConstructorArgValue(raspberryProperties.getNumberingScheme());
				registry.registerBeanDefinition(BEAN_PREFIX_GPIO + name, bdb.getBeanDefinition());
			} else if (type.getDimmedLed() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jDimmedLedGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(type.getDimmedLed().getGpio().getPin()));
				bdb.addConstructorArgValue(raspberryProperties.getNumberingScheme());
				registry.registerBeanDefinition(BEAN_PREFIX_GPIO + name, bdb.getBeanDefinition());
			} else if (type.getShiftRegister() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jGpioShiftRegisterComponentGpioFactoryBean.class);
				bdb.addConstructorArgValue(String.valueOf(type.getShiftRegister().getGpio().getSdiPin()));
				bdb.addConstructorArgValue(String.valueOf(type.getShiftRegister().getGpio().getRclkPin()));
				bdb.addConstructorArgValue(String.valueOf(type.getShiftRegister().getGpio().getSrclkPin()));
				bdb.addConstructorArgValue(raspberryProperties.getNumberingScheme());
				registry.registerBeanDefinition(BEAN_PREFIX_GPIO + name, bdb.getBeanDefinition());
			} else if (type.getPotentiometer() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jPCF8591Potentiometer.class);
				bdb.addConstructorArgValue(type.getPotentiometer().getI2c().getBus());
				bdb.addConstructorArgValue(type.getPotentiometer().getI2c().getAddress());
				bdb.addConstructorArgValue(type.getPotentiometer().getMin());
				bdb.addConstructorArgValue(type.getPotentiometer().getMax());
				registry.registerBeanDefinition(BEAN_PREFIX_I2C + name, bdb.getBeanDefinition());
			} else if (type.getLcd() != null) {
				I2CLcdDisplay lcd = getI2CLcdDisplay(type.getLcd().getI2c().getBus(), type.getLcd().getI2c().getAddress(),
						type.getLcd().getRows(), type.getLcd().getColumns());
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jPCF8574Lcd.class);
				bdb.addConstructorArgValue(lcd);
				registry.registerBeanDefinition(BEAN_PREFIX_I2C + name, bdb.getBeanDefinition());
			} else if (type.getTermistor() != null) {
				Termistor termistor = getTermistor(type.getTermistor().getI2c().getBus(), type.getTermistor().getI2c().getAddress(),
						type.getTermistor().getSupplyVoltage(), type.getTermistor().getDacBits(), type.getTermistor().getResistance(),
						type.getTermistor().getBeta(), type.getTermistor().getReferenceTemp());
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jPCF8591TemperatureSensor.class);
				bdb.addConstructorArgValue(null);
				bdb.addConstructorArgValue(termistor);
				registry.registerBeanDefinition(BEAN_PREFIX_I2C + name, bdb.getBeanDefinition());
			} else if (type.getHumidity() != null) {
				BeanDefinitionBuilder bdb = BeanDefinitionBuilder.rootBeanDefinition(Pi4jDHT11HumiditySensorComponentGpioFactoryBean.class);
				bdb.addConstructorArgValue(type.getHumidity().getGpio().getPin());
				bdb.addConstructorArgValue(raspberryProperties.getNumberingScheme());
				registry.registerBeanDefinition(BEAN_PREFIX_GPIO + name, bdb.getBeanDefinition());
			}
		}

	}

	protected Termistor getTermistor(int i2cBus, int i2cAddr, double voltageSupply, int dacBits, int resistance, double beta,
			double referenceTemp) {
		try {
			return new Termistor(i2cBus, i2cAddr, voltageSupply, dacBits, resistance, beta, referenceTemp);
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}

	public static class Pi4jDHT11HumiditySensorComponentGpioFactoryBean  implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;

		private NumberingScheme numberingScheme;
		private String pinName;
		private Pi4jDHT11HumiditySensor object;

		public Pi4jDHT11HumiditySensorComponentGpioFactoryBean(String pinName, NumberingScheme numberingScheme) {
			this.pinName = pinName;
			this.numberingScheme = numberingScheme;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin pin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + pinName)
					: RaspiPin.getPinByName("GPIO " + pinName);
			GpioPinDigitalMultipurpose multi = gpioController.provisionDigitalMultipurposePin(pin, PinMode.DIGITAL_OUTPUT);
			object = new Pi4jDHT11HumiditySensor(multi);
			if (object instanceof InitializingBean) {
				((InitializingBean)object).afterPropertiesSet();
			}
		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return HumiditySensor.class;
		}
	}

	public static class Pi4jGpioShiftRegisterComponentGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private NumberingScheme numberingScheme;
		private String sdiPinName;
		private String rclkPinName;
		private String srclkPinName;
		private Pi4jShiftRegister object;

		public Pi4jGpioShiftRegisterComponentGpioFactoryBean(String sdiPinName, String rclkPinName, String srclkPinName,
				NumberingScheme numberingScheme) {
			this.sdiPinName = sdiPinName;
			this.rclkPinName = rclkPinName;
			this.srclkPinName = srclkPinName;
			this.numberingScheme = numberingScheme;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin sdiPin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + sdiPinName)
					: RaspiPin.getPinByName("GPIO " + sdiPinName);
			Pin rclkPin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + rclkPinName)
					: RaspiPin.getPinByName("GPIO " + rclkPinName);
			Pin srclkPin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + srclkPinName)
					: RaspiPin.getPinByName("GPIO " + srclkPinName);


			GpioPinDigitalOutput sdi = gpioController.provisionDigitalOutputPin(sdiPin);
			GpioPinDigitalOutput rclk = gpioController.provisionDigitalOutputPin(rclkPin);
			GpioPinDigitalOutput srclk = gpioController.provisionDigitalOutputPin(srclkPin);
			object = new Pi4jShiftRegister(sdi, rclk, srclk);
			if (object instanceof InitializingBean) {
				((InitializingBean)object).afterPropertiesSet();
			}

		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return ShiftRegister.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

	public static class Pi4jGpioIncrementalRotaryComponentGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private Integer incrementSteps;
		private String leftPinName;
		private String rightPinName;
		private NumberingScheme numberingScheme;
		private Pi4jIncrementalRotary object;

		public Pi4jGpioIncrementalRotaryComponentGpioFactoryBean(Integer incrementSteps, String leftPinName, String rightPinName,
				NumberingScheme numberingScheme) {
			this.incrementSteps = incrementSteps;
			this.leftPinName = leftPinName;
			this.rightPinName = rightPinName;
			this.numberingScheme = numberingScheme;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin leftPin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + leftPinName)
					: RaspiPin.getPinByName("GPIO " + leftPinName);
			Pin rightPin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + rightPinName)
					: RaspiPin.getPinByName("GPIO " + rightPinName);
			GpioPinDigitalInput left = gpioController.provisionDigitalInputPin(leftPin);
			GpioPinDigitalInput right = gpioController.provisionDigitalInputPin(rightPin);
			object = new Pi4jIncrementalRotary(incrementSteps, left, right);
			if (object instanceof InitializingBean) {
				((InitializingBean)object).afterPropertiesSet();
			}

		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return IncrementalRotary.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

	public static class Pi4jGpioButtonComponentGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private String pinName;
		private NumberingScheme numberingScheme;
		private Pi4jButton object;

		public Pi4jGpioButtonComponentGpioFactoryBean(String pinName, NumberingScheme numberingScheme) {
			this.pinName = pinName;
			this.numberingScheme = numberingScheme;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin pin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + pinName)
					: RaspiPin.getPinByName("GPIO " + pinName);
			GpioPinDigitalInput input = gpioController.provisionDigitalInputPin(pin, PinPullResistance.PULL_UP);
			object = new Pi4jButton(input);
			if (object instanceof InitializingBean) {
				((InitializingBean)object).afterPropertiesSet();
			}

		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return Button.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

	public static class Pi4jGpioRelayComponentGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private String pinName;
		private NumberingScheme numberingScheme;
		private Pi4jGpioRelayComponent object;

		public Pi4jGpioRelayComponentGpioFactoryBean(String pinName, NumberingScheme numberingScheme) {
			this.pinName = pinName;
			this.numberingScheme = numberingScheme;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin pin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + pinName)
					: RaspiPin.getPinByName("GPIO " + pinName);
			GpioPinDigitalOutput output = gpioController.provisionDigitalOutputPin(pin);
			object = new Pi4jGpioRelayComponent(output);
		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return Relay.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

	public static class Pi4jDimmedLedGpioFactoryBean implements FactoryBean<Object>, InitializingBean {

		@Autowired
		private GpioController gpioController;
		private String pinName;
		private NumberingScheme numberingScheme;
		private Pi4jDimmedLed object;

		public Pi4jDimmedLedGpioFactoryBean(String pinName, NumberingScheme numberingScheme) {
			this.pinName = pinName;
			this.numberingScheme = numberingScheme;
		}

		@Override
		public void afterPropertiesSet() throws Exception {
			Pin pin = numberingScheme == NumberingScheme.BROADCOM ? RaspiBcmPin.getPinByName("GPIO " + pinName)
					: RaspiPin.getPinByName("GPIO " + pinName);
			GpioPinPwmOutput output = null;
			if (pin.getSupportedPinModes().contains(PinMode.PWM_OUTPUT)) {
				output = gpioController.provisionPwmOutputPin(pin, 0);
			} else {
				output = gpioController.provisionSoftPwmOutputPin(pin, 0);
			}

			object = new Pi4jDimmedLed(output);
		}

		@Override
		public Object getObject() throws Exception {
			return object;
		}

		@Override
		public Class<?> getObjectType() {
			return DimmedLed.class;
		}

		@Override
		public boolean isSingleton() {
			return true;
		}
	}

}
