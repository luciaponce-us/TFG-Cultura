package com.tfg.cultura.api.categories.factory;

import com.tfg.cultura.api.categories.model.Category;

public class CategoryFactory {
    public static Category validCategory() {
        return Category.builder()
                .id("1")
                .name("Test Category")
                .build();
    }

    public static Category anotherValidCategory() {
        return Category.builder()
                .id("2")
                .name("Another Test Category")
                .build();
    }
}
