package com.tfg.cultura.api.catalog.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import com.tfg.cultura.api.catalog.exception.saga.SagaAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.saga.SagaNotFoundException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.catalog.repository.SagaRepository;
import java.util.List;
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

	// CREATE

	public Saga createSaga(String name) throws SagaAlreadyExistsException {
		boolean exists = sagaRepository.existsByName(name);
		if (exists) {
			logger.error("Ya existe una saga con el nombre: {}", name);
			throw new SagaAlreadyExistsException(name);
		}

		Saga saga = Saga.builder().name(name).build();

		return sagaRepository.save(saga);
	}

	// READ

	public Saga findById(String id) throws SagaNotFoundException {
		Optional<Saga> optionalSaga = sagaRepository.findById(id);
		if (optionalSaga.isPresent()) {
			return optionalSaga.get();
		} else {
			String errorMessage = "Saga no encontrada con ID: " + sanitize(id);
			logger.error(errorMessage);
			throw new SagaNotFoundException(errorMessage);
		}
	}

	public Saga findByName(String name) throws SagaNotFoundException {
		Saga saga = sagaRepository.findByName(name);
		if (saga != null) {
			return saga;
		} else {
			String errorMessage = "Saga no encontrada con nombre: " + sanitize(name);
			logger.error(errorMessage);
			throw new SagaNotFoundException(errorMessage);
		}
	}

	public List<Saga> findAll() {
		return sagaRepository.findAllByOrderByNameAsc();
	}

	// UPDATE

	public Saga updateSaga(String id, String name) throws SagaNotFoundException, SagaAlreadyExistsException {
		Saga existingSaga = findById(id);

		if (!existingSaga.getName().equals(name) && sagaRepository.existsByName(name)) {
			logger.error("Ya existe una saga con el nombre: {}", name);
			throw new SagaAlreadyExistsException(name);
		}

		existingSaga.setName(name);
		return sagaRepository.save(existingSaga);
	}

	// DELETE

	public void deleteSaga(String id) throws SagaNotFoundException {
		Saga existingSaga = findById(id);
		Iterable<Book> booksInSaga = bookRepository.findAllBySaga(id);
		booksInSaga.forEach(book -> {
			book.setSaga(null);
			bookRepository.save(book);
		});
		sagaRepository.delete(existingSaga);
	}

}
