package com.tfg.cultura.api.seeder.parser;

import java.util.List;
import java.util.Map;

import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.enumerators.RolBookType;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.Section;

public class RolGameCsvParser extends ItemCsvParser {

    private static final String CSV_FILE_PATH = "data/rol_games.csv";

    public List<RolGame> loadRolGamesFromCsv(Map<String, RolSaga> sagasByName, Map<String, Category> categoriesByName,
            Map<String, Section> sectionsByName) {
        return loadCsv(CSV_FILE_PATH, line -> mapLine(line, sagasByName, categoriesByName, sectionsByName));
    }

    private RolGame mapLine(String line, Map<String, RolSaga> sagasByName, Map<String, Category> categoriesByName,
            Map<String, Section> sectionsByName) {
        String[] parts = lineToParts(line);

        RolGame.RolGameBuilder<?, ?> builder = RolGame.builder();

        RolSaga saga = sagasByName.get(clean(parts[14]));
        RolBookType type = RolBookType.valueOf(clean(parts[15]));

        fillItemFields(builder, parts, categoriesByName, sectionsByName);

        return builder
                .categories(saga.getCategories())
                .section(saga.getSection())
                .saga(saga)
                .type(type)
                .build();
    }
}
