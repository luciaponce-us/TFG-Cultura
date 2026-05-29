package com.tfg.cultura.api.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;
import com.tfg.cultura.api.users.exception.SelfActivationNotAllowedException;
import com.tfg.cultura.api.users.exception.UserAlreadyExistsException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserProfileUpdateRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SuggestionRepository suggestionRepository;

    @Mock
    private UserFileService userFileService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private UserService service;

    private User user;
    private UserUpdateRequest updateRequest;
    private CustomUserDetails userDetails;
    private UserProfileUpdateRequest userProfileUpdateRequest;

    @BeforeEach
    void setUp() {
        user = UserFactory.validUser();
        updateRequest = UserFactory.validUserUpdateRequest();
        userDetails = new CustomUserDetails(user);
        userProfileUpdateRequest = new UserProfileUpdateRequest();
    }

    private void mockAuthContext() {
        CustomUserDetails currentUser = UserFactory.mockAuthContext();
        when(userDetailsService.getCurrentUserDetails()).thenReturn(currentUser);
    }

    // GET USER

    @Test
    void should_return_user_response_when_get_existing_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserResponse response = service.getUser(user.getUsername());

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void should_throw_exception_when_get_unexisting_user() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.getUser("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    // FIND USER BY ID

    @Test
    void should_return_user_when_find_user_by_id_with_existing_user() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        User foundUser = service.findUserById(user.getId());

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void should_throw_UserNotFoundException_when_find_user_by_id_with_unexisting_user() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.findUserById("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    // UPDATE USER

    @Test
    void should_update_user_successfully() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse response = service.updateUser(user.getUsername(), updateRequest);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getSurname(), response.getSurname());
    }

    @Test
    void should_update_user_username_successfully() {
        String newUsername = "newUsername";
        updateRequest.setUsername(newUsername);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(newUsername)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = service.updateUser(user.getUsername(), updateRequest);

        assertNotNull(response);
        assertEquals(newUsername, response.getUsername());
    }

    @Test
    void should_update_user_password_succesfully() {
        String username = user.getUsername();
        String oldEmail = user.getEmail();

        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newPassword");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(any()))
                .thenReturn("encodedNewPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        UserResponse response = service.updateUser(username, request);

        // THEN
        assertEquals("encodedNewPassword", user.getPassword());
        assertEquals(oldEmail, response.getEmail()); // no cambia

        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(user);

    }

    @Test
    void should_throw_UserNotFoundException_when_update_unexisting_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.updateUser("123", updateRequest));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void should_throw_UserAlreadyExistsException_when_update_user_with_existing_username() {
        String username = user.getUsername();
        String existingUsername = "existingUsername";
        updateRequest.setUsername(existingUsername);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> service.updateUser(username, updateRequest));

        assertTrue(ex.getMessage().contains("ya está en uso"));
    }

    @Test
    void should_throw_UserAlreadyExistsException_when_update_user_with_existing_dni() {
        String username = user.getUsername();
        String existingDni = "06323988T";
        updateRequest.setDni(existingDni);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByDni(existingDni)).thenReturn(true);
        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> service.updateUser(username, updateRequest));

        assertTrue(ex.getMessage().contains("ya está en uso"));
    }

    // DELETE USER

    @Test
    void should_delete_user_successfully() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        service.deleteUser(user.getUsername());

        verify(userRepository).delete(user);
    }

    @Test
    void should_throw_UserNotFoundException_when_delete_unexisting_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.deleteUser("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    // =================== PROFILE ===================

    // GET USER PROFILE

    @Test
    void should_return_user_profile_when_authenticated() throws Exception {
        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        UserResponse response = service.getProfile();

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void should_throw_exception_when_user_not_found() {
        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findByUsername(anyString()))
                .thenThrow(new UserNotFoundException("User not found"));

        assertThrows(UserNotFoundException.class, () -> {
            service.getProfile();
        });
    }

    @Test
    void should_throw_exception_when_no_authenticated_user() {

        when(userDetailsService.getCurrentUserDetails())
                .thenThrow(new UnathenticatedException("No auth"));

        assertThrows(UnathenticatedException.class, () -> {
            service.getProfile();
        });

        verify(userDetailsService).getCurrentUserDetails();
    }

    // UPDATE USER PROFILE

    @Test
    void should_update_profile_successfully() throws Exception {

        UserProfileUpdateRequest request = new UserProfileUpdateRequest();

        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = service.updateProfile(request);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());

        verify(userDetailsService).getCurrentUserDetails();
        verify(userRepository).save(any(User.class));
    }

    @Test
    void should_throw_when_user_not_found_on_update() {
        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            service.updateProfile(userProfileUpdateRequest);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void should_throw_when_user_already_exists() {
        userProfileUpdateRequest.setUsername("newUsername");
        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        // simula conflicto
        when(userRepository.existsByUsername(anyString()))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            service.updateProfile(userProfileUpdateRequest);
        });
    }

    // DELETE USER PROFILE

    @Test
    void should_delete_user_profile_successfully() throws Exception {
        user.setAvatar("avatar_url");
        user.setPaymentReceipt("receipt_url");

        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.of(user));

        service.deleteProfile();

        verify(userFileService).deleteUserFiles("avatar_url", "receipt_url");
        verify(suggestionRepository).deleteByAuthorId(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void should_throw_when_user_not_found_on_delete() {
        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            service.deleteProfile();
        });

        verifyNoInteractions(userFileService);
        verify(userRepository, never()).delete(any());
    }

    // GET ALL USERS

    @Test
    void should_return_paginated_user_response_list() {
        User user2 = UserFactory.validUser();
        user2.setId("2");
        user2.setUsername("otherUser");

        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user, user2)));

        Page<UserResponse> result = service.getAllUsers(0, 10, null, null, null);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());

        assertEquals(user.getUsername(), result.getContent().get(0).getUsername());
        assertEquals(user2.getUsername(), result.getContent().get(1).getUsername());

        verify(userRepository).findAll(any(PageRequest.class));
    }

    @Test
    void should_call_repository_with_correct_sorting() {
        User user2 = UserFactory.validUser();
        user2.setId("2");
        user2.setUsername("otherUser");

        PageRequest pageable = PageRequest.of(
            1,
            5,
            Sort.by("createdAt").descending()
        );
        
        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(user, user2), pageable, 2));

        Page<UserResponse> result = service.getAllUsers(1, 5, null, null, null);

        assertEquals(1, result.getPageable().getPageNumber());
        assertEquals(5, result.getPageable().getPageSize());
        assertTrue(result.getPageable().getSort().isSorted());
        assertTrue(result.getPageable().getSort().getOrderFor("createdAt").getDirection().isDescending());

        verify(userRepository).findAll(any(PageRequest.class));
    }

    @Test
    void should_return_empty_page_when_no_users_exist() {

        when(userRepository.findAll(any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<UserResponse> result = service.getAllUsers(0, 10, null, null, null);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // UPDATE USER AVATAR

    @Test
    void should_update_user_avatar_successfully() {
        MockMultipartFile avatar = new MockMultipartFile(
                "avatar",
                "avatar.png",
                "image/png",
                "image-content".getBytes()
        );
        String newAvatarUrl = "https://cdn.example.com/avatar.png";

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userFileService.uploadAvatar(user.getId(), avatar)).thenReturn(newAvatarUrl);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = service.updateUserAvatar(user.getUsername(), avatar);

        assertNotNull(response);
        assertEquals(newAvatarUrl, response.getAvatar());
        verify(userFileService).uploadAvatar(user.getId(), avatar);
        verify(userRepository).save(user);
    }

    @Test
    void should_throw_UserNotFoundException_when_update_user_avatar_unexisting_user() {
        MockMultipartFile avatar = new MockMultipartFile(
                "avatar",
                "avatar.png",
                "image/png",
                "image-content".getBytes()
        );

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.updateUserAvatar("unknown", avatar));

        assertTrue(ex.getMessage().contains("no existe"));
        verifyNoInteractions(userFileService);
        verify(userRepository, never()).save(any());
    }

    // DEACTIVATE USER

    @Test
    void should_return_user_response_when_deactivate_successfully() {
        mockAuthContext();
        user.setActive(true);
        user.setId("otherId"); // Usuario distinto a sí mismo

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = service.deactivateUser("testUser");

        assertNotNull(response);
        assertTrue(!response.isActive());
    }

    @Test
    void should_return_user_response_when_deactivate_already_inactive_user() {
        mockAuthContext();
        user.setActive(false);
        user.setId("otherId"); // Usuario distinto a sí mismo

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserResponse response = service.deactivateUser("testUser");

        assertNotNull(response);
        assertTrue(!response.isActive());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void should_throw_exception_when_deactivate_unexisting_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.deactivateUser("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void should_throw_exception_when_user_deactivates_himself() {
        mockAuthContext();
        user.setActive(true);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        assertThrows(SelfActivationNotAllowedException.class, () -> {
            service.deactivateUser("123");
        });
    }

    @Test
    void should_throw_exception_when_deactivating_user_unathenticated() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UnathenticatedException ex = assertThrows(UnathenticatedException.class, () -> {
            service.deactivateUser("123");
        });

        assertTrue(ex.getMessage().contains("permiso"));
    }

    @Test
    void should_throw_exception_when_deactivating_user_and_no_user_details() {
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UnathenticatedException ex = assertThrows(UnathenticatedException.class, () -> {
            service.deactivateUser("123");
        });

        assertTrue(ex.getMessage().contains("permiso"));
    }

       // ACTIVATE USER

    @Test
    void should_return_user_response_when_activate_successfully() {
        mockAuthContext();
        user.setActive(false);
        user.setId("otherId"); // Usuario distinto a sí mismo
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = service.activateUser("testUser");

        assertNotNull(response);
        assertTrue(response.isActive());
    }

    @Test
    void should_return_user_response_when_activate_already_active_user() {
        mockAuthContext();
        user.setActive(true);
        user.setId("otherId"); // Usuario distinto a sí mismo
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserResponse response = service.activateUser("testUser");
        assertNotNull(response);
        assertTrue(response.isActive());
    }

    @Test
    void should_throw_exception_when_activate_unexisting_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.activateUser("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void should_throw_exception_when_user_activates_himself() {
        mockAuthContext();
        user.setActive(false);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        assertThrows(SelfActivationNotAllowedException.class, () -> {
            service.activateUser("123");
        });
    }

    @Test
    void should_throw_exception_when_activating_user_unathenticated() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        UnathenticatedException ex = assertThrows(UnathenticatedException.class, () -> {
            service.activateUser("123");
        });
        
        assertTrue(ex.getMessage().contains("permiso"));
    }

    @Test
    void should_throw_exception_when_activating_user_and_no_user_details() {
        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UnathenticatedException ex = assertThrows(UnathenticatedException.class, () -> {
            service.activateUser("123");
        });

        assertTrue(ex.getMessage().contains("permiso"));
    }

}
