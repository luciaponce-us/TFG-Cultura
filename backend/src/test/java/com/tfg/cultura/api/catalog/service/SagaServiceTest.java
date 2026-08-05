package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.catalog.exception.saga.SagaAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.saga.SagaNotFoundException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.catalog.repository.SagaRepository;

@ExtendWith(MockitoExtension.class)
class SagaServiceTest {

    @Mock
    private SagaRepository sagaRepository;

    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private SagaService service;

    private Saga saga;

    @BeforeEach
    void setUp() {
        saga = Saga.builder()
                .id("1")
                .name("The Lord of the Rings")
                .build();
    }

    @Test
    void should_create_saga_when_name_is_available() {
        when(sagaRepository.existsByName("The Lord of the Rings")).thenReturn(false);
        when(sagaRepository.save(any(Saga.class))).thenReturn(saga);

        Saga result = service.createSaga("The Lord of the Rings");

        assertEquals(saga, result);
        verify(sagaRepository).existsByName("The Lord of the Rings");
        verify(sagaRepository).save(any(Saga.class));
    }

    @Test
    void should_throw_exception_when_saga_name_already_exists_on_create() {
        when(sagaRepository.existsByName("The Lord of the Rings")).thenReturn(true);

        assertThrows(SagaAlreadyExistsException.class,
                () -> service.createSaga("The Lord of the Rings"));

        verify(sagaRepository).existsByName("The Lord of the Rings");
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

        assertThrows(SagaNotFoundException.class, () -> service.findById("99"));
    }

    @Test
    void should_find_saga_by_name_when_present() {
        when(sagaRepository.findByName("The Lord of the Rings")).thenReturn(saga);

        Saga result = service.findByName("The Lord of the Rings");

        assertEquals(saga, result);
    }

    @Test
    void should_throw_exception_when_saga_name_does_not_exist() {
        when(sagaRepository.findByName("Unknown Saga")).thenReturn(null);

        assertThrows(SagaNotFoundException.class, () -> service.findByName("Unknown Saga"));
    }

    @Test
    void should_return_all_sagas_ordered_by_name() {
        List<Saga> expectedSagas = List.of(
                Saga.builder().id("2").name("B Saga").build(),
                Saga.builder().id("1").name("A Saga").build());
        when(sagaRepository.findAllByOrderByNameAsc()).thenReturn(expectedSagas);

        List<Saga> result = service.findAll();

        assertEquals(expectedSagas, result);
        verify(sagaRepository).findAllByOrderByNameAsc();
    }

    @Test
    void should_update_saga_when_name_is_available() {
        when(sagaRepository.findById("1")).thenReturn(Optional.of(saga));
        when(sagaRepository.existsByName("The Hobbit")).thenReturn(false);
        when(sagaRepository.save(any(Saga.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Saga result = service.updateSaga("1", "The Hobbit");

        assertEquals("The Hobbit", result.getName());
        verify(sagaRepository).save(any(Saga.class));
    }

    @Test
    void should_throw_exception_when_update_name_already_exists() {
        when(sagaRepository.findById("1")).thenReturn(Optional.of(saga));
        when(sagaRepository.existsByName("The Hobbit")).thenReturn(true);

        assertThrows(SagaAlreadyExistsException.class, () -> service.updateSaga("1", "The Hobbit"));

        verify(sagaRepository, never()).save(any(Saga.class));
    }

    @Test
    void should_delete_saga_and_detach_books_from_it() {
        Book firstBook = Book.builder().id("b1").saga(saga).build();
        Book secondBook = Book.builder().id("b2").saga(saga).build();

        when(sagaRepository.findById("1")).thenReturn(Optional.of(saga));
        when(bookRepository.findAllBySaga("1")).thenReturn(List.of(firstBook, secondBook));

        service.deleteSaga("1");

        assertNull(firstBook.getSaga());
        assertNull(secondBook.getSaga());
        verify(bookRepository).save(firstBook);
        verify(bookRepository).save(secondBook);
        verify(sagaRepository).delete(saga);
    }
}
