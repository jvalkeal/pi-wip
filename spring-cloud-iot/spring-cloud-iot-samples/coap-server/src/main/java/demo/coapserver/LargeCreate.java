package demo.coapserver;

import org.springframework.cloud.iot.coap.annotation.CoapController;
import org.springframework.cloud.iot.coap.annotation.CoapRequestMapping;
import org.springframework.cloud.iot.coap.annotation.CoapResponseBody;

@CoapController
public class LargeCreate {

	private int counter = 0;

	@CoapRequestMapping(path = "/large-create")
	@CoapResponseBody
	public String large() {
		StringBuilder builder = new StringBuilder();
		builder.append("hello");
		return builder.toString();
	}

}
