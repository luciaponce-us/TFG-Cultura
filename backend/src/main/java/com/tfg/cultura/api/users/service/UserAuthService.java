package com.tfg.cultura.api.users.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.*;
import com.tfg.cultura.api.users.exception.*;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.jwt.JwtService;

import java.util.Optional;

import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class UserAuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final UserFileService userFileService;
    private final UserService userService;

    private static final Logger logger = LoggerFactory.getLogger("usersLogger");

    public UserResponse register(UserRegisterRequest request, MultipartFile avatar, MultipartFile paymentReceipt)
            throws UserAlreadyExistsException, FileUploadException {
        userFileService.validateAvatar(avatar);
        userFileService.validatePaymentReceipt(paymentReceipt);

        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Error al registrar el usuario: El nombre de usuario ya existe");
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }

        if (userRepository.existsByDni(request.getDni())) {
            logger.warn("Error al registrar el usuario: El DNI ya existe");
            throw new UserAlreadyExistsException("Ya existe un usuario con el mismo DNI");
        }

        String avatarUrl = UserFileService.AVATAR_PLACEHOLDER;
        if (avatar != null && !avatar.isEmpty()) {
            logger.info("Se va a intentar subir el avatar del usuario {}", request.getUsername());
            avatarUrl = userFileService.uploadAvatar(request.getUsername(), avatar);
        }

        logger.info("Se va a intentar subir el PDF de la carta de pago del usuario {}",
                request.getUsername());
        String paymentReceiptUrl = userFileService.uploadPaymentReceiptPdf(request.getUsername(), paymentReceipt);
        logger.info("PDF subido a Cloudinary: {}", paymentReceiptUrl);

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .surname(request.getSurname())
                .dni(request.getDni())
                .phone(request.getPhone())
                .email(request.getEmail())
                .avatar(avatarUrl)
                .paymentReceipt(paymentReceiptUrl)
                .build();

        User savedUser = userRepository.save(user);

        logger.info("Usuario registrado correctamente: {}", savedUser.getUsername());
        return new UserResponse(savedUser);
    }

    public String login(UserLoginRequest request)
            throws UserNotFoundException, DisabledException, BadCredentialsException {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isEmpty()) {
            logger.warn("Error al iniciar sesión: El usuario no existe");
            throw new UserNotFoundException(
                    "El usuario con username " + request.getUsername() + " no existe");
        }
        User foundUser = user.get();
        if (!foundUser.isActive()) {
            logger.warn("Error al iniciar sesión: El usuario está desactivado");
            throw new DisabledException("El usuario está desactivado");
        }

        if (!passwordEncoder.matches(request.getPassword(), foundUser.getPassword())) {
            logger.warn("Error al iniciar sesión: El usuario introdujo una contraseña incorrecta para la cuenta {}",
                    foundUser.getUsername());
            throw new BadCredentialsException("Credenciales inválidas");
        }

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService
                .loadUserByUsername(request.getUsername());

        return jwtService.generateToken(
                userDetails.getUsername(),
                userDetails.getRole(),
                userDetails.getId());
    }

    public UserResponse activateUser(String id) throws UserNotFoundException {

        User user = userService.findUserById(id);
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
