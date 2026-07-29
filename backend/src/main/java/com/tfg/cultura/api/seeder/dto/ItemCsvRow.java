package com.tfg.cultura.api.seeder.dto;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class ItemCsvRow {
    private String name;
    private String description;
    private String imageUrl;
    private String condition;
    private String comments;
    private Boolean loanAvailable;
    private Boolean publicated;
    private String purchasedAt;
    private Double price;
    private Integer copies;
    private Integer availableCopies;
    private Integer loanDays;
    private String section;
    private String categories;
    
}
