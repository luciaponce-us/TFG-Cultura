package com.tfg.cultura.api.seeder.parser;

import com.tfg.cultura.api.categories.model.Category;
import java.util.List;

public class CategoryCsvParser extends CsvParser {

	private static final String CSV_FILE_PATH = "data/categories.csv";

	public List<Category> loadCategoriesFromCsv() {
		return loadCsv(CSV_FILE_PATH, this::mapLine);
	}

	private Category mapLine(String line) {
		String[] parts = lineToParts(line);

		return Category.builder().name(clean(parts[0])).color(clean(parts[1])).build();
	}

}
