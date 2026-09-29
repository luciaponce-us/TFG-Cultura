package com.tfg.cultura.api.catalog.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

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
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SagaService {

	private static final Logger logger = LoggerFactory.getLogger("catalogLogger");

	private final SagaRepository sagaRepository;
	private final BookRepository bookRepository;
	private final MovieRepository movieRepository;

	// CREATE

	public Saga createSaga(SagaRequest request) throws DuplicationException {
		String name = request.getName();
		boolean exists = sagaRepository.existsByName(name);
		if (exists) {
			throw new DuplicationException(logger, Map.of("name", "Ya existe una saga con el nombre: " + sanitize(name)));
		}

		Saga saga = Saga.builder().name(name).build();

		return sagaRepository.save(saga);
	}

	// READ

	public Saga findById(String id) throws NotFoundException {
		Optional<Saga> optionalSaga = sagaRepository.findById(id);
		if (optionalSaga.isPresent()) {
			return optionalSaga.get();
		} else {
			String errorMessage = "Saga no encontrada con ID: " + sanitize(id);
			throw new NotFoundException(errorMessage, logger);
		}
	}

	public Saga findByName(String name) throws NotFoundException {
		Saga saga = sagaRepository.findByName(name);
		if (saga != null) {
			return saga;
		} else {
			String errorMessage = "Saga no encontrada con nombre: " + sanitize(name);
			logger.error(errorMessage);
			throw new NotFoundException(errorMessage, logger);
		}
	}

	public List<Saga> findAll() {
		return sagaRepository.findAllByOrderByNameAsc();
	}

	// UPDATE

	public Saga updateSaga(String id, SagaRequest request) throws NotFoundException, DuplicationException {
		Saga existingSaga = findById(id);
		String name = request.getName();

		if (!existingSaga.getName().equals(name) && sagaRepository.existsByName(name)) {
			throw new DuplicationException(logger, Map.of("name", "Ya existe una saga con el nombre: " + sanitize(name)));
		}

		existingSaga.setName(name);
		return sagaRepository.save(existingSaga);
	}

	// DELETE

	public void deleteSaga(String id) throws NotFoundException {
		Saga existingSaga = findById(id);
		Iterable<Book> booksInSaga = bookRepository.findAllBySaga(existingSaga);
		booksInSaga.forEach(book -> {
			book.setSaga(null);
			bookRepository.save(book);
		});
		Iterable<Movie> moviesInSaga = movieRepository.findAllByMovieInfoSagaId(id);
		moviesInSaga.forEach(movie -> {
			movie.getMovieInfo().setSaga(null);
			movieRepository.save(movie);
		});
		sagaRepository.delete(existingSaga);
	}

}
