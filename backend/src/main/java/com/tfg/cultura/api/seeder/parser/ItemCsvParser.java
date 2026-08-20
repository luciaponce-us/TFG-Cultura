package com.tfg.cultura.api.seeder.parser;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.enumerators.ItemCondition;
import com.tfg.cultura.api.sections.model.Section;

public abstract class ItemCsvParser extends CsvParser {

    protected void fillItemFields(
            Item.ItemBuilder<?, ?> builder,
            String[] parts,
            Map<String, Category> categoriesByName,
            Map<String, Section> sectionsByName) {

        Set<Category> categories = getCategories(parseList(parts[13]), categoriesByName);
        Section section = getSection(parseNullableString(parts[12]), sectionsByName);

        builder
                .name(clean(parts[0]))
                .description(clean(parts[1]))
                .imageUrl(parseNullableString(parts[2]))
                .condition(ItemCondition.valueOf(clean(parts[3])))
                .comments(parseNullableString(parts[4]))
                .loanAvailable(Boolean.parseBoolean(parts[5]))
                .publicated(Boolean.parseBoolean(parts[6]))
                .purchasedAt(parseLocalDate(parts[7]))
                .price(BigDecimal.valueOf(Double.parseDouble(clean(parts[8]))))
                .copies(parseInteger(parts[9]))
                .availableCopies(parseInteger(parts[10]))
                .loanDays(parseInteger(parts[11]))
                .section(section)
                .categories(categories);
    }

    protected Section getSection(
            String name,
            Map<String, Section> sectionsByName) {

        if (name == null || name.isEmpty()) {
            return null;
        }
        
        Section section = sectionsByName.get(name);

        if (section == null) {
            throw new IllegalStateException("No existe la sección '" + name + "'");
        }

        return section;
    }

    protected Set<Category> getCategories(
            List<String> names,
            Map<String, Category> categoriesByName) {

        return names.stream()
                .map(name -> {
                    Category category = categoriesByName.get(name);

                    if (category == null) {
                        throw new IllegalStateException(
                                "No existe la categoría '" + name + "'");
                    }

                    return category;
                })
                .collect(Collectors.toSet());
    }

    protected Saga getSaga(
            String name,
            Map<String, Saga> sagasByName) {

        String cleanedName = parseNullableString(name);

        if (cleanedName == null) {
            return null;
        }

        Saga saga = sagasByName.get(cleanedName);

        if (saga == null) {
            throw new IllegalStateException("No existe la saga '" + cleanedName + "'");
        }

        return saga;
    }
}
