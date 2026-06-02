package com.tfg.cultura.api.users.jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.users.repository.UserRepository;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger("usersLogger");

    @Override
    public UserDetails loadUserByUsername(String username) throws UserNotFoundException{

        User user = userRepository.findByUsername(username).orElseThrow(() -> {
                logger.warn("Error al conceder permisos: El usuario {} no existe", username);
                return new UserNotFoundException("El usuario con username " + username + " no existe");
            });

        return new CustomUserDetails(user);
    }

    public UserDetails loadUserById(String id) throws UserNotFoundException {
        User user = userRepository.findById(id).orElseThrow(() -> {
            logger.warn("Error al conceder permisos: El usuario con id {} no existe", id);
            return new UserNotFoundException("El usuario con id " + id + " no existe");
        });

        return new CustomUserDetails(user);
    }

    public CustomUserDetails getCurrentUserDetails() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UnathenticatedException("No se ha podido obtener la autenticación del usuario");
        }

        if (!(auth.getPrincipal() instanceof CustomUserDetails currentUser)) {
            throw new UnathenticatedException("No se ha podido obtener la información del usuario");
        }

        if (!userRepository.existsById(currentUser.getId()))
            throw new UserNotFoundException("El usuario logado no existe");

        return currentUser;
    }
}