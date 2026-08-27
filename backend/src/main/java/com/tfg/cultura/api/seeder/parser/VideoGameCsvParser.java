package com.tfg.cultura.api.seeder.parser;

import com.tfg.cultura.api.catalog.model.VideoGame;
import com.tfg.cultura.api.catalog.model.enumerators.Platform;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.Section;
import java.util.List;
import java.util.Map;

public class VideoGameCsvParser extends ItemCsvParser {

	private static final String CSV_FILE_PATH = "data/videogames.csv";

	public List<VideoGame> loadVideoGamesFromCsv(Map<String, Section> sectionsByName,
			Map<String, Category> categoriesByName) {
		return loadCsv(CSV_FILE_PATH, line -> mapLine(line, sectionsByName, categoriesByName));
	}

	private VideoGame mapLine(String line, Map<String, Section> sectionsByName,
			Map<String, Category> categoriesByName) {
		String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

		VideoGame.VideoGameBuilder<?, ?> builder = VideoGame.builder();

		fillItemFields(builder, parts, categoriesByName, sectionsByName);

		return builder.platform(Platform.valueOf(clean(parts[14]))).releaseDate(parseLocalDate(parts[15]))
				.trailerUrl(parseNullableString(parts[16])).build();
	}

}
