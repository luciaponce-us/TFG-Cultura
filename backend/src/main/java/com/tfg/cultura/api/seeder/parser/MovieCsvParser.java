package com.tfg.cultura.api.seeder.parser;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.MovieInfo;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.Section;

@Component
public class MovieCsvParser extends ItemCsvParser {

    private static final String CSV_FILE_PATH = "data/movies.csv";

    public List<Movie> loadMoviesFromCsv(
            Map<String, Section> sectionsByName,
            Map<String, Category> categoriesByName,
            Map<String, Saga> sagasByName) {

        return loadCsv(CSV_FILE_PATH, line -> mapLine(line, sectionsByName, categoriesByName, sagasByName));
    }

    private Movie mapLine(
            String line,
            Map<String, Section> sectionsByName,
            Map<String, Category> categoriesByName,
            Map<String, Saga> sagasByName) {

        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        Movie.MovieBuilder<?, ?> builder = Movie.builder();

        fillItemFields(builder, parts, categoriesByName, sectionsByName);

        MovieInfo movieInfo = MovieInfo.builder()
                .releaseDate(parseNullableString(parts[16]) != null
                        ? parseLocalDate(parts[16])
                        : null)
                .trailerUrl(parseNullableString(parts[17]))
                .saga(getSaga(parts[18], sagasByName))
                .build();

        return builder
                .format(Format.valueOf(clean(parts[14])))
                .numberOfDiscs(Integer.parseInt(parts[15]))
                .movieInfo(movieInfo)
                .build();
    }

}
