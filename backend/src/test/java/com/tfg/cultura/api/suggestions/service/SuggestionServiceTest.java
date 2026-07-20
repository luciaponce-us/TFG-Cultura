package com.tfg.cultura.api.suggestions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.suggestions.exception.*;
import com.tfg.cultura.api.suggestions.factory.SuggestionFactory;
import com.tfg.cultura.api.suggestions.model.*;
import com.tfg.cultura.api.suggestions.model.dto.*;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;

import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.enumerators.Role;
import com.tfg.cultura.api.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceTest {

    @Mock
    private SuggestionRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AppProperties appProperties;

    @InjectMocks
    private SuggestionService service;

    private SuggestionCreateRequest request;
    private Suggestion suggestion;
    private User user;
    private User currentUser;

    @BeforeEach
    void setUp() {
        user = UserFactory.validUser();
        currentUser = UserFactory.validCurrentUserWithRole(Role.SOCIO);
        suggestion = SuggestionFactory.validSuggestion();
        request = SuggestionFactory.validSuggestionCreateRequest();
    }

    private void mockAuthContext() {
        CustomUserDetails currentUserDetails = UserFactory.mockAuthContext();
        when(userDetailsService.getCurrentUserDetails()).thenReturn(currentUserDetails);
    }

    private void assertSuggestionResponse(SuggestionResponse response) {
        assertNotNull(response);
        assertEquals(suggestion.getTitle(), response.getTitle());
        assertEquals(suggestion.getDescription(), response.getDescription());
        assertEquals(suggestion.getType(), response.getType());
        assertEquals(suggestion.getTotalSupporters(), response.getTotalSupporters());
    }

    // CREATE SUGGESTION

    @Test
    void should_return_suggestion_response_when_create_suggestion() {
        mockAuthContext();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(suggestion);

        SuggestionResponse response = service.create(request);

        assertSuggestionResponse(response);
    }

    // GET ALL SUGGESTIONS WITH FILTERS

    private Page<Suggestion> suggestionPage(int page, int size) {
        return new PageImpl<>(
                List.of(suggestion),
                PageRequest.of(page, size),
                1);
    }

    private Page<Suggestion> emptySuggestionPage(int page, int size) {
        return new PageImpl<>(
                List.of(),
                PageRequest.of(page, size),
                0);
    }

    @Test
    void getAllWithFilters_should_return_page_when_no_filters() throws UserNotFoundException {
        when(repository.findAll(any(Pageable.class)))
                .thenReturn(suggestionPage(0, 10));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        Page<SuggestionResponse> responses = service.getAllWithFilters(null, null, false, null, null, 0, 10);

        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertEquals(1, responses.getContent().size());
        assertSuggestionResponse(responses.getContent().get(0));
    }

    @Test
    void getAllWithFilters_should_return_empty_page_if_no_suggestions() {
        when(repository.findAll(any(Pageable.class)))
                .thenReturn(emptySuggestionPage(0, 10));

        Page<SuggestionResponse> responses = service.getAllWithFilters(null, null, false, null, null, 0, 10);

        assertNotNull(responses);
        assertEquals(0, responses.getTotalElements());
        assertEquals(0, responses.getContent().size());
    }

    @Test
    void getAllWithFilters_should_use_repository_filters() throws UserNotFoundException {
        when(repository.findAllWithFilters(any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(suggestionPage(0, 5));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        Page<SuggestionResponse> responses = service.getAllWithFilters(
                SuggestionType.EVENT,
                "query",
                Boolean.FALSE,
                Boolean.TRUE,
                Boolean.TRUE,
                0,
                5);

        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertEquals(1, responses.getContent().size());
        assertSuggestionResponse(responses.getContent().get(0));
    }

    @Test
    void toResponse_should_throw_UserNotFoundException_if_author_does_not_exists() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<Suggestion> suggestions = new PageImpl<>(List.of(suggestion), pageable, 1);
        when(repository.findAll(any(Pageable.class))).thenReturn(suggestions);
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> service.getAllWithFilters(null, null, false, null, null, 0, 10));
    }

    // GET SUGGESTION BY ID

    @Test
    void getById_should_return_suggestion_response() throws SuggestionNotFoundException {
        when(repository.findById(any())).thenReturn(Optional.of(suggestion));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        SuggestionResponse response = service.getById(suggestion.getId());
        assertSuggestionResponse(response);
    }

    @Test
    void getById_should_throw_SuggestionNotFoundException_if_suggestion_does_not_exists() {
        when(repository.findById(any())).thenReturn(Optional.empty());
        assertThrows(
                SuggestionNotFoundException.class,
                () -> service.getById(suggestion.getId()));
    }

    // SUPPORT SUGGESTIONS

    @Test
    void toggleSupport_when_sugestion_not_supported_success() throws Exception {
        mockAuthContext();
        suggestion.setAuthorId("otherAuthorId");
        suggestion.setSupportersId(new ArrayList<>());
        suggestion.setTotalSupporters(0);

        when(repository.findById(any())).thenReturn(Optional.of(suggestion));
        when(userRepository.findAllById(anyList()))
                .thenReturn(List.of(currentUser));
        when(repository.save(any())).thenReturn(suggestion);
        when(userRepository.findById("otherAuthorId"))
                .thenReturn(Optional.of(user));
        assertEquals(0, suggestion.getTotalSupporters());

        SuggestionResponse response = service.toggleSupport(suggestion.getId());

        assertNotNull(response);
        assertEquals(1, response.getTotalSupporters());
        assertEquals(1, response.getSupporters().size());
        verify(repository).save(suggestion);
        assertTrue(suggestion.getSupportersId().contains(currentUser.getId()));
    }

    @Test
    void toggleSupport_when_sugestion_supported_success() throws Exception {
        mockAuthContext();
        suggestion.setAuthorId("otherAuthorId");
        assertNotEquals(suggestion.getAuthorId(), currentUser.getId());
        suggestion.setSupportersId(new ArrayList<>(List.of(currentUser.getId())));

        when(userRepository.findById("otherAuthorId")).thenReturn(Optional.of(user));

        when(repository.findById(any())).thenReturn(Optional.of(suggestion));

        when(repository.save(any(Suggestion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SuggestionResponse response = service.toggleSupport(suggestion.getId());
        List<UserResponse> supporters = response.getSupporters();

        assertNotNull(response);
        assertEquals(0, response.getTotalSupporters());
        assertFalse(supporters.stream().map(UserResponse::getUsername).toList().contains(currentUser.getUsername()));
        verify(repository).save(suggestion);
        assertFalse(suggestion.getSupportersId().contains(currentUser.getId()));
    }

    @Test
    void toggleSupport_selfSupport() {
        mockAuthContext();
        suggestion.setAuthorId(currentUser.getId());
        when(repository.findById(any())).thenReturn(Optional.of(suggestion));

        assertThrows(
                SelfSupportSuggestionException.class,
                () -> service.toggleSupport(suggestion.getId()));
        verify(repository, never()).save(any());
    }

    @Test
    void toggleSupport_notFound() {
        mockAuthContext();

        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                SuggestionNotFoundException.class,
                () -> service.toggleSupport(suggestion.getId()));

        verify(repository, never()).save(any());
    }

    // DELETE SUGGESTION

    @Test
    void deleteSuggestion_success() throws Exception {
        mockAuthContext();
        suggestion.setAuthorId(currentUser.getId());

        when(repository.findById(any())).thenReturn(Optional.of(suggestion));
        doNothing().when(repository).delete(any());

        service.delete(suggestion.getId());
        verify(repository).delete(suggestion);
    }

    @Test
    void deleteSuggestion_unauthorized() throws Exception {
        mockAuthContext();
        suggestion.setAuthorId("otherAuthorId");

        when(repository.findById(any())).thenReturn(Optional.of(suggestion));

        assertThrows(
                UnauthorizedException.class,
                () -> service.delete(suggestion.getId()));
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteSuggestion_notFound() throws Exception {
        mockAuthContext();
        when(repository.findById(any())).thenReturn(Optional.empty());

        assertThrows(
                SuggestionNotFoundException.class,
                () -> service.delete(suggestion.getId()));
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteSuggestion_unauthenticated() throws Exception {
        when(userDetailsService.getCurrentUserDetails())
                .thenThrow(new UnathenticatedException("User not authenticated"));
        assertThrows(
                UnathenticatedException.class,
                () -> service.delete(suggestion.getId()));

        verify(repository, never()).delete(any());
    }

}
