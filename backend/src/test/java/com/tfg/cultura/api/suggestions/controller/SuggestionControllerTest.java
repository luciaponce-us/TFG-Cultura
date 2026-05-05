package com.tfg.cultura.api.suggestions.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.suggestions.exception.SelfSupportSuggestionException;
import com.tfg.cultura.api.suggestions.exception.SuggestionAlreadySupportedException;
import com.tfg.cultura.api.suggestions.exception.SuggestionExceptionHandler;
import com.tfg.cultura.api.suggestions.exception.SuggestionNotFoundException;
import com.tfg.cultura.api.suggestions.exception.SuggestionNotSupportedException;
import com.tfg.cultura.api.suggestions.factory.SuggestionFactory;
import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionCreateRequest;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionResponse;
import com.tfg.cultura.api.suggestions.service.SuggestionService;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.exception.UsersExceptionHandler;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.utils.BaseControllerTest;

class SuggestionControllerTest extends BaseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private SuggestionService service;

    private static final String BASE_URL = "/api/suggestions";
    private static final String CREATE_URL = BASE_URL + "/create";
    private static final String SUPPORT_URL = BASE_URL + "/%s/support";
    private static final String STOP_SUPPORT_URL = SUPPORT_URL + "/stop";

    private Suggestion suggestion;
    private SuggestionCreateRequest request;
    private SuggestionResponse response;
    private UserResponse author;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SuggestionController controller = new SuggestionController(service);
        mockMvc = buildMockMvc(controller, UsersExceptionHandler.class, SuggestionExceptionHandler.class);

        initTestData();
    }

    private void initTestData() {
        author = UserFactory.validUserResponse();
        suggestion = SuggestionFactory.validSuggestion();
        request = SuggestionFactory.validSuggestionCreateRequest();
        response = SuggestionFactory.validSuggestionResponse();
    }

    // CREATE SUGGESTIONS

    @Test
    void create_success() throws Exception {
        when(service.create(any(SuggestionCreateRequest.class))).thenReturn(response);

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(suggestion.getTitle()))
                .andExpect(jsonPath("$.description").value(suggestion.getDescription()))
                .andExpect(jsonPath("$.type").value(suggestion.getType().toString()))
                .andExpect(jsonPath("$.author.username").value(author.getUsername()))
                .andExpect(jsonPath("$.someSupportersAvatars").isArray());
    }

    @Test
    void create_fail_invalid_request() throws Exception {
        request.setTitle("");

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());

    }

    @Test
    void create_fail_unauthenticated() throws Exception {
        when(service.create(any(SuggestionCreateRequest.class)))
                .thenThrow(new UnathenticatedException("No se ha podido obtener la autenticación del usuario"));

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void create_fail_author_does_not_exist() throws Exception {
        when(service.create(any(SuggestionCreateRequest.class)))
                .thenThrow(new UserNotFoundException("El usuario logeado no existe"));

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }

    // GET ALL SUGGESTIONS

    @Test
    void getAll_success() throws Exception {
        when(service.getAll()).thenReturn(List.of(response));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(suggestion.getTitle()));
    }

    // SUPPORT SUGGESTION

    @Test
    void supportSuggestion_success() throws Exception {
        when(service.supportSuggestion(suggestion.getId())).thenReturn(response);
        
        mockMvc.perform(put(String.format(SUPPORT_URL, suggestion.getId())))
            .andExpect(status().isOk());
    }

    @Test
    void supportSuggestion_suggestionNotFound() throws Exception {
        String id = suggestion.getId();
        SuggestionNotFoundException ex = new SuggestionNotFoundException(id);
        when(service.supportSuggestion(any())).thenThrow(ex);

        mockMvc.perform(put(String.format(SUPPORT_URL, id)))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void supportSuggestion_suggestionAlreadySupported() throws Exception {
        SuggestionAlreadySupportedException ex = new SuggestionAlreadySupportedException();
        when(service.supportSuggestion(any())).thenThrow(ex);

        mockMvc.perform(put(String.format(SUPPORT_URL, suggestion.getId())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void supportSuggestion_selfSupport() throws Exception {
        SelfSupportSuggestionException ex = new SelfSupportSuggestionException();
        when(service.supportSuggestion(any())).thenThrow(ex);

        mockMvc.perform(put(String.format(SUPPORT_URL, suggestion.getId())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").exists());
    }

    // STOP SUPPORTING SUGGESTION

    @Test
    void stopSupportSuggestion_success() throws Exception {
        when(service.stopSupportingSuggestion(suggestion.getId())).thenReturn(response);
        
        mockMvc.perform(put(String.format(STOP_SUPPORT_URL, suggestion.getId())))
            .andExpect(status().isOk());
    }

    @Test
    void stopSupportSuggestion_suggestionNotFound() throws Exception {
        String id = suggestion.getId();
        SuggestionNotFoundException ex = new SuggestionNotFoundException(id);
        when(service.stopSupportingSuggestion(any())).thenThrow(ex);
        
        mockMvc.perform(put(String.format(STOP_SUPPORT_URL, id)))
            .andExpect(status().isNotFound());
    }

    @Test
    void stopSupportSuggestion_suggestionNotSupported() throws Exception {
        String id = suggestion.getId();
        SuggestionNotSupportedException ex = new SuggestionNotSupportedException();
        when(service.stopSupportingSuggestion(any())).thenThrow(ex);
        
        mockMvc.perform(put(String.format(STOP_SUPPORT_URL, id)))
            .andExpect(status().isBadRequest());
    }    

}
