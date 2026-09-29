package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.SagaRequest;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.catalog.repository.MovieRepository;
import com.tfg.cultura.api.catalog.repository.SagaRepository;
import com.tfg.cultura.api.core.exception.DuplicationException;
import com.tfg.cultura.api.core.exception.NotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SagaServiceTest {

	@Mock
	private SagaRepository sagaRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private MovieRepository movieRepository;

	@InjectMocks
	private SagaService service;

	private Saga saga;
	private Saga saga2;
	private SagaRequest request;

	@BeforeEach
	void setUp() {
		saga = CatalogFactory.validSaga();
		saga2 = CatalogFactory.validSaga2();
		request = CatalogFactory.validSagaRequest();
	}

	@Test
	void should_create_saga_when_name_is_available() {
		when(sagaRepository.existsByName(request.getName())).thenReturn(false);
		when(sagaRepository.save(any(Saga.class))).thenReturn(saga);

		Saga result = service.createSaga(request);

		assertEquals(saga, result);
		verify(sagaRepository).existsByName(request.getName());
		verify(sagaRepository).save(any(Saga.class));
	}

	@Test
	void should_throw_exception_when_saga_name_already_exists_on_create() {
		when(sagaRepository.existsByName(request.getName())).thenReturn(true);

		assertThrows(DuplicationException.class, () -> service.createSaga(request));

		verify(sagaRepository).existsByName(request.getName());
		verify(sagaRepository, never()).save(any(Saga.class));
	}

	@Test
	void should_find_saga_by_id_when_present() {
		when(sagaRepository.findById("1")).thenReturn(Optional.of(saga));

		Saga result = service.findById("1");

		assertEquals(saga, result);
	}

	@Test
	void should_throw_exception_when_saga_id_does_not_exist() {
		when(sagaRepository.findById("99")).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> service.findById("99"));
	}

	@Test
	void should_find_saga_by_name_when_present() {
		when(sagaRepository.findByName(saga.getName())).thenReturn(saga);

		Saga result = service.findByName(saga.getName());

		assertEquals(saga, result);
	}

	@Test
	void should_throw_exception_when_saga_name_does_not_exist() {
		when(sagaRepository.findByName("Unknown Saga")).thenReturn(null);

		assertThrows(NotFoundException.class, () -> service.findByName("Unknown Saga"));
	}

	@Test
	void should_return_all_sagas_ordered_by_name() {
		List<Saga> expectedSagas = List.of(saga2, saga);
		when(sagaRepository.findAllByOrderByNameAsc()).thenReturn(expectedSagas);

		List<Saga> result = service.findAll();

		assertEquals(expectedSagas, result);
		verify(sagaRepository).findAllByOrderByNameAsc();
	}

	@Test
	void should_update_saga_when_name_is_available() {
		when(sagaRepository.findById(saga.getId())).thenReturn(Optional.of(saga));
		when(sagaRepository.existsByName(request.getName())).thenReturn(false);
		when(sagaRepository.save(any(Saga.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Saga result = service.updateSaga(saga.getId(), request);

		assertEquals(request.getName(), result.getName());
		verify(sagaRepository).save(any(Saga.class));
	}

	@Test
	void should_throw_exception_when_update_name_already_exists() {
		when(sagaRepository.findById(saga.getId())).thenReturn(Optional.of(saga));
		when(sagaRepository.existsByName(request.getName())).thenReturn(true);

		assertThrows(DuplicationException.class, () -> service.updateSaga(saga.getId(), request));

		verify(sagaRepository, never()).save(any(Saga.class));
	}

	@Test
	void should_delete_saga_and_detach_books_from_it() {
		Book firstBook = CatalogFactory.validBookWithSaga("b1", saga);
		Book secondBook = CatalogFactory.validBookWithSaga("b2", saga);

		when(sagaRepository.findById(saga.getId())).thenReturn(Optional.of(saga));
		when(bookRepository.findAllBySaga(saga)).thenReturn(Set.of(firstBook, secondBook));
		when(movieRepository.findAllByMovieInfoSagaId(saga.getId())).thenReturn(Set.of());

		service.deleteSaga(saga.getId());

		assertNull(firstBook.getSaga());
		assertNull(secondBook.getSaga());
		verify(bookRepository).save(firstBook);
		verify(bookRepository).save(secondBook);
		verify(sagaRepository).delete(saga);
	}

	@Test
	void should_delete_saga_and_detach_movies_from_it() {
		Movie firstMovie = CatalogFactory.validMovieWithSaga("m1", saga);
		Movie secondMovie = CatalogFactory.validMovieWithSaga("m2", saga);

		when(sagaRepository.findById(saga.getId())).thenReturn(Optional.of(saga));
		when(bookRepository.findAllBySaga(saga)).thenReturn(Set.of());
		when(movieRepository.findAllByMovieInfoSagaId(saga.getId())).thenReturn(Set.of(firstMovie, secondMovie));

		when(movieRepository.save(firstMovie)).thenReturn(firstMovie);
		when(movieRepository.save(secondMovie)).thenReturn(secondMovie);

		service.deleteSaga(saga.getId());

		assertNull(firstMovie.getMovieInfo().getSaga());
		assertNull(secondMovie.getMovieInfo().getSaga());
		verify(movieRepository).findAllByMovieInfoSagaId(saga.getId());
		verify(sagaRepository).delete(saga);
	}
}
