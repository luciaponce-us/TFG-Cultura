package com.tfg.cultura.api.catalog.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.tfg.cultura.api.catalog.model.enumerators.Format;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "movies")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Movie extends Item {
    private Format format;
    private Integer numberOfDiscs;
    private MovieInfo movieInfo;
    
}
