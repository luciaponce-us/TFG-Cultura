package com.tfg.cultura.api.users.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.users.repository.UserRepository;

import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.*;
import com.cloudinary.Transformation;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.users.exception.*;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.jwt.JwtService;

import java.util.Optional;

import com.tfg.cultura.api.core.exception.FileUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final FileService fileService;

    public static final String AVATAR_PLACEHOLDER = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png";

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
            CustomUserDetailsService userDetailsService, FileService fileService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
        this.fileService = fileService;
    }

    private static final Logger logger = LoggerFactory.getLogger("usersLogger");

    public UserResponse register(UserRegisterRequest request, MultipartFile avatar, MultipartFile paymentReceipt) throws UserAlreadyExistsException, FileUploadException {
        validateAvatar(avatar);
        validatePaymentReceipt(paymentReceipt);
        
        if (userRepository.existsByUsername(request.getUsername())) {
            logger.warn("Error al registrar el usuario {}: El nombre de usuario ya existe", request.getUsername());
            throw new UserAlreadyExistsException("El nombre de usuario ya existe");
        }

        if (userRepository.existsByDni(request.getDni())) {
            logger.warn("Error al registrar el usuario {}: El DNI {} ya existe", request.getUsername(),
                    request.getDni());
            throw new UserAlreadyExistsException("Ya existe un usuario con el mismo DNI");
        }

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .surname(request.getSurname())
                .dni(request.getDni())
                .phone(request.getPhone())
                .email(request.getEmail())
                .avatar(AVATAR_PLACEHOLDER)
                .build();

        if (avatar != null && !avatar.isEmpty()) {
            logger.info("Se va a intentar subir el avatar: {}", avatar.getOriginalFilename());
            String avatarUrl = uploadAvatar(request.getUsername(), avatar);
            user.setAvatar(avatarUrl);
        }

        logger.info("Se va a intentar subir el PDF de la carta de pago: {}",
                paymentReceipt.getOriginalFilename());
        String paymentReceiptUrl = uploadPaymentReceiptPdf(request.getUsername(), paymentReceipt);
        user.setPaymentReceipt(paymentReceiptUrl);

        User savedUser = userRepository.save(user);
        logger.info("Usuario registrado correctamente: {}", savedUser.getUsername());
        return new UserResponse(savedUser);
    }

    public String login(UserLoginRequest request)
            throws UserNotFoundException, DisabledException, BadCredentialsException {
        Optional<User> user = userRepository.findByUsername(request.getUsername());
        if (user.isEmpty()) {
            logger.warn("Error al iniciar sesión: El usuario {} no existe", request.getUsername());
            throw new UserNotFoundException(
                    "El usuario con username " + request.getUsername() + " no existe");
        }
        User foundUser = user.get();
        if (!foundUser.isActive()) {
            logger.warn("Error al iniciar sesión: El usuario {} está desactivado", request.getUsername());
            throw new DisabledException("El usuario está desactivado");
        }

        if (!passwordEncoder.matches(request.getPassword(), foundUser.getPassword())) {
            logger.warn("Error al iniciar sesión: El usuario {} introdujo una contraseña incorrecta",
                    request.getUsername());
            throw new BadCredentialsException("Credenciales inválidas");
        }

        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService
                .loadUserByUsername(request.getUsername());

        return jwtService.generateToken(
                userDetails.getUsername(),
                userDetails.getRole(),
                userDetails.getId());
    }

    public String uploadAvatar(String userId, MultipartFile file) {
        try {
            FileUploadRequest request = FileUploadRequest.builder()
                    .file(file)
                    .folder("cultura/avatars")
                    .publicId("user_" + userId)
                    .transformation(new Transformation()
                            .width(300)
                            .height(300)
                            .crop("fill"))
                    .build();

            return fileService.uploadFile(request);
        } catch (Exception ex) {
            logger.error("No se ha podido subir el avatar {} para el usuario con id {}: {}", file.getOriginalFilename(),
                    userId, ex.getMessage());
            return AVATAR_PLACEHOLDER;
        }

    }

    public String uploadPaymentReceiptPdf(String userId, MultipartFile file) throws FileUploadException {
        try {
            FileUploadRequest request = FileUploadRequest.builder()
                    .file(file)
                    .folder("cultura/payment_receipts")
                    .publicId("payment_" + userId)
                    .build();

            return fileService.uploadFile(request);
        } catch (Exception ex) {
            logger.error(
                    "No se ha podido subir el PDF {} para el usuario con id {}: {}",
                    file.getOriginalFilename(),
                    userId,
                    ex.getMessage());

            throw new FileUploadException(
                    String.format("Error subiendo PDF '%s' para el usuario '%s'",
                            file.getOriginalFilename(),
                            userId));
        }
    }

    public UserResponse getUserById(String id) throws UserNotFoundException {
        User user = findUserById(id);
        return new UserResponse(user);
    }

    public UserResponse activateUser(String id) throws UserNotFoundException {

        User user = findUserById(id);
        CustomUserDetails currentUser = getCurrentUserDetails();

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

    private User findUserById(String id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            logger.warn("Error al obtener el usuario: El usuario con id {} no existe", id);
            throw new UserNotFoundException("El usuario con id " + id + " no existe");
        }

        return user.get();
    }

    private CustomUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnathenticatedException("No se ha podido obtener la autenticación del usuario");
        }

        CustomUserDetails currentUser = (CustomUserDetails) auth.getPrincipal();
        if (currentUser == null) {
            throw new UnathenticatedException("No se ha podido obtener la información del usuario");
        }

        return currentUser;
    }

    private void validateAvatar(MultipartFile avatar) {
        if (avatar != null && !avatar.isEmpty()) {
            String contentType = avatar.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("El archivo de avatar debe ser una imagen");
            }

            long maxMB = 2;
            long maxSize = maxMB * 11048576;

            if (avatar.getSize() > maxSize) {
                throw new IllegalArgumentException("El archivo de avatar no puede superar los 2MB");
            }

        }
    }

    private void validatePaymentReceipt(MultipartFile pdf) {
        if (pdf == null || pdf.isEmpty())
            throw new IllegalArgumentException("El archivo de carta de pago es obligatorio");
        String contentType = pdf.getContentType();
        if (contentType == null || !contentType.equals("application/pdf")) {
            throw new IllegalArgumentException("El archivo de carta de pago debe ser un PDF");
        }
        long maxMB = 2;
        long maxSize = maxMB * 11048576;
        if (pdf.getSize() > maxSize) {
            throw new IllegalArgumentException("El archivo de carta de pago no puede superar los 2MB");
        }
    }

}
