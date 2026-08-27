package com.tfg.cultura.api.seeder.parser;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.Section;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class BooksCsvParser extends ItemCsvParser {

	private static final String CSV_FILE_PATH = "data/books.csv";

	public List<Book> loadBooksFromCsv(Map<String, Section> sectionsByName, Map<String, Category> categoriesByName,
			Map<String, Saga> sagasByName) {

		return loadCsv(CSV_FILE_PATH, line -> mapLine(line, sectionsByName, categoriesByName, sagasByName));
	}

	private Book mapLine(String line, Map<String, Section> sectionsByName, Map<String, Category> categoriesByName,
			Map<String, Saga> sagasByName) {

		String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

		Book.BookBuilder<?, ?> builder = Book.builder();

		fillItemFields(builder, parts, categoriesByName, sectionsByName);

		return builder.author(clean(parts[14])).isbn(clean(parts[15])).type(BookType.valueOf(clean(parts[16])))
				.saga(getSaga(parts[17], sagasByName)).build();
	}

}
