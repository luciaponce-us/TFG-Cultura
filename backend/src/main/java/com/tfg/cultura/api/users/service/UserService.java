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
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;
import com.tfg.cultura.api.users.exception.RoleModificationNotAllowedException;
import com.tfg.cultura.api.users.exception.SelfActivationNotAllowedException;
import com.tfg.cultura.api.users.exception.UserAlreadyExistsException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.model.enumerators.Role;
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
    private final AppProperties appProperties;

    private static final Logger logger = LoggerFactory.getLogger("usersLogger");

    // HELPERS

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

    User getCurrentUser() throws UnathenticatedException, UserNotFoundException {
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        return findUserById(currentUser.getId());
    }

boolean isSameOrHigherRole(Role role1, Role role2) {
    if (role1 == role2) {
        return true;
    }

    switch (role1) {
        case COORDINADOR:
            return true;
        case SECRETARIO:
            return role2 == Role.ENCARGADO
                    || role2 == Role.COLABORADOR
                    || role2 == Role.SOCIO;
        case ENCARGADO:
            return role2 == Role.COLABORADOR
                    || role2 == Role.SOCIO;
        case COLABORADOR:
            return role2 == Role.SOCIO;
        default:
            return false;
    }
}

    private boolean isChanged(String newValue, String currentValue) {
        return newValue != null && !newValue.trim().equals(currentValue);
    }

    // GETTERS

    public Page<UserResponse> getAllUsers(int page, int size, Role role, Boolean active, String name) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<User> userPage;
        if (name != null || role != null || active != null) {
            userPage = userRepository.findAllWithFilters(role, active, name, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        return userPage.map(UserResponse::new);
    }

    public UserResponse getUser(String username) throws UserNotFoundException {
        User user = findUserByUsername(username);
        return new UserResponse(user);
    }

    public UserResponse getProfile() throws UserNotFoundException, UnathenticatedException {
        User currentUser = getCurrentUser();
        return new UserResponse(currentUser);
    }

    // UPDATE

    UserResponse updateUser(User user, UserUpdateRequest request, User currentUser) throws UserNotFoundException, UserAlreadyExistsException, UnathenticatedException, RoleModificationNotAllowedException, UnauthorizedException {
        
        boolean isAdmin = appProperties.adminRoles().contains(currentUser.getRole());

        boolean isSelfUpdate = currentUser.getId().equals(user.getId());
        if(isSameOrHigherRole(user.getRole(),currentUser.getRole()) && !isSelfUpdate) {
            logger.warn("El usuario {} con rol {} ha intentado actualizar un usuario con rol {}", currentUser.getUsername(), currentUser.getRole(), user.getRole());
            throw new UnauthorizedException("No tienes permisos para actualizar este usuario.");
        }

        logger.info("Se va a actualizar el usuario con username {}", user.getUsername());

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

        if (isAdmin) {
            if (isChanged(request.getDni(), user.getDni())) {
                if (userRepository.existsByDni(request.getDni()))
                    throw new UserAlreadyExistsException("El DNI ya está en uso");

                user.setDni(request.getDni());
            }

            boolean isRoleChanged = request.getRole() != null && request.getRole() != user.getRole();
            if (isRoleChanged) {
                boolean isSameRole = request.getRole() == currentUser.getRole();
                boolean isHigherRole = isSameOrHigherRole(request.getRole(), currentUser.getRole()) && !isSameRole;
                if(isSelfUpdate && isHigherRole) {
                    logger.warn("El usuario {} con rol {} ha intentado actualizar su propio rol a {}", currentUser.getUsername(), currentUser.getRole(), request.getRole());
                    throw new RoleModificationNotAllowedException("No puedes asignarte un rol superior al tuyo");
                }

                if(!isSelfUpdate && isSameOrHigherRole(request.getRole(), currentUser.getRole())) {
                    logger.warn("El usuario {} con rol {} ha intentado actualizar el rol de otro usuario a {}", currentUser.getUsername(), currentUser.getRole(), request.getRole());
                    throw new RoleModificationNotAllowedException("No puedes asignar un rol igual o superior al tuyo");
                }
                
                user.setRole(request.getRole());
            }
        }

        return saveUpdatedUser(user);
    }

    public UserResponse updateUser(String username, UserUpdateRequest request)
            throws UserNotFoundException, UserAlreadyExistsException, UnathenticatedException {
        User user = findUserByUsername(username);
        User currentUser = getCurrentUser();
        return updateUser(user, request, currentUser);
    }

    public UserResponse updateProfile(UserUpdateRequest request)
            throws UserNotFoundException, UserAlreadyExistsException, UnathenticatedException {
        User currentUser = getCurrentUser();
        return updateUser(currentUser, request, currentUser);
    }

    UserResponse saveUpdatedUser(User user) {
        if(user == null) {
            logger.warn("Error al guardar el usuario: El usuario es nulo");
            throw new IllegalArgumentException("El usuario no puede ser nulo");
        }
        User savedUser = userRepository.save(user);
        logger.info("Usuario con username {} actualizado correctamente", user.getUsername());
        return new UserResponse(savedUser);
    }

    UserResponse updateAvatar(User user, MultipartFile avatar) {
        String newAvatar = userFileService.uploadAvatar(user.getId(), avatar);
        logger.info("Nuevo avatar subido para el usuario con username {}: {}", user.getUsername(), newAvatar);
        user.setAvatar(newAvatar);
        UserResponse response = saveUpdatedUser(user);
        logger.info("Avatar del usuario con username {} actualizado correctamente", user.getUsername());
        return response;
    }

    public UserResponse updateUserAvatar(String username, MultipartFile avatar) throws UserNotFoundException {
        User user = findUserByUsername(username);
        return updateAvatar(user, avatar);
    }

    public UserResponse updateCurrentUserAvatar(MultipartFile avatar) throws UserNotFoundException {
        User user = getCurrentUser();
        return updateAvatar(user, avatar);
    }

    public UserResponse toggleUserActivation(String username) throws UserNotFoundException {
        User user = findUserByUsername(username);
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();

        if (currentUser == null) {
            throw new UnathenticatedException("No tienes permisos para eliminar usuarios");
        }

        if (user.getId().equals(currentUser.getId())) {
            throw new SelfActivationNotAllowedException(
                    String.format("El usuario %s con id %s ha intentado activar o desactivar su propio usuario", user.getUsername(),
                            user.getId()));
        }

        if (user.isActive()) {
            user.setActive(false);
        } else {
            user.setActive(true);
        }

        logger.info("Se ha cambiado el estado de activación del usuario {} con id {} a {}", user.getUsername(), user.getId(), user.isActive());

        return saveUpdatedUser(user);
    }

    // DELETE

    @Transactional
    void deleteUser(User user) {
        userFileService.deleteUserFile(user.getAvatar());
        userFileService.deleteUserFile(user.getPaymentReceipt());
        suggestionRepository.deleteByAuthorId(user.getId());
        userRepository.delete(user);
        logger.info("Usuario con username {} eliminado correctamente", user.getUsername());
    }

    @Transactional
    public void deleteUser(String username) throws UserNotFoundException {
        User user = findUserByUsername(username);
        deleteUser(user);
    }

    @Transactional
    public void deleteProfile() throws UserNotFoundException, UnathenticatedException {
        User user = getCurrentUser();
        deleteUser(user);
    }

}
