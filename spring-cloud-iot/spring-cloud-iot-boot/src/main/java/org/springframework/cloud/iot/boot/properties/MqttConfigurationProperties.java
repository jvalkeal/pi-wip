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
package org.springframework.cloud.iot.boot.properties;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Properties for "spring.cloud.iot.mqtt".
 *
 * @author Janne Valkealahti
 *
 */
@ConfigurationProperties(prefix = MqttConfigurationProperties.NAMESPACE)
public class MqttConfigurationProperties {

	public static final String NAMESPACE = "spring.cloud.iot.mqtt";

	/**
	 * MQTT host.
	 */
	private String host = "localhost";

	/**
	 * MQTT port.
	 */
	private int port = 1883;

	/**
	 * Login user to authenticate to the broker.
	 */
	private String username;

	/**
	 * Login to authenticate against the broker.
	 */
	private String password;

	/**
	 * SSL configuration.
	 */
	private final Ssl ssl = new Ssl();

	/**
	 * Comma-separated list of addresses to which the client should connect.
	 */
	private String addresses;

	/**
	 * Connection timeout, in milliseconds; zero for infinite.
	 */
	private Integer connectionTimeout;

	private List<Address> parsedAddresses;

	public String getHost() {
		return this.host;
	}

	/**
	 * Returns the host from the first address, or the configured host if no addresses
	 * have been set.
	 * @return the host
	 * @see #setAddresses(String)
	 * @see #getHost()
	 */
	public String determineHost() {
		if (CollectionUtils.isEmpty(this.parsedAddresses)) {
			return getHost();
		}
		return this.parsedAddresses.get(0).host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return this.port;
	}

	/**
	 * Returns the port from the first address, or the configured port if no addresses
	 * have been set.
	 * @return the port
	 * @see #setAddresses(String)
	 * @see #getPort()
	 */
	public int determinePort() {
		if (CollectionUtils.isEmpty(this.parsedAddresses)) {
			return getPort();
		}
		Address address = this.parsedAddresses.get(0);
		return address.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getAddresses() {
		return this.addresses;
	}

	/**
	 * Returns the comma-separated addresses or a single address ({@code host:port})
	 * created from the configured host and port if no addresses have been set.
	 * @return the addresses
	 */
	public List<String> determineAddresses() {
		ArrayList<String> addressesList = new ArrayList<>();
		if (CollectionUtils.isEmpty(this.parsedAddresses)) {
			addressesList.add(Address.PREFIX_MQTT + this.host + ":" + this.port);
			return addressesList;
		}
		for (Address parsedAddress : this.parsedAddresses) {
			addressesList.add(Address.PREFIX_MQTT + parsedAddress.host + ":" + parsedAddress.port);
		}
		return addressesList;
	}

	public void setAddresses(String addresses) {
		this.addresses = addresses;
		this.parsedAddresses = parseAddresses(addresses);
	}

	private List<Address> parseAddresses(String addresses) {
		List<Address> parsedAddresses = new ArrayList<Address>();
		for (String address : StringUtils.commaDelimitedListToStringArray(addresses)) {
			parsedAddresses.add(new Address(address));
		}
		return parsedAddresses;
	}

	public String getUsername() {
		return this.username;
	}

	/**
	 * If addresses have been set and the first address has a username it is returned.
	 * Otherwise returns the result of calling {@code getUsername()}.
	 * @return the username
	 * @see #setAddresses(String)
	 * @see #getUsername()
	 */
	public String determineUsername() {
		if (CollectionUtils.isEmpty(this.parsedAddresses)) {
			return this.username;
		}
		Address address = this.parsedAddresses.get(0);
		return address.username == null ? this.username : address.username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return this.password;
	}

	/**
	 * If addresses have been set and the first address has a password it is returned.
	 * Otherwise returns the result of calling {@code getPassword()}.
	 * @return the password or {@code null}
	 * @see #setAddresses(String)
	 * @see #getPassword()
	 */
	public String determinePassword() {
		if (CollectionUtils.isEmpty(this.parsedAddresses)) {
			return getPassword();
		}
		Address address = this.parsedAddresses.get(0);
		return address.password == null ? getPassword() : address.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Ssl getSsl() {
		return this.ssl;
	}

	public Integer getConnectionTimeout() {
		return this.connectionTimeout;
	}

	public void setConnectionTimeout(Integer connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}

	public static class Ssl {

		/**
		 * Enable SSL support.
		 */
		private boolean enabled;

		/**
		 * Path to the key store that holds the SSL certificate.
		 */
		private String keyStore;

		/**
		 * Password used to access the key store.
		 */
		private String keyStorePassword;

		/**
		 * Trust store that holds SSL certificates.
		 */
		private String trustStore;

		/**
		 * Password used to access the trust store.
		 */
		private String trustStorePassword;

		/**
		 * SSL algorithm to use (e.g. TLSv1.1). Default is set automatically by the paho
		 * client library.
		 */
		private String algorithm;

		public boolean isEnabled() {
			return this.enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}

		public String getKeyStore() {
			return this.keyStore;
		}

		public void setKeyStore(String keyStore) {
			this.keyStore = keyStore;
		}

		public String getKeyStorePassword() {
			return this.keyStorePassword;
		}

		public void setKeyStorePassword(String keyStorePassword) {
			this.keyStorePassword = keyStorePassword;
		}

		public String getTrustStore() {
			return this.trustStore;
		}

		public void setTrustStore(String trustStore) {
			this.trustStore = trustStore;
		}

		public String getTrustStorePassword() {
			return this.trustStorePassword;
		}

		public void setTrustStorePassword(String trustStorePassword) {
			this.trustStorePassword = trustStorePassword;
		}

		public String getAlgorithm() {
			return this.algorithm;
		}

		public void setAlgorithm(String sslAlgorithm) {
			this.algorithm = sslAlgorithm;
		}

	}

	private static final class Address {

		private static final String PREFIX_MQTT = "tcp://";

		private static final int DEFAULT_PORT = 1883;

		private String host;

		private int port;

		private String username;

		private String password;

		private Address(String input) {
			input = input.trim();
			input = trimPrefix(input);
			input = parseUsernameAndPassword(input);
			parseHostAndPort(input);
		}

		private String trimPrefix(String input) {
			if (input.startsWith(PREFIX_MQTT)) {
				input = input.substring(PREFIX_MQTT.length());
			}
			return input;
		}

		private String parseUsernameAndPassword(String input) {
			if (input.contains("@")) {
				String[] split = StringUtils.split(input, "@");
				String creds = split[0];
				input = split[1];
				split = StringUtils.split(creds, ":");
				this.username = split[0];
				if (split.length > 0) {
					this.password = split[1];
				}
			}
			return input;
		}

		private void parseHostAndPort(String input) {
			int portIndex = input.indexOf(':');
			if (portIndex == -1) {
				this.host = input;
				this.port = DEFAULT_PORT;
			}
			else {
				this.host = input.substring(0, portIndex);
				this.port = Integer.valueOf(input.substring(portIndex + 1));
			}
		}

	}

}
