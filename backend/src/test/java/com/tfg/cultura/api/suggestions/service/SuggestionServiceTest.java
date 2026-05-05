package com.tfg.cultura.api.suggestions.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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

import com.tfg.cultura.api.suggestions.exception.*;
import com.tfg.cultura.api.suggestions.factory.SuggestionFactory;
import com.tfg.cultura.api.suggestions.model.*;
import com.tfg.cultura.api.suggestions.model.dto.*;
import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;

import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class SuggestionServiceTest {

    @Mock
    private SuggestionRepository repository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private SuggestionService service;

    private SuggestionCreateRequest request;
    private Suggestion suggestion;
    private User user;
    
    @BeforeEach
    void setUp() {
        user = UserFactory.validUser();
        suggestion = SuggestionFactory.validSuggestion();
        request = SuggestionFactory.validSuggestionCreateRequest();
    }

    private void mockCurrentUser() {
        CustomUserDetails currentUser = UserFactory.mockAuthContext();
        when(userDetailsService.getCurrentUserDetails()).thenReturn(currentUser);
    }

    @Test
    void should_return_suggestion_response_when_create_suggestion() {
        mockCurrentUser();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(suggestion);

        SuggestionResponse response = service.create(request);

        assertNotNull(response);
        assertEquals(suggestion.getTitle(), response.getTitle());
        assertEquals(suggestion.getSupportersId().size(), response.getTotalSupporters());
    }

    @Test
    void getAll_should_return_sorted_suggestions() throws UserNotFoundException {
        when(repository.findAll()).thenReturn(List.of(suggestion));
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        List<SuggestionResponse> responses = service.getAll();

        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(suggestion.getTitle(), responses.get(0).getTitle());
        assertEquals(suggestion.getSupportersId().size(), responses.get(0).getTotalSupporters());
    }

    @Test
    void getAll_should_return_empty_list_if_no_suggestions() {
        when(repository.findAll()).thenReturn(List.of());

        List<SuggestionResponse> responses = service.getAll();

        assertNotNull(responses);
        assertEquals(0, responses.size());
    }

    @Test
    void toResponse_should_throw_UserNotFoundException_if_author_does_not_exists() {
        when(repository.findAll()).thenReturn(List.of(suggestion));
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        try {
            service.getAll();
        } catch (Exception e) {
            assertEquals(e.getClass(), UserNotFoundException.class);
        }
    }

    // SUPPORT SUGGESTIONS

    @Test
    void supportSuggestion_success() throws Exception {
        mockCurrentUser();
        suggestion.setAuthorId("otherAuthorId");
        suggestion.setSupportersId(new ArrayList<>());

        when(repository.findById(any())).thenReturn(Optional.of(suggestion));
        when(repository.save(any())).thenReturn(suggestion);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.findAllById(any())).thenReturn(List.of(user));

        SuggestionResponse response = service.supportSuggestion(suggestion.getId());
        List<String> supportersUsernames = response.getSupporters().stream().map(UserResponse::getUsername).toList();

        assertNotNull(response);
        assertEquals(1, response.getTotalSupporters());
        assertTrue(supportersUsernames.contains(user.getUsername()));
        verify(repository).save(suggestion);
        assertTrue(suggestion.getSupportersId().contains(user.getId()));
    }

    @Test
    void supportSuggestion_alreadySupported() {
        mockCurrentUser();
        suggestion.getSupportersId().add(user.getId());

        when(repository.findById(any())).thenReturn(Optional.of(suggestion));

        try {
            service.supportSuggestion(suggestion.getId());
        } catch (Exception e) {
            assertEquals(e.getClass(), SuggestionAlreadySupportedException.class);
            verify(repository, never()).save(any());
        }
    }

    @Test
    void supportSuggestion_selfSupport() {
        mockCurrentUser();
        // El autor de suggestion ya es currentUser
        when(repository.findById(any())).thenReturn(Optional.of(suggestion));

        try {
            service.supportSuggestion(suggestion.getId());
        } catch (Exception e) {
            assertEquals(e.getClass(), SelfSupportSuggestionException.class);
            verify(repository, never()).save(any());
        }
    }

    @Test
    void supportSuggestion_notFound() {
        mockCurrentUser();

        when(repository.findById(any())).thenReturn(Optional.empty());

        try {
            service.supportSuggestion(suggestion.getId());
        } catch (Exception e) {
            assertEquals(e.getClass(), SuggestionNotFoundException.class);
            verify(repository, never()).save(any());
        }
    }

    // STOP SUPPORTING SUGGESTIONS

    @Test
    void stopSupportingSuggestion_success() {
        mockCurrentUser();
        List<String> supportersId = new ArrayList<>(List.of(user.getId()));
        suggestion.setSupportersId(supportersId);
        when(repository.findById(any())).thenReturn(Optional.of(suggestion));
        when(repository.save(any())).thenReturn(suggestion);
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.findAllById(any())).thenReturn(List.of());

        SuggestionResponse response = service.stopSupportingSuggestion(suggestion.getId());

        verify(repository).save(suggestion);
        assertNotNull(response);
        assertEquals(0, response.getTotalSupporters());
        assertEquals(List.of(), response.getSupporters());
    }

    @Test
    void stopSupportingSuggestion_notFound() {
        mockCurrentUser();

        when(repository.findById(any())).thenReturn(Optional.empty());

        try {
            service.stopSupportingSuggestion(suggestion.getId());
        } catch (Exception e) {
            assertEquals(e.getClass(), SuggestionNotFoundException.class);
            verify(repository, never()).save(any());
        }
    }

    @Test
    void stopSupportingSuggestion_suggestionNotSupported() {
        mockCurrentUser();
        
        when(repository.findById(any())).thenReturn(Optional.of(suggestion));

        try {
            service.stopSupportingSuggestion(suggestion.getId());
        } catch (Exception e) {
            assertEquals(e.getClass(), SuggestionNotSupportedException.class);
            verify(repository, never()).save(any());
        }
    }

}
