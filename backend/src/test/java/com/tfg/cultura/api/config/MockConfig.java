package com.tfg.cultura.api.config;

import static org.mockito.Mockito.mock;

import com.cloudinary.Cloudinary;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ActiveProfiles;

@TestConfiguration
@ActiveProfiles("test")
public class MockConfig {

	@Bean
	public Cloudinary cloudinary() {
		return mock(Cloudinary.class);
	}
}
