package demo.coapserver;

import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapRequestMapping;

@CoapController
public class Large {

	@CoapRequestMapping(path = "/large")
	public String large() {
		StringBuilder builder = new StringBuilder();
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 1 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 2 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 3 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 4 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		builder.append("/-------------------------------------------------------------\\\n");
		builder.append("|                 RESOURCE BLOCK NO. 5 OF 5                   |\n");
		builder.append("|               [each line contains 64 bytes]                 |\n");
		builder.append("\\-------------------------------------------------------------/\n");
		return builder.toString();
	}
}
