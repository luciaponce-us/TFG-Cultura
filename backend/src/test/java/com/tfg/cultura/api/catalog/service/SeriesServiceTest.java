package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Series;
import com.tfg.cultura.api.catalog.model.SeriesInfo;
import com.tfg.cultura.api.catalog.model.dto.SeriesRequest;
import com.tfg.cultura.api.catalog.repository.SeriesRepository;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeriesServiceTest {

	@Mock
	private SeriesRepository seriesRepository;

	@Mock
	private SectionService sectionService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private FileService fileService;

	@InjectMocks
	private SeriesService service;

	private Series series;
	private SeriesRequest request;

	@BeforeEach
	void setUp() {
		series = CatalogFactory.validSeries();
		request = CatalogFactory.validSeriesRequest();
	}

	@Test
	void should_not_throw_when_purchase_is_after_release() {
		series.setPurchasedAt(LocalDate.of(2015, 01, 01));
		SeriesInfo info = series.getSeriesInfo();
		info.setReleaseDate(LocalDate.of(2014, 01, 01));
		series.setSeriesInfo(info);

		assertDoesNotThrow(() -> service.validate(series));
	}

	@Test
	void should_not_throw_when_purchase_date_is_null() {
		series.setPurchasedAt(null);
		assertDoesNotThrow(() -> service.validate(series));
	}

	@Test
	void should_not_throw_when_purchase_is_equals_to_release() {
		LocalDate date = LocalDate.of(2015, 01, 01);
		series.setPurchasedAt(date);
		SeriesInfo info = series.getSeriesInfo();
		info.setReleaseDate(date);
		series.setSeriesInfo(info);

		assertDoesNotThrow(() -> service.validate(series));
	}

	@Test
	void should_throw_when_purchase_is_before_release() {
		series.setPurchasedAt(LocalDate.of(2014, 01, 01));
		SeriesInfo info = series.getSeriesInfo();
		info.setReleaseDate(LocalDate.of(2015, 01, 01));
		series.setSeriesInfo(info);

		assertThrows(IllegalArgumentException.class, () -> service.validate(series));
	}

	@Test
	void should_not_throw_when_number_of_seasons_is_less_than_series_info() {
		series.getSeasons().get(0).setSeasonNumber(1);
		series.getSeriesInfo().setNumberOfSeasons(2);

		assertDoesNotThrow(() -> service.validate(series));
	}

	@Test
	void should_not_throw_when_number_of_seasons_is_equal_to_series_info() {
		series.getSeasons().get(0).setSeasonNumber(2);
		series.getSeriesInfo().setNumberOfSeasons(2);

		assertDoesNotThrow(() -> service.validate(series));
	}

	@Test
	void should_throw_when_number_of_seasons_is_greater_than_series_info() {
		series.getSeasons().get(0).setSeasonNumber(3);
		series.getSeriesInfo().setNumberOfSeasons(2);

		assertThrows(IllegalArgumentException.class, () -> service.validate(series));
	}

	@Test
	void should_throw_when_number_of_seasons_is_greater_than_series_info_with_multiple_seasons() {
		series.getSeasons().get(0).setSeasonNumber(1);
		series.getSeasons().add(CatalogFactory.validSeason());
		series.getSeasons().get(1).setSeasonNumber(3);
		series.getSeriesInfo().setNumberOfSeasons(2);

		assertThrows(IllegalArgumentException.class, () -> service.validate(series));
	}

	@Test
	void should_fill_specific_fields_correctly() {
		Series newSeries = new Series();
		service.fillSpecificFields(newSeries, request);

		assertDoesNotThrow(() -> service.validate(newSeries));
		assertEquals(request.getFormat(), newSeries.getFormat());
		assertEquals(request.getNumberOfDiscs(), newSeries.getNumberOfDiscs());
		assertEquals(request.getReleaseDate(), newSeries.getSeriesInfo().getReleaseDate());
		assertEquals(request.getNumberOfSeasons(), newSeries.getSeriesInfo().getNumberOfSeasons());
		assertEquals(request.getStatus(), newSeries.getSeriesInfo().getStatus());
		assertEquals(request.getSeasons(), newSeries.getSeasons());
	}

	@Test
	void should_return_correct_loan_days_based_on_number_of_discs() {
		Integer loanDays1Disc = 3;
		Integer loanDays2Discs = 7;

		SeriesRequest request1Disc = CatalogFactory.validSeriesRequest();
		request1Disc.setNumberOfDiscs(1);
		assertEquals(loanDays1Disc, service.getLoanDays(request1Disc));

		SeriesRequest request2Discs = CatalogFactory.validSeriesRequest();
		request2Discs.setNumberOfDiscs(2);
		assertEquals(loanDays2Discs, service.getLoanDays(request2Discs));
	}

}
