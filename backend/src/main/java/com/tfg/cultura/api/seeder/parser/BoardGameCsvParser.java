package com.tfg.cultura.api.seeder.parser;

import com.tfg.cultura.api.catalog.model.BoardGame;
import com.tfg.cultura.api.catalog.model.enumerators.BoardGameType;
import com.tfg.cultura.api.catalog.model.enumerators.Complexity;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.Section;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class BoardGameCsvParser extends ItemCsvParser {

	private static final String CSV_FILE_PATH_BASE = "data/boardgames.csv";
	private static final String CSV_FILE_PATH_EXPANSION = "data/boardgames_expansion.csv";

	public List<BoardGame> loadBaseBoardGamesFromCsv(Map<String, Section> sectionsByName,
			Map<String, Category> categoriesByName) {
		return loadCsv(CSV_FILE_PATH_BASE, line -> mapLine(line, sectionsByName, categoriesByName, null));
	}

	public List<BoardGame> loadBoardGamesExpansionFromCsv(Map<String, Section> sectionsByName,
			Map<String, Category> categoriesByName, Map<String, BoardGame> baseGamesByName) {
		return loadCsv(CSV_FILE_PATH_EXPANSION,
				line -> mapLine(line, sectionsByName, categoriesByName, baseGamesByName));
	}

	private BoardGame mapLine(String line, Map<String, Section> sectionsByName, Map<String, Category> categoriesByName,
			Map<String, BoardGame> baseGamesByName) {

		String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

		BoardGame.BoardGameBuilder<?, ?> builder = BoardGame.builder();

		fillItemFields(builder, parts, categoriesByName, sectionsByName);

		Set<BoardGameType> types = parseList(parts[18]).stream().map(BoardGameType::valueOf)
				.collect(Collectors.toSet());

		return builder.minPlayers(parseInteger(parts[14])).maxPlayers(parseInteger(parts[15]))
				.playTime(parseInteger(parts[16])).complexity(Complexity.valueOf(parts[17])).types(types)
				.baseGame(getBaseGame(parts[16], baseGamesByName)).build();
	}

	private BoardGame getBaseGame(String baseGameId, Map<String, BoardGame> baseGamesByName) {
		if (baseGameId == null || baseGameId.isEmpty() || baseGamesByName == null) {
			return null;
		}
		return baseGamesByName.get(baseGameId);
	}

}
