package com.tfg.cultura.api.users.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;
import com.tfg.cultura.api.users.exception.SelfActivationNotAllowedException;
import com.tfg.cultura.api.users.exception.UserAlreadyExistsException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserProfileUpdateRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService;
    private final SuggestionRepository suggestionRepository;
    private final UserFileService userFileService;

    private static final Logger logger = LoggerFactory.getLogger("usersLogger");

    public Page<UserResponse> getAllUsers(int page, int size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage = userRepository.findAll(pageable);

        return userPage.map(UserResponse::new);
    }

    public UserResponse getUser(String username) throws UserNotFoundException {
        User user = findUserByUsername(username);
        return new UserResponse(user);
    }

    User findUserByUsername(String username) throws UserNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);

        if (user.isEmpty()) {
            logger.warn("Error al obtener el usuario: El usuario no existe");
            throw new UserNotFoundException(String.format("El usuario con username %s no existe", username));
        }

        return user.get();
    }

    User findUserById(String id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            logger.warn("Error al obtener el usuario: El usuario no existe");
            throw new UserNotFoundException(String.format("El usuario con id %s no existe", id));
        }

        return user.get();
    }

    public UserResponse updateUser(String username, UserUpdateRequest request) throws UserNotFoundException, UserAlreadyExistsException {
        
        User user = updateProfile(username, toProfileUpdateRequest(request));

        if (isChanged(request.getDni(), user.getDni())) {
            if (userRepository.existsByDni(request.getDni()))
                throw new UserAlreadyExistsException("El DNI ya está en uso");

            user.setDni(request.getDni());
        }

        if (request.getRole() != null) {
            user.setRole(request.getRole());
        }

        user.setActive(request.isActive());

        return saveUpdatedUser(user);
    }

    private boolean isChanged(String newValue, String currentValue) {
        return newValue != null && !newValue.trim().equals(currentValue);
    }

    private UserProfileUpdateRequest toProfileUpdateRequest(UserUpdateRequest request) {
        return UserProfileUpdateRequest.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .name(request.getName())
                .surname(request.getSurname())
                .phone(request.getPhone())
                .email(request.getEmail())
                .build();
    }

    @Transactional
    public void deleteUser(String username) throws UserNotFoundException {
        User user = findUserByUsername(username);
        userFileService.deleteUserFiles(user.getAvatar(), user.getPaymentReceipt());
        suggestionRepository.deleteByAuthorId(user.getId());
        userRepository.delete(user);
        logger.info("Usuario con username {} eliminado correctamente", user.getUsername());
    }

    // PROFILE

    public UserResponse getProfile() throws UserNotFoundException {
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();

        return getUser(currentUser.getUsername());
    }

    public UserResponse updateProfile(UserProfileUpdateRequest request) throws UserNotFoundException, UserAlreadyExistsException {
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        User updatedUser = updateProfile(currentUser.getUsername(), request);

        return saveUpdatedUser(updatedUser);
    }

    public User updateProfile(String username, UserProfileUpdateRequest request) throws UserNotFoundException, UserAlreadyExistsException {
        logger.info("Se va a actualizar un usuario con username");
        User user = findUserByUsername(username);

        if (isChanged(request.getUsername(), user.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername()))
                throw new UserAlreadyExistsException("El username ya está en uso");

            user.setUsername(request.getUsername());
            logger.info("Se ha cambiado el username del usuario a {}", user.getUsername());
        }

        if (isChanged(request.getName(), user.getName())) {
            user.setName(request.getName());
        }

        if (isChanged(request.getSurname(), user.getSurname())) {
            user.setSurname(request.getSurname());
        }

        if (isChanged(request.getPhone(), user.getPhone())) {
            user.setPhone(request.getPhone());
        }

        if (isChanged(request.getEmail(), user.getEmail())) {
            user.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        return user;
    }

    public UserResponse saveUpdatedUser(User user){
        User savedUser = userRepository.save(user);
        logger.info("Usuario con username {} actualizado correctamente", user.getUsername());
        return new UserResponse(savedUser);
    }

    @Transactional
    public void deleteProfile() throws UserNotFoundException {
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        String username = currentUser.getUsername();
        User user = findUserByUsername(username);
        String userToDelete = user.getUsername();
        userFileService.deleteUserFiles(user.getAvatar(), user.getPaymentReceipt());
        suggestionRepository.deleteByAuthorId(user.getId());
        userRepository.delete(user);
        logger.info("Usuario con username {} eliminado correctamente", userToDelete);
    }

    public UserResponse activateUser(String id) throws UserNotFoundException {

        User user = findUserById(id);
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();

        if (currentUser == null) {
            throw new UnathenticatedException("No tienes permisos para eliminar usuarios");
        }
        if (user.getId().equals(currentUser.getId())) {
            throw new SelfActivationNotAllowedException(
                    String.format("El usuario %s con id %s ha intentado activar su propio usuario", user.getUsername(),
                            user.getId()));
        }

        if (!user.isActive()) {
            user.setActive(true);
            user = userRepository.save(user);
        }

        logger.info("Se ha aprobado el registro del usuario {} con id {}", user.getUsername(), user.getId());
        return new UserResponse(user);
    }
}
