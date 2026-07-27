package com.tfg.cultura.api.suggestions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import com.tfg.cultura.api.users.service.UserService;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceTest {

    @Mock
    private SuggestionRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserService userService;

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

    private void mockSuggestionById(Suggestion suggestion) {
        if (suggestion == null) {
            when(repository.findById(anyString())).thenReturn(Optional.empty());
        } else {
            when(repository.findById(anyString())).thenReturn(Optional.of(suggestion));
        }
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
        when(userService.findUserById(any())).thenReturn(user);
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

    // GET SUGGESTION BY ID

    @Test
    void getById_should_return_suggestion_response() throws SuggestionNotFoundException {
        mockSuggestionById(suggestion);
        SuggestionResponse response = service.getById(suggestion.getId());
        assertSuggestionResponse(response);
    }

    @Test
    void getById_should_throw_SuggestionNotFoundException_if_suggestion_does_not_exists() {
        mockSuggestionById(null);
        assertThrows(
                SuggestionNotFoundException.class,
                () -> service.getById("someSuggestionId"));
    }

    // SUPPORT SUGGESTIONS

    @Test
    void toggleSupport_when_sugestion_not_supported_success() throws Exception {
        mockAuthContext();
        when(userService.findUserById(currentUser.getId())).thenReturn(currentUser);
        suggestion.setAuthor(UserFactory.validUser2());
        suggestion.setSupporters(new ArrayList<>());
        suggestion.setTotalSupporters(0);

        mockSuggestionById(suggestion);
        when(repository.save(any())).thenReturn(suggestion);
        assertEquals(0, suggestion.getTotalSupporters());

        SuggestionResponse response = service.toggleSupport(suggestion.getId());

        assertNotNull(response);
        assertEquals(1, response.getTotalSupporters());
        assertEquals(1, response.getSupporters().size());
        verify(repository).save(suggestion);
        assertTrue(suggestion.getSupporters().contains(currentUser));
    }

    @Test
    void toggleSupport_when_sugestion_supported_success() throws Exception {
        mockAuthContext();
        when(userService.findUserById(currentUser.getId())).thenReturn(currentUser);
        suggestion.setAuthor(UserFactory.validUser2());
        assertNotEquals(suggestion.getAuthor().getId(), currentUser.getId());
        suggestion.setSupporters(new ArrayList<>(List.of(currentUser)));
        suggestion.setTotalSupporters(1);

        mockSuggestionById(suggestion);

        when(repository.save(any(Suggestion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SuggestionResponse response = service.toggleSupport(suggestion.getId());
        List<UserResponse> supporters = response.getSupporters();

        assertNotNull(response);
        assertEquals(0, response.getTotalSupporters());
        assertFalse(supporters.stream().map(UserResponse::getUsername).toList().contains(currentUser.getUsername()));
        verify(repository).save(suggestion);
        assertFalse(suggestion.getSupporters().contains(currentUser));
    }

    @Test
    void toggleSupport_selfSupport() {
        mockAuthContext();
        when(userService.findUserById(currentUser.getId())).thenReturn(currentUser);
        suggestion.setAuthor(currentUser);
        mockSuggestionById(suggestion);

        assertThrows(
                SelfSupportSuggestionException.class,
                () -> service.toggleSupport("someSuggestionId"));
        verify(repository, never()).save(any());
    }

    @Test
    void toggleSupport_notFound() {
        mockAuthContext();
        mockSuggestionById(null);

        assertThrows(
                SuggestionNotFoundException.class,
                () -> service.toggleSupport("someSuggestionId"));

        verify(repository, never()).save(any());
    }

    // DELETE SUGGESTION

    @Test
    void deleteSuggestion_success() throws Exception {
        mockAuthContext();
        suggestion.setAuthor(currentUser);

        mockSuggestionById(suggestion);
        doNothing().when(repository).delete(any());

        service.delete(suggestion.getId());
        verify(repository).delete(suggestion);
    }

    @Test
    void deleteSuggestion_unauthorized() {
        mockAuthContext();
        suggestion.setAuthor(UserFactory.validUser2());

        mockSuggestionById(suggestion);

        assertThrows(
                UnauthorizedException.class,
                () -> service.delete("someSuggestionId"));
        verify(repository, never()).delete(any());
    }

    @Test
    void deleteSuggestion_notFound() {
        mockAuthContext();
        mockSuggestionById(null);

        assertThrows(
                SuggestionNotFoundException.class,
                () -> service.delete("someSuggestionId"));

        verify(repository, never()).delete(any());
    }

    @Test
    void deleteSuggestion_unauthenticated() throws Exception {
        when(userDetailsService.getCurrentUserDetails())
                .thenThrow(new UnathenticatedException("User not authenticated"));

        assertThrows(
                UnathenticatedException.class,
                () -> service.delete("someSuggestionId"));

        verify(repository, never()).delete(any());
    }

}
