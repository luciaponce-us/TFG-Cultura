package com.tfg.cultura.api.seeder.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;

import com.tfg.cultura.api.users.model.User;

public abstract class CsvParser {

    protected <T> List<T> loadCsv(String path, Function<String, T> mapper) {
        ClassPathResource resource = new ClassPathResource(path);

        try (InputStream is = resource.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines()
                    .skip(1)
                    .map(mapper)
                    .toList();

        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo " + path, e);
        }
    }

    protected static String[] lineToParts(String line) {
        return Arrays.stream(line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1))
                .map(String::trim)
                .map(s -> s.replace("\"", ""))
                .toArray(String[]::new);
    }

    protected static String parseNullableString(String value) {

        value = clean(value);

        return value.equalsIgnoreCase("null") ? null : value;
    }

    protected static String clean(String value) {
        return value.trim().replace("\"", "").trim();
    }

    protected static List<String> parseList(String value) {

        value = clean(value);

        if (value.equals("[]")) {
            return List.of();
        }

        List<String> cleanedValues = Arrays.stream(value.substring(1, value.length() - 1).split(","))
                .map(String::trim)
                .map(s -> s.replace("\"", ""))
                .toList();

        return cleanedValues;
    }

    protected static LocalDate parseLocalDate(String value) {
        return LocalDate.parse(clean(value));
    }

    protected static Integer parseInteger(String value) {
        if (clean(value).isEmpty() || clean(value).equalsIgnoreCase("null")) {
            return null;
        }
        value = value.trim();
        return Integer.parseInt(clean(value));
    }

    protected Set<User> getUsers(
            List<String> usernames,
            Map<String, User> usersByUsername) {

        return usernames.stream()
                .map(username -> {
                    User user = usersByUsername.get(username);

                    if (user == null) {
                        throw new IllegalStateException(
                                "No existe el usuario '" + username + "'");
                    }

                    return user;
                })
                .collect(Collectors.toSet());
    }
    
}
