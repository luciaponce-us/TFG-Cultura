package com.tfg.cultura.api.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import com.cloudinary.Cloudinary;

@TestConfiguration
public class MockConfig {

    @Bean
    public Cloudinary cloudinary() {
        return Mockito.mock(Cloudinary.class);
    }
}
