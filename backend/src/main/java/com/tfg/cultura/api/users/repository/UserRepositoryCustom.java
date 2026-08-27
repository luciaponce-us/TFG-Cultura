package com.tfg.cultura.api.users.repository;

import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepositoryCustom {
	Page<User> findAllWithFilters(Role role, Boolean active, String name, Pageable pageable);
}
