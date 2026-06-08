package com.tfg.cultura.api.suggestions.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import com.tfg.cultura.api.core.utils.CriteriaBuilder;

public class SuggestionRepositoryImpl implements SuggestionRespositoryCustom {
    private MongoTemplate mongoTemplate;
    private CustomUserDetailsService userDetailsService;
    private static final List<Role> MANAGEMENT_ROLES = List.of(
            Role.SECRETARIO,
            Role.COORDINADOR,
            Role.ENCARGADO,
            Role.COLABORADOR);

    public SuggestionRepositoryImpl(MongoTemplate mongoTemplate, CustomUserDetailsService userDetailsService) {
        this.mongoTemplate = mongoTemplate;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Page<Suggestion> findAllWithFilters(
            SuggestionType type,
            String text,
            Boolean supportedByAdmins,
            Boolean mySuggestions,
            Pageable pageable) {

        List<Criteria> filters = new ArrayList<>();

        // 🔹 Filtro por tipo
        if (type != null) {
            filters.add(Criteria.where("type").is(type));
        }

        // 🔹 Filtro de texto
        Criteria textCriteria = CriteriaBuilder.buildTextCriteria(text, List.of("title", "description"));
        if (textCriteria != null) {
            filters.add(textCriteria);
        }

        // 🔹 Filtro por admins
        if (Boolean.TRUE.equals(supportedByAdmins)) {
            Query usersQuery = new Query(Criteria.where("role").in(MANAGEMENT_ROLES));
            usersQuery.fields().include("id");

            List<String> adminIds = mongoTemplate.find(usersQuery, User.class).stream()
                    .map(User::getId)
                    .filter(Objects::nonNull)
                    .toList();

            if (adminIds.isEmpty()) {
                return new PageImpl<>(List.of(), pageable, 0);
            }

            filters.add(Criteria.where("supportersId").in(adminIds));
        }

        // 🔹 Filtro por mis sugerencias
        if (Boolean.TRUE.equals(mySuggestions)) {
            CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
            filters.add(Criteria.where("authorId").is(currentUser.getId()));
        }

        // 🔹 Construcción del criteria
        Criteria criteria = filters.isEmpty()
            ? new Criteria()
            : buildFiltersCriteria(filters);

        // 🔹 Query + paginación + sort
        Query query = new Query(criteria).with(pageable);

        long total = mongoTemplate.count(query, Suggestion.class);
        List<Suggestion> results = mongoTemplate.find(query, Suggestion.class);

        return new PageImpl<>(results, pageable, total);
    }

    private static Criteria buildFiltersCriteria(List<Criteria> filters) {
        return filters.size() == 1
                ? filters.get(0)
                : new Criteria().andOperator(filters.toArray(new Criteria[0]));
    }

}
