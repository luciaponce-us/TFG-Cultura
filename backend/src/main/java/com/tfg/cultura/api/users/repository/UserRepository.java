package com.tfg.cultura.api.users.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.tfg.cultura.api.users.model.User;


public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {
    boolean existsByUsername(String username);
    boolean existsByDni(String dni);

    Optional<User> findByUsername(String username);
    
    Page<User> findAll(Pageable pageable);
}
