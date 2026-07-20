package com.tfg.cultura.api.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;
import com.tfg.cultura.api.users.exception.RoleModificationNotAllowedException;
import com.tfg.cultura.api.users.exception.SelfActivationNotAllowedException;
import com.tfg.cultura.api.users.exception.UserAlreadyExistsException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.model.enumerators.Role;
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

    private AppProperties appProperties;

    private UserService service;

    private User user;
    private User currentUser;
    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;
    private CustomUserDetails userDetails;

    @BeforeEach
    void setUp() {
        user = UserFactory.validUser();

        userResponse = UserFactory.validUserResponse();
        updateRequest = UserFactory.validUserUpdateRequest();
        userDetails = new CustomUserDetails(user);
        appProperties = createAppProperties();
        service = new UserService(
                userRepository,
                passwordEncoder,
                userDetailsService,
                suggestionRepository,
                userFileService,
                appProperties);

    }

    private void mockAuthContext(boolean isAdmin) {
        CustomUserDetails currentUserDetails = isAdmin ? UserFactory.mockAuthContextAdmin()
                : UserFactory.mockAuthContext();
        when(userDetailsService.getCurrentUserDetails()).thenReturn(currentUserDetails);
    }

    private void mockCurrentUser(boolean isAdmin) {
        if (isAdmin) {
            currentUser = UserFactory.validCurrentUserWithRole(Role.COORDINADOR);
        } else {
            currentUser = UserFactory.validCurrentUserWithRole(Role.SOCIO);
        }
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(currentUser));
    }

    private AppProperties createAppProperties() {
        AppProperties.Jwt jwt = new AppProperties.Jwt("test-secret", 3600);
        AppProperties.Cloudinary cloudinary = new AppProperties.Cloudinary(
                "test-cloud",
                "test-key",
                "test-secret",
                false);
        return new AppProperties(
                "http://localhost:3000", // frontendUrl
                false, // seedEnabled
                jwt,
                cloudinary,
                List.of(Role.COORDINADOR, Role.SECRETARIO, Role.ENCARGADO, Role.COLABORADOR) // adminRoles
        );
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

    // GET CURRENT USER

    @Test
    void should_return_current_user_successfully() throws Exception {
        mockAuthContext(false);
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.of(user));

        User result = service.getCurrentUser();

        assertEquals(user, result);
        verify(userRepository).findById(currentUser.getId());
    }

    @Test
    void should_throw_exception_when_user_not_found_in_get_current_user() {
        mockAuthContext(false);

        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class,
                () -> service.getCurrentUser());
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

    void mockSaveUser() {
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void should_update_user_successfully() {
        mockAuthContext(false);
        mockSaveUser();
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        UserResponse response = service.updateUser(user.getUsername(), updateRequest);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getSurname(), response.getSurname());
    }

    @Test
    void should_update_user_username_successfully() {
        mockAuthContext(false);
        mockSaveUser();

        String newUsername = "newUsername";
        updateRequest.setUsername(newUsername);

        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(newUsername)).thenReturn(false);

        UserResponse response = service.updateUser(user.getUsername(), updateRequest);

        assertNotNull(response);
        assertEquals(newUsername, response.getUsername());
    }

    @Test
    void should_update_user_password_succesfully() {
        mockAuthContext(false);
        mockSaveUser();

        String username = user.getUsername();
        String oldEmail = user.getEmail();

        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newPassword");

        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.encode(any()))
                .thenReturn("encodedNewPassword");

        // WHEN
        UserResponse response = service.updateUser(username, request);

        // THEN
        assertEquals("encodedNewPassword", user.getPassword());
        assertEquals(oldEmail, response.getEmail()); // no cambia

        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(user);

    }

    @Test
    void should_update_user_dni_and_role_successfully_when_admin() {
        mockAuthContext(true);
        mockCurrentUser(true);
        mockSaveUser();

        String newDni = "12345678A";
        Role newRole = Role.COORDINADOR;
        updateRequest.setDni(newDni);
        updateRequest.setRole(newRole);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByDni(newDni)).thenReturn(false);

        UserResponse response = service.updateUser(user.getUsername(), updateRequest);
        assertNotNull(response);
        assertEquals(newDni, response.getDni());
        assertEquals(newRole, response.getRole());
    }

    @Test
    void should_not_update_user_dni_and_role_when_not_admin() {
        mockAuthContext(false);
        mockSaveUser();

        String originalDni = user.getDni();
        Role originalRole = user.getRole();
        updateRequest.setDni("12345678A");
        updateRequest.setRole(Role.COORDINADOR);

        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        UserResponse response = service.updateUser(user.getUsername(), updateRequest);
        assertNotNull(response);
        assertEquals(originalDni, response.getDni());
        assertEquals(originalRole, response.getRole());
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
        mockAuthContext(false);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));
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
        mockAuthContext(true);
        mockCurrentUser(true);

        String username = user.getUsername();
        String existingDni = "06323988T";
        updateRequest.setDni(existingDni);
        assertNotEquals(existingDni, user.getDni());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(userRepository.existsByDni(existingDni)).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> service.updateUser(username, updateRequest));

        assertTrue(ex.getMessage().contains("ya está en uso"));
    }

    @Test
    void should_throw_UnauthorizedException_when_update_user_with_higher_role() {
        User currentUser = UserFactory.validUser2();
        currentUser.setRole(Role.SOCIO); // Rol inferior al del usuario a actualizar
        updateRequest.setRole(Role.COORDINADOR); // Rol superior al del usuario actual

        UnauthorizedException ex = assertThrows(UnauthorizedException.class,
                () -> service.updateUser(user, updateRequest, currentUser));

        assertTrue(ex.getMessage().contains("No tienes permisos"));
    }

    @Test
    void saveUpdatedUser_when_user_is_null_should_throw_illegal_argument_exception() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.saveUpdatedUser(null));

        assertEquals("El usuario no puede ser nulo", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    // UPDATE ROLES

    @Test
    void should_update_other_user_to_inferior_role() throws Exception {
        mockSaveUser();
        User currentUser = UserFactory.validUser();
        currentUser.setId("1");
        currentUser.setRole(Role.SECRETARIO);

        User user = UserFactory.validUser2();
        user.setId("2");
        user.setRole(Role.COLABORADOR);

        UserUpdateRequest request = UserFactory.validUserUpdateRequest();
        request.setRole(Role.SOCIO);

        service.updateUser(user, request, currentUser);

        assertEquals(Role.SOCIO, user.getRole());
    }

    @Test
    void should_throw_when_self_assigning_higher_role() {
        User currentUser = UserFactory.validUser();
        currentUser.setId("1");
        currentUser.setRole(Role.SECRETARIO);

        User user = UserFactory.validUser();
        user.setId("1");
        user.setRole(Role.SECRETARIO);

        UserUpdateRequest request = UserFactory.validUserUpdateRequest();
        request.setRole(Role.COORDINADOR);

        assertThrows(RoleModificationNotAllowedException.class,
                () -> service.updateUser(user, request, currentUser));

        assertEquals(Role.SECRETARIO, user.getRole());
    }

    @Test
    void should_allow_self_downgrade() throws Exception {
        mockSaveUser();
        User currentUser = UserFactory.validUser();
        currentUser.setId("1");
        currentUser.setRole(Role.SECRETARIO);

        User user = UserFactory.validUser();
        user.setId("1");
        user.setRole(Role.SECRETARIO);

        UserUpdateRequest request = UserFactory.validUserUpdateRequest();
        request.setRole(Role.ENCARGADO);

        service.updateUser(user, request, currentUser);

        assertEquals(Role.ENCARGADO, user.getRole());
    }

    @Test
    void should_throw_when_assigning_same_role_to_other_user() {
        User currentUser = UserFactory.validUser();
        currentUser.setId("1");
        currentUser.setRole(Role.SECRETARIO);

        User user = UserFactory.validUser2();
        user.setId("2");
        user.setRole(Role.COLABORADOR);

        UserUpdateRequest request = UserFactory.validUserUpdateRequest();
        request.setRole(Role.SECRETARIO);

        assertThrows(RoleModificationNotAllowedException.class,
                () -> service.updateUser(user, request, currentUser));

        assertEquals(Role.COLABORADOR, user.getRole());
    }

    @Test
    void should_throw_when_assigning_higher_role_to_other_user() {
        User currentUser = UserFactory.validUser();
        currentUser.setId("1");
        currentUser.setRole(Role.SECRETARIO);

        User user = UserFactory.validUser2();
        user.setId("2");
        user.setRole(Role.COLABORADOR);

        UserUpdateRequest request = UserFactory.validUserUpdateRequest();
        request.setRole(Role.COORDINADOR);

        assertThrows(RoleModificationNotAllowedException.class,
                () -> service.updateUser(user, request, currentUser));

        assertEquals(Role.COLABORADOR, user.getRole());
    }

    // DELETE USER

    @Test
    void should_delete_user_successfully() {
        mockAuthContext(true);
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

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

    @Test
    void deleteUser_when_target_user_has_same_or_higher_role_and_is_not_self_should_throw_unauthorized_exception()
            throws Exception {

        mockAuthContext(true);
        mockCurrentUser(true);

        currentUser.setRole(Role.SECRETARIO);

        user.setId("otherUserId"); // No es el mismo usuario
        user.setRole(Role.COORDINADOR); // Rol superior

        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> service.deleteUser(user.getUsername()));

        assertEquals("No tienes permisos para eliminar este usuario.", exception.getMessage());

        verify(userRepository, never()).delete(any());
    }

    // =================== PROFILE ===================

    // GET USER PROFILE

    @Test
    void should_return_user_profile_when_authenticated() throws Exception {
        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findById(anyString()))
                .thenReturn(Optional.of(user));

        UserResponse response = service.getProfile();

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
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

        UserUpdateRequest request = new UserUpdateRequest();

        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findById(anyString()))
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

        when(userRepository.findById(anyString()))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            service.updateProfile(updateRequest);
        });

        verify(userRepository, never()).save(any());
    }

    @Test
    void should_throw_when_user_already_exists() {
        updateRequest.setUsername("newUsername");
        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findById(anyString()))
                .thenReturn(Optional.of(user));

        // simula conflicto
        when(userRepository.existsByUsername(anyString()))
                .thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            service.updateProfile(updateRequest);
        });
    }

    // DELETE USER PROFILE

    @Test
    void should_delete_user_profile_successfully() throws Exception {
        user.setAvatar("avatar_url");
        user.setPaymentReceipt("receipt_url");

        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findById(anyString()))
                .thenReturn(Optional.of(user));

        service.deleteProfile();

        verify(userFileService).deleteUserFile("avatar_url");
        verify(userFileService).deleteUserFile("receipt_url");
        verify(suggestionRepository).deleteByAuthorId(user.getId());
        verify(userRepository).delete(user);
    }

    @Test
    void should_throw_when_user_not_found_on_delete() {
        when(userDetailsService.getCurrentUserDetails())
                .thenReturn(userDetails);

        when(userRepository.findById(anyString()))
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
                Sort.by("createdAt").descending());

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

    @Test
    void should_use_filters_when_any_filter_is_provided() {
        PageRequest pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        User user2 = UserFactory.validUser();
        user2.setId("2");
        user2.setUsername("otherUser");

        when(userRepository.findAllWithFilters(Role.COLABORADOR, true, "Ana", pageable))
                .thenReturn(new PageImpl<>(List.of(user, user2), pageable, 2));

        Page<UserResponse> result = service.getAllUsers(0, 10, Role.COLABORADOR, true, "Ana");

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        verify(userRepository).findAllWithFilters(Role.COLABORADOR, true, "Ana", pageable);
        verify(userRepository, never()).findAll(any(PageRequest.class));
    }

    // UPDATE USER AVATAR

    @Test
    void should_update_user_avatar_successfully() {
        MockMultipartFile avatar = new MockMultipartFile(
                "avatar",
                "avatar.png",
                "image/png",
                "image-content".getBytes());
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
                "image-content".getBytes());

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.updateUserAvatar("unknown", avatar));

        assertTrue(ex.getMessage().contains("no existe"));
        verifyNoInteractions(userFileService);
        verify(userRepository, never()).save(any());
    }

    // UPDATE CURRENT USER AVATAR

    @Test
    void should_update_avatar_successfully() throws Exception {
        // Arrange
        MultipartFile avatar = mock(MultipartFile.class);
        mockAuthContext(false);
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        UserResponse expectedResponse = userResponse;

        // Espiamos el service para mockear updateAvatar (método interno)
        UserService spyService = spy(service);
        doReturn(expectedResponse).when(spyService).updateAvatar(user, avatar);

        // Act
        UserResponse result = spyService.updateCurrentUserAvatar(avatar);

        // Assert
        assertEquals(expectedResponse, result);
        verify(spyService).updateCurrentUserAvatar(avatar);
    }

    @Test
    void should_throw_exception_when_user_not_found_in_update_avatar() {
        // Arrange
        MultipartFile avatar = mock(MultipartFile.class);
        mockAuthContext(false);

        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        when(userRepository.findById(currentUser.getId())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class,
                () -> service.updateCurrentUserAvatar(avatar));
    }

    // TOGGLE USER ACTIVATION

    @Test
    void should_return_user_response_when_toggle_activation_with_active_user() {
        mockAuthContext(true);
        mockSaveUser();
        User currentUser = UserFactory.validUser();
        currentUser.setId("currentUserId");
        currentUser.setRole(Role.COORDINADOR); // Usuario con rol superior
        user.setActive(true); // Usuario activo
        user.setId("otherId"); // Usuario distinto a sí mismo
        user.setRole(Role.SOCIO); // Usuario con rol inferior

        assertEquals(Role.COORDINADOR, currentUser.getRole());
        assertEquals(Role.SOCIO, user.getRole());
        when(userRepository.findById("currentUserId")).thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserResponse response = service.toggleUserActivation("testUser");

        assertNotNull(response);
        assertTrue(!response.isActive()); // Verifica que el usuario ahora está inactivo
    }

    @Test
    void should_return_user_response_when_toggle_activation_with_inactive_user() {
        mockAuthContext(true);
        mockCurrentUser(true);
        user.setActive(false); // Usuario inactivo
        user.setId("otherId"); // Usuario distinto a sí mismo

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        // Simula que el repositorio persiste el usuario retornando la entidad guardada
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = service.toggleUserActivation("testUser");

        assertNotNull(response);
        assertTrue(response.isActive()); // Verifica que el usuario ahora está activo
    }

    @Test
    void should_throw_exception_when_toggle_activation_unexisting_user() {
        mockAuthContext(true);

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.toggleUserActivation("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void should_throw_exception_when_user_toggles_activation_himself() {
        mockAuthContext(false);
        mockCurrentUser(false);
        currentUser.setActive(true);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(currentUser));

        assertThrows(SelfActivationNotAllowedException.class, () -> {
            service.toggleUserActivation("currentUserId");
        });
    }

    @Test
    void should_throw_exception_when_toggling_activation_unathenticated() {
        when(userDetailsService.getCurrentUserDetails())
        .thenThrow(new UnathenticatedException("No se ha podido obtener la autenticación del usuario"));

        UnathenticatedException ex = assertThrows(UnathenticatedException.class, () -> {
            service.toggleUserActivation("123");
        });

        assertTrue(ex.getMessage().contains("autenticación"));
    }

    @Test
    void should_throw_exception_when_toggling_activation_and_no_user_details() {
        when(userDetailsService.getCurrentUserDetails())
        .thenThrow(new UnathenticatedException("No se ha podido obtener la autenticación del usuario"));

        SecurityContext context = mock(SecurityContext.class);
        SecurityContextHolder.setContext(context);

        UnathenticatedException ex = assertThrows(UnathenticatedException.class, () -> {
            service.toggleUserActivation("123");
        });

        assertTrue(ex.getMessage().contains("autenticación"));
    }

    @Test
    void toggleUserActivation_when_target_user_has_same_or_higher_role_should_throw_unauthorized_exception()
            throws Exception {

        mockAuthContext(true);
        mockCurrentUser(true);

        user.setId("otherId");
        user.setRole(Role.COORDINADOR);

        when(userRepository.findByUsername(currentUser.getUsername()))
                .thenReturn(Optional.of(currentUser));
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        UnauthorizedException exception = assertThrows(
                UnauthorizedException.class,
                () -> service.toggleUserActivation(user.getUsername()));

        assertEquals("No tienes permisos para actualizar este usuario.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    // IS SAME OR HIGHER ROLE

    @ParameterizedTest
    @MethodSource("sameOrHigherRoleProvider")
    void isSameOrHigherRole_should_return_expected_result(Role role1, Role role2, boolean expected) {
        assertEquals(expected, service.isSameOrHigherRole(role1, role2));
    }

    private static Stream<Arguments> sameOrHigherRoleProvider() {
        return Stream.of(
                // Same role
                Arguments.of(Role.COORDINADOR, Role.COORDINADOR, true),
                Arguments.of(Role.SECRETARIO, Role.SECRETARIO, true),
                Arguments.of(Role.ENCARGADO, Role.ENCARGADO, true),
                Arguments.of(Role.COLABORADOR, Role.COLABORADOR, true),
                Arguments.of(Role.SOCIO, Role.SOCIO, true),

                // Higher role
                Arguments.of(Role.COORDINADOR, Role.SECRETARIO, true),
                Arguments.of(Role.COORDINADOR, Role.ENCARGADO, true),
                Arguments.of(Role.COORDINADOR, Role.COLABORADOR, true),
                Arguments.of(Role.COORDINADOR, Role.SOCIO, true),

                Arguments.of(Role.SECRETARIO, Role.ENCARGADO, true),
                Arguments.of(Role.SECRETARIO, Role.COLABORADOR, true),
                Arguments.of(Role.SECRETARIO, Role.SOCIO, true),

                Arguments.of(Role.ENCARGADO, Role.COLABORADOR, true),
                Arguments.of(Role.ENCARGADO, Role.SOCIO, true),

                Arguments.of(Role.COLABORADOR, Role.SOCIO, true),

                // Lower role
                Arguments.of(Role.SECRETARIO, Role.COORDINADOR, false),
                Arguments.of(Role.ENCARGADO, Role.COORDINADOR, false),
                Arguments.of(Role.ENCARGADO, Role.SECRETARIO, false),
                Arguments.of(Role.COLABORADOR, Role.COORDINADOR, false),
                Arguments.of(Role.COLABORADOR, Role.SECRETARIO, false),
                Arguments.of(Role.COLABORADOR, Role.ENCARGADO, false),
                Arguments.of(Role.SOCIO, Role.COORDINADOR, false),
                Arguments.of(Role.SOCIO, Role.SECRETARIO, false),
                Arguments.of(Role.SOCIO, Role.ENCARGADO, false),
                Arguments.of(Role.SOCIO, Role.COLABORADOR, false));
    }

}
