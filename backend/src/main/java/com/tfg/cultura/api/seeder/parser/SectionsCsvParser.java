package com.tfg.cultura.api.seeder.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.seeder.dto.SectionCsvRow;
import com.tfg.cultura.api.users.model.User;

@Component
public class SectionsCsvParser {
    private static final String CSV_FILE_PATH = "../data/sections.csv";

    public List<Section> loadSectionsFromCsv(Map<String, User> usersByUsername) {

        InputStream is = SectionsCsvParser.class.getResourceAsStream(CSV_FILE_PATH);

        if (is == null) {
            throw new IllegalStateException("No se encontró " + CSV_FILE_PATH);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines()
                    .skip(1)
                    .map(this::mapLine)
                    .map(row -> toSection(row, usersByUsername))
                    .toList();

        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo sections.csv", e);
        }
    }

    private SectionCsvRow mapLine(String line) {

        String[] parts = line.split(",");

        return SectionCsvRow.builder()
                .name(parts[0])
                .managers(parseUsernames(parts[1]))
                .collaborators(parseUsernames(parts[2]))
                .build();
    }

    private List<String> parseUsernames(String value) {
        if (value.isBlank()) {
            return List.of();
        }

        return Arrays.stream(value.split(";"))
                .map(String::trim)
                .toList();
    }

    private Section toSection(
            SectionCsvRow row,
            Map<String, User> usersByUsername) {

        return Section.builder()
                .name(row.getName())
                .managers(getUsers(row.getManagers(), usersByUsername))
                .collaborators(getUsers(row.getCollaborators(), usersByUsername))
                .build();
    }

    private Set<User> getUsers(
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
