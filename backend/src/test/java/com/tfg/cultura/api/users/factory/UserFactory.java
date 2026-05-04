package com.tfg.cultura.api.users.factory;

import static org.mockito.Mockito.mock;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserLoginRequest;
import com.tfg.cultura.api.users.model.dto.UserProfileUpdateRequest;
import com.tfg.cultura.api.users.model.dto.UserRegisterRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.model.enumerators.Role;

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

    public static CustomUserDetails mockAuthContext() {
        User user = validUser();

        SecurityContext context = mock(SecurityContext.class);

        SecurityContextHolder.setContext(context);
        return new CustomUserDetails(user);
    }

    public static MockMultipartFile valid_avatar_file() {
        return new MockMultipartFile("avatar", "avatar.png", "image/png",
            "fake-image-content".getBytes());
    }

    public static MockMultipartFile valid_payment_receipt_file() {
        return new MockMultipartFile("paymentReceipt", "receipt.pdf", "application/pdf",
                "fake-pdf-content".getBytes());
    }

    public static UserUpdateRequest validUserUpdateRequest() {
        return UserUpdateRequest.builder()
                .name("Jane")
                .surname("Smith")
                .active(false)
                .dni("51835019B")
                .email("test2@test.com")
                .phone("987654321")
                .role(Role.COLABORADOR)
                .build();
    }

    public static UserProfileUpdateRequest validUserProfileUpdateRequest() {
        return UserProfileUpdateRequest.builder()
            .username("janesmith")
            .password("password123")
            .name("Jane")
            .surname("Smith")
            .email("test2@test.com")
            .phone("987654321")
            .build();
    }
}
