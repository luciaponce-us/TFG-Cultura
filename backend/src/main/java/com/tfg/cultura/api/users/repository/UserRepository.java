package com.tfg.cultura.api.users.repository;

import com.tfg.cultura.api.users.model.User;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String>, UserRepositoryCustom {
	boolean existsByUsername(String username);
	boolean existsByDni(String dni);

	Optional<User> findByUsername(String username);
	List<User> findByUsernameIn(Collection<String> usernames);

	Page<User> findAll(Pageable pageable);
}
