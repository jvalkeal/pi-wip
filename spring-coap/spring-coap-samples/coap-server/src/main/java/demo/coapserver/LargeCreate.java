package demo.coapserver;

import org.springframework.coap.annotation.CoapController;
import org.springframework.coap.annotation.CoapRequestMapping;
import org.springframework.coap.annotation.CoapResponseBody;

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
