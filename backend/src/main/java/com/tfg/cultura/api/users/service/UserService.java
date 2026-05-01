package com.tfg.cultura.api.users.service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.repository.UserRepository;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final Logger logger = LoggerFactory.getLogger("usersLogger");

    public UserResponse getUserById(String id) throws UserNotFoundException {
        User user = findUserById(id);
        return new UserResponse(user);
    }

    User findUserById(String id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);

        if (user.isEmpty()) {
            logger.warn("Error al obtener el usuario: El usuario con id {} no existe", id);
            throw new UserNotFoundException("El usuario con id " + id + " no existe");
        }

        return user.get();
    }
    
}
