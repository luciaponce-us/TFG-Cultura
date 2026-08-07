package com.tfg.cultura.api.catalog.model.enumerators;

public enum Format {
    DVD("DVD"),
    BLURAY("Blu-ray"),
    UHD_4K("4K");

    private final String displayName;

    Format(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
    
}
