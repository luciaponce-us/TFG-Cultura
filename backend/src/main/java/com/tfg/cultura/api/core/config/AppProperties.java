package com.tfg.cultura.api.core.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.tfg.cultura.api.users.model.enumerators.Role;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        String frontendUrl,
        boolean seedEnabled,
        Jwt jwt,
        Cloudinary cloudinary,
        List<Role> adminRoles,
        DefaultImages defaultImages) {

    public AppProperties {
        adminRoles = List.copyOf(adminRoles);
    }

    public record Jwt(
            String secret,
            long expiration) {
    }

    public record Cloudinary(
            String cloudName,
            String apiKey,
            String apiSecret,
            boolean enabled) {
    }

    public record DefaultImages(
            String userAvatar,
            String movie,
            String series,
            String book,
            String boardGame,
            String rolGame) {
    }
}
