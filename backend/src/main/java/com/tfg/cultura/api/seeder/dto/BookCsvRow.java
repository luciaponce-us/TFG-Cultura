package com.tfg.cultura.api.seeder.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class BookCsvRow extends ItemCsvRow {
    private String author;
    private String isbn;
    private String saga;
    private Integer number;
    private String type;
    private String prequel;
    private String sequel;
}
