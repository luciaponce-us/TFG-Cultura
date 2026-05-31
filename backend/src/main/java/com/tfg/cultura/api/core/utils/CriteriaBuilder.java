package com.tfg.cultura.api.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;

public class CriteriaBuilder {
    public static Criteria buildTextCriteria(String text, List<String> fields) {
        if (text == null || text.isBlank() || fields == null || fields.isEmpty()) {
            return null;
        }

        String[] tokens = text.trim().split("\\s+");

        List<Criteria> tokenAndCriteria = new ArrayList<>();

        for (String token : tokens) {

            List<Criteria> fieldOrCriteria = fields.stream()
                    .map(field -> Criteria.where(field).regex(token, "i"))
                    .toList();

            tokenAndCriteria.add(new Criteria().orOperator(
                    fieldOrCriteria.toArray(new Criteria[0])));
        }

        return new Criteria().andOperator(
                tokenAndCriteria.toArray(new Criteria[0]));
    }
}
