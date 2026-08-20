package com.tfg.cultura.api.core.factory;

import java.util.List;

import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.users.model.enumerators.Role;

public class AppPropertiesFactory {

    public static AppProperties validAppPropertiesWithJwt(String jwtSecret, long jwtExpiration) {
        AppProperties.Jwt jwt = new AppProperties.Jwt(jwtSecret, jwtExpiration);
        return new AppProperties(
                "http://localhost:3000", // frontendUrl
                false, // seedEnabled
                jwt,
                cloudinary,
                List.of(Role.COORDINADOR),
                defaultImages);
    }

    public static AppProperties validAppProperties() {
        String jwtSecret = "mySuperSecretKeyThatIsLongEnoughForHS256Algorithm12345";

        return new AppProperties(
                "http://localhost:3000", // frontendUrl
                false, // seedEnabled
                new AppProperties.Jwt(jwtSecret, 3600), // jwt
                cloudinary, // cloudinary
                List.of(Role.COORDINADOR, Role.SECRETARIO, Role.ENCARGADO, Role.COLABORADOR), // adminRoles
                defaultImages // defaultImages
        );
    }

    private static AppProperties.Cloudinary cloudinary = new AppProperties.Cloudinary(
            "test-cloud",
            "test-key",
            "test-secret",
            false);

    private static AppProperties.DefaultImages defaultImages = new AppProperties.DefaultImages(
            "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png",
            "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/movie_placeholder.jpg",
            "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/series_placeholder.jpg",
            "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/book_placeholder.jpg",
            "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/boardgames_placeholder.jpg",
            "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/rolgames_placeholder.jpg",
            "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/rolsagas_placeholder.jpg");

}
