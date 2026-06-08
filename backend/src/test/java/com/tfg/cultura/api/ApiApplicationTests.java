package com.tfg.cultura.api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.tfg.cultura.api.config.MockConfig;

@ActiveProfiles("test")
@Import(MockConfig.class)
@SpringBootTest
class ApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
