package com.tfg.cultura.api.users.factory;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserLoginRequest;
import com.tfg.cultura.api.users.model.dto.UserRegisterRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;

public class UserFactory {

    public static User validUser() {
        return User.builder()
                .id("user_id")
                .username("test")
                .password("12345678-encrypted")
                .name("John")
                .surname("Doe")
                .dni("12345678Z")
                .phone("600123123")
                .email("test@test.com")
                .paymentReceipt("test.pdf")
                .avatar("test.png")
                .active(true)
                .build();
    }

    public static UserResponse validUserResponse() {
        User user = validUser();
        return new UserResponse(user);
    }

    public static UserRegisterRequest validUserRegisterRequest() {
        User user = validUser();

        return UserRegisterRequest.builder()
                .username(user.getUsername())
                .password("12345678")
                .name(user.getName())
                .surname(user.getSurname())
                .dni(user.getDni())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

    public static UserLoginRequest loginRequest() {
        User user = validUser();
        return UserLoginRequest.builder()
                .username(user.getUsername())
                .password("12345678")
                .build();
    }

    public static void mockAuthContext() {
        User user = validUser();
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(new CustomUserDetails(user));

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);
    }

    public static MockMultipartFile valid_avatar_file() {
        return new MockMultipartFile("avatar", "avatar.png", "image/png",
            "fake-image-content".getBytes());
    }

    public static MockMultipartFile valid_payment_receipt_file() {
        return new MockMultipartFile("paymentReceipt", "receipt.pdf", "application/pdf",
                "fake-pdf-content".getBytes());
    }
}
