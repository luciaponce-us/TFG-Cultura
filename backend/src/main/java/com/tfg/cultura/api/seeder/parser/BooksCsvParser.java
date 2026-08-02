package com.tfg.cultura.api.seeder.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.model.enumerators.ItemCondition;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.seeder.dto.BookCsvRow;

@Component
public class BooksCsvParser {

    private static final String CSV_FILE_PATH = "data/books.csv";

    public List<Book> loadBooksFromCsv(
            Map<String, Section> sectionsByName,
            Map<String, Category> categoriesByName) {

        return loadBookCsvRowsFromCsv().stream()
                .map(row -> toBook(row, sectionsByName, categoriesByName))
                .toList();
    }

    private List<BookCsvRow> loadBookCsvRowsFromCsv() {
        Resource resource = new ClassPathResource(CSV_FILE_PATH);

        try (InputStream is = resource.getInputStream();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(is, StandardCharsets.UTF_8))) {

            return reader.lines()
                    .skip(1)
                    .map(this::mapLine)
                    .toList();

        } catch (IOException e) {
            throw new IllegalStateException("Error leyendo books.csv", e);
        }
    }

    public List<Book> assignPrequelsAndSequels(List<Book> loadedBooks) {
        List<BookCsvRow> bookCsvRows = loadBookCsvRowsFromCsv();
        Map<String, Book> loadedBooksByName = loadedBooks.stream()
                .collect(Collectors.toMap(Book::getName, book -> book));

        for (BookCsvRow row : bookCsvRows) {
            Book currentBook = loadedBooksByName.get(row.getName());

            String prequelName = row.getPrequel();
            if (prequelName != null) {
                Book prequel = loadedBooksByName.get(prequelName);
                if (prequel == null) {
                    throw new IllegalStateException(
                            "No existe el libro predecesor '" + prequelName + "'");
                }
                currentBook.setPrequel(prequel);
            }

            String sequelName = row.getSequel();
            if (sequelName != null) {
                Book sequel = loadedBooksByName.get(sequelName);
                if (sequel == null) {
                    throw new IllegalStateException(
                            "No existe el libro sucesor '" + sequelName + "'");
                }
                currentBook.setSequel(sequel);
            }
        }

        return loadedBooks;
    }

    private BookCsvRow mapLine(String line) {

        String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);

        return BookCsvRow.builder()
                .name(clean(parts[0]))
                .description(clean(parts[1]))
                .imageUrl(parseNullableString(parts[2]))
                .condition(clean(parts[3]))
                .comments(parseNullableString(parts[4]))
                .loanAvailable(Boolean.valueOf(parts[5]))
                .publicated(Boolean.valueOf(parts[6]))
                .purchasedAt(LocalDate.parse(parts[7]).toString())
                .price(parts[8])
                .copies(Integer.valueOf(parts[9]))
                .availableCopies(Integer.valueOf(parts[10]))
                .loanDays(Integer.valueOf(parts[11]))
                .section(clean(parts[12]))
                .categories(parts[13])
                .author(clean(parts[14]))
                .isbn(clean(parts[15]))
                .saga(parseNullableString(parts[16]))
                .number(parseNullableInteger(parts[17]))
                .type(clean(parts[18]))
                .prequel(parseNullableString(parts[19]))
                .sequel(parseNullableString(parts[20]))
                .build();
    }

    private Book toBook(
            BookCsvRow row,
            Map<String, Section> sectionsByName,
            Map<String, Category> categoriesByName) {

        LocalDate purchasedAt = row.getPurchasedAt() != null
                ? LocalDate.parse(row.getPurchasedAt())
                : null;

        BigDecimal price = new BigDecimal(row.getPrice().trim())
                .setScale(2, RoundingMode.HALF_UP);
        List<String> categoryNames = parseCategories(row.getCategories());
        Set<Category> categories = getCategories(categoryNames, categoriesByName);

        return Book.builder()
                .name(row.getName())
                .description(row.getDescription())
                .imageUrl(row.getImageUrl())
                .condition(ItemCondition.valueOf(row.getCondition()))
                .comments(row.getComments())
                .loanAvailable(row.getLoanAvailable())
                .publicated(row.getPublicated())
                .purchasedAt(purchasedAt)
                .price(price)
                .copies(row.getCopies())
                .availableCopies(row.getAvailableCopies())
                .loanDays(row.getLoanDays())
                .section(getSection(row.getSection(), sectionsByName))
                .categories(categories)
                .author(row.getAuthor())
                .isbn(row.getIsbn())
                .saga(row.getSaga())
                .number(row.getNumber())
                .type(BookType.valueOf(row.getType()))
                .build();
    }

    private Section getSection(
            String name,
            Map<String, Section> sectionsByName) {

        Section section = sectionsByName.get(name);

        if (section == null) {
            throw new IllegalStateException("No existe la sección '" + name + "'");
        }

        return section;
    }

    private Set<Category> getCategories(
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

    private List<String> parseCategories(String value) {

        value = clean(value);

        if (value.equals("[]")) {
            return List.of();
        }

        value = value.substring(1, value.length() - 1);

        return Arrays.stream(value.split(","))
                .map(this::clean)
                .toList();
    }

    private String parseNullableString(String value) {

        value = clean(value);

        return value.equalsIgnoreCase("null") ? null : value;
    }

    private Integer parseNullableInteger(String value) {

        value = clean(value);

        return value.equalsIgnoreCase("null")
                ? null
                : Integer.valueOf(value);
    }

    private String clean(String value) {
        return value.trim().replace("\"", "");
    }
}
