package com.tfg.cultura.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.tfg.cultura.api.config.MockConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@Import(MockConfig.class)
@SpringBootTest
class ApiApplicationTests {

	@Autowired
	private ApplicationContext context;

	@Test
	void context_loads() {
		assertNotNull(context);
	}

}
