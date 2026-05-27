package com.tfg.cultura.api.users.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

import java.util.List;

public class UserRepositoryImpl implements UserRepositoryCustom {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Page<User> findAllWithFilters(Role role, Boolean active, String name, Pageable pageable) {
        Query query = new Query();

        // Apply role filter
        if (role != null) {
            query.addCriteria(Criteria.where("role").is(role));
        }

        // Apply active status filter
        if (active != null) {
            query.addCriteria(Criteria.where("active").is(active));
        }

        // Apply name filter (case-insensitive partial match)
        if (name != null && !name.trim().isEmpty()) {
            String trimmedName = name.trim();
            String[] tokens = trimmedName.split("\\s+");
            Criteria[] tokenCriteria = new Criteria[tokens.length];

            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                tokenCriteria[i] = new Criteria().orOperator(
                        Criteria.where("name").regex(token, "i"),
                        Criteria.where("surname").regex(token, "i"),
                        Criteria.where("username").regex(token, "i")
                );
            }

            query.addCriteria(new Criteria().andOperator(tokenCriteria));
        }

        // Get total count before applying pagination
        long total = mongoTemplate.count(query, User.class);

        // Apply pagination and sorting
        query.with(pageable);

        // Execute query
        List<User> users = mongoTemplate.find(query, User.class);

        return new PageImpl<>(users, pageable, total);
    }
}
