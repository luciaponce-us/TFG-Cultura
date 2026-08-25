package com.tfg.cultura.api.seeder.parser;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.enumerators.GameMaster;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.Section;

public class RolSagaCsvParser extends ItemCsvParser {

    private static final String CSV_FILE_PATH = "data/rol_sagas.csv";

    public List<RolSaga> loadRolSagasFromCsv(Map<String, Section> sectionsByName, Map<String, Category> categoriesByName) {
        return loadCsv(CSV_FILE_PATH, (line) -> mapLine(line, sectionsByName, categoriesByName));
    }

    private RolSaga mapLine(String line, Map<String, Section> sectionsByName, Map<String, Category> categoriesByName) {
        String[] parts = lineToParts(line);

        Section section = getSection(clean(parts[8]), sectionsByName);
        Set<Category> categories = getCategories(parseList(parts[9]), categoriesByName);

        RolSaga rolSaga = RolSaga.builder()
                .name(clean(parts[0]))
                .description(clean(parts[1]))
                .website(parseNullableString(parts[2]))
                .imageUrl(parseNullableString(parts[3]))
                .characterSheetUrl(parseNullableString(parts[4]))
                .gameMaster(GameMaster.valueOf(clean(parts[5])))
                .dice(parseNullableString(parts[6]))
                .recommendedPlayers(parseNullableString(parts[7]))
                .section(section)
                .categories(categories)
                .build();

        return rolSaga;
    }
    
}
