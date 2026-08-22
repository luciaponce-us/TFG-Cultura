package com.tfg.cultura.api.catalog.model.dto;

import java.time.LocalDate;

import com.tfg.cultura.api.catalog.model.VideoGame;
import com.tfg.cultura.api.catalog.model.enumerators.Platform;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class VideoGameResponse extends ItemResponse{
    private Platform platform;
    private LocalDate releaseDate;
    private String trailerUrl;

    public VideoGameResponse(VideoGame videoGame) {
        super(videoGame);
        this.platform = videoGame.getPlatform();
        this.releaseDate = videoGame.getReleaseDate();
        this.trailerUrl = videoGame.getTrailerUrl();
    }
}
