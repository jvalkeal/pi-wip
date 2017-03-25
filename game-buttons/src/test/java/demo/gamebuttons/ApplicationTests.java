package demo.gamebuttons;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.iot.test.junit.IotRule;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ApplicationTests {

	@Rule
	public IotRule iotRule = new IotRule();

	@Test
	public void contextLoads() {
	}

}
