package com.giwootjang.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class GiwootJangApplicationTests {

	@Test
	void contextLoads() {
		String securityName = System.getenv("SECURITY_NAME");
		assertNotNull(securityName, "SECURITY_NAME should not be null");

		String rootPassword = System.getenv("MYSQL_ROOT_PASSWORD");
		assertNotNull(rootPassword, "MYSQL_ROOT_PASSWORD should not be null");

		// Additional assertions...
	}

}
