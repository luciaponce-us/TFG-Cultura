package com.tfg.cultura.api.seeder.parser;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.users.model.User;

@Component
public class SectionsCsvParser extends CsvParser {

    private static final String CSV_FILE_PATH = "data/sections.csv";

    public List<Section> loadSectionsFromCsv(Map<String, User> usersByUsername) {
        return loadCsv(CSV_FILE_PATH, line -> mapLine(line, usersByUsername));
    }

    private Section mapLine(String line, Map<String, User> usersByUsername) {

        String[] parts = lineToParts(line);

        return Section.builder()
                .name(parseNullableString(parts[0]))
                .managers(getUsers(parseList(parts[1]), usersByUsername))
                .collaborators(getUsers(parseList(parts[2]), usersByUsername))
                .build();
    }

}
