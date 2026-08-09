package com.tfg.cultura.api.catalog.model.dto;

import java.time.LocalDate;
import java.util.List;

import com.tfg.cultura.api.catalog.model.Season;
import com.tfg.cultura.api.catalog.model.Series;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.catalog.model.enumerators.SeriesStatus;

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
public class SeriesResponse extends ItemResponse {
    private Format format;
    private Integer numberOfDiscs;
    private LocalDate releaseDate;
    private Integer numberOfSeasons;
    private SeriesStatus status;
    private List<Season> seasons;

    public SeriesResponse(Series series) {
        super(series);
        this.format = series.getFormat();
        this.numberOfDiscs = series.getNumberOfDiscs();
        this.releaseDate = series.getSeriesInfo().getReleaseDate();
        this.numberOfSeasons = series.getSeriesInfo().getNumberOfSeasons();
        this.status = series.getSeriesInfo().getStatus();
        this.seasons = series.getSeasons();
    }
}
