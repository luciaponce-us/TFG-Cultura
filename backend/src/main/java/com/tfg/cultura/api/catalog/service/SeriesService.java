package com.tfg.cultura.api.catalog.service;

import com.tfg.cultura.api.catalog.exception.series.InvalidNumberOfSeasonsException;
import com.tfg.cultura.api.catalog.exception.series.PurchasedAtBeforeReleaseDateException;
import com.tfg.cultura.api.catalog.model.Series;
import com.tfg.cultura.api.catalog.model.SeriesInfo;
import com.tfg.cultura.api.catalog.model.dto.SeriesRequest;
import com.tfg.cultura.api.catalog.model.dto.SeriesResponse;
import com.tfg.cultura.api.catalog.repository.SeriesRepository;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;
import org.springframework.stereotype.Service;

@Service
public class SeriesService extends AbstractItemService<Series, SeriesRepository, SeriesRequest, SeriesResponse> {

	private final AppProperties appProperties;

	public SeriesService(SeriesRepository seriesRepository, SectionService sectionService,
			CategoryService categoryService, FileService fileService, AppProperties appProperties) {
		super(seriesRepository, sectionService, categoryService, fileService, SeriesResponse::new);
		this.appProperties = appProperties;
	}

	@Override
	protected String getImageFolder() {
		return "cultura/items/series";
	}

	@Override
	protected String getDefaultImageUrl() {
		return appProperties.defaultImages().series();
	}

	@Override
	protected void validate(Series item) throws PurchasedAtBeforeReleaseDateException, InvalidNumberOfSeasonsException {
		checkPurchasedAtAfterReleaseDate(item);
		checkNumberOfSeasons(item);
	}

	private void checkPurchasedAtAfterReleaseDate(Series item) {
		if (item.getPurchasedAt() != null && item.getPurchasedAt().isBefore(item.getSeriesInfo().getReleaseDate())) {
			throw new PurchasedAtBeforeReleaseDateException();
		}
	}

	private void checkNumberOfSeasons(Series item) {
		Integer higherSeason = item.getSeasons().stream().mapToInt(season -> season.getSeasonNumber()).max().orElse(0);
		Integer maxSeason = item.getSeriesInfo().getNumberOfSeasons();
		if (higherSeason > maxSeason) {
			throw new InvalidNumberOfSeasonsException(higherSeason, maxSeason);
		}
	}

	@Override
	protected Series createEntity() {
		return Series.builder().build();
	}

	@Override
	protected void fillSpecificFields(Series item, SeriesRequest request) {
		SeriesInfo seriesInfo = SeriesInfo.builder().releaseDate(request.getReleaseDate())
				.numberOfSeasons(request.getNumberOfSeasons()).status(request.getStatus()).build();

		item.setFormat(request.getFormat());
		item.setNumberOfDiscs(request.getNumberOfDiscs());
		item.setSeriesInfo(seriesInfo);
		item.setSeasons(request.getSeasons());
	}

	@Override
	protected Integer getLoanDays(SeriesRequest request) {
		switch (request.getNumberOfDiscs()) {
			case 1 :
				return 3;
			default :
				return 7;
		}
	}

}
