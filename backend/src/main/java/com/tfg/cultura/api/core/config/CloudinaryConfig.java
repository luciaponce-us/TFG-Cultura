package com.tfg.cultura.api.core.config;

import com.cloudinary.Cloudinary;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@ConditionalOnProperty(name = "cloudinary.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
@Profile("!test")
public class CloudinaryConfig {

	private final AppProperties appProperties;

	@Bean
	public Cloudinary cloudinary() {
		Map<String, String> config = new HashMap<>();
		config.put("cloud_name", appProperties.cloudinary().cloudName());
		config.put("api_key", appProperties.cloudinary().apiKey());
		config.put("api_secret", appProperties.cloudinary().apiSecret());
		return new Cloudinary(config);
	}
}
