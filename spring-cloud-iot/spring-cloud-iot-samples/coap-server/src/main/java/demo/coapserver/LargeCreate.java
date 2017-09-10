package demo.coapserver;

import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapRequestMapping;

@CoapController
public class LargeCreate {

	@CoapRequestMapping(path = "/large-create")
	public String large() {
		StringBuilder builder = new StringBuilder();
		builder.append("hello");
		return builder.toString();
	}

}
