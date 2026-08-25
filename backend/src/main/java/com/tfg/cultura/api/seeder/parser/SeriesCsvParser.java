package com.tfg.cultura.api.seeder.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.tfg.cultura.api.catalog.model.Season;
import com.tfg.cultura.api.catalog.model.Series;
import com.tfg.cultura.api.catalog.model.SeriesInfo;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.catalog.model.enumerators.SeriesStatus;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.Section;

public class SeriesCsvParser extends ItemCsvParser {

    private static final String CSV_FILE_PATH = "data/series.csv";

    public List<Series> loadSeriesFromCsv(
            Map<String, Section> sectionsByName,
            Map<String, Category> categoriesByName) {

        return loadCsv(CSV_FILE_PATH, line -> mapLine(line, sectionsByName, categoriesByName));
    }

    private Series mapLine(
            String line,
            Map<String, Section> sectionsByName,
            Map<String, Category> categoriesByName) {

        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        Series.SeriesBuilder<?, ?> builder = Series.builder();

        fillItemFields(builder, parts, categoriesByName, sectionsByName);

        SeriesInfo seriesInfo = SeriesInfo.builder()
                .releaseDate(parseLocalDate(parts[16]))
                .numberOfSeasons(parseInteger(parts[17]))
                .status(SeriesStatus.valueOf(clean(parts[18])))
                .build();

        List<Season> seasons = parseSeasons(parts[19]);

        return builder
                .format(Format.valueOf(clean(parts[14])))
                .numberOfDiscs(parseInteger(parts[15]))
                .seriesInfo(seriesInfo)
                .seasons(seasons)
                .build();
    }

    private List<Season> parseSeasons(String value) {
        // [ (seasonNumber,seasonPart, trailerUrl) ]

        value = clean(value);
        if (value.isEmpty()) {
            return List.of();
        }
        List<Season> seasons = new ArrayList<>();
        String[] seasonStrings = value
                .replace("[", "")
                .replace("]", "")
                .replace("(", "")
                .split("\\)");

        for (String seasonString : seasonStrings) {
            seasonString = clean(seasonString);
            if (seasonString.isEmpty()) {
                continue;
            }
            Season season = parseSeason(seasonString);
            seasons.add(season);
        }

        return seasons;
    }

    private Season parseSeason(String value) {
        // (seasonNumber,seasonPart, trailerUrl)

        

        String[] parts = value
                .replace("(", "")
                .replace(")", "")
                .split(",");

        Integer seasonNumber = parseInteger(parts[0]);
        Integer seasonPart = parseInteger(parts[1]);
        String trailerUrl = parseNullableString(parts[2]);

        return Season.builder()
                .seasonNumber(seasonNumber)
                .seasonPart(seasonPart)
                .trailerUrl(trailerUrl)
                .build();
    }

}
