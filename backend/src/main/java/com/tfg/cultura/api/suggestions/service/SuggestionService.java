package com.tfg.cultura.api.suggestions.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.suggestions.exception.*;
import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.dto.*;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;

import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.service.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final SuggestionRepository repository;
    private final CustomUserDetailsService userDetailsService;
    private final UserService userService;
    private final AppProperties appProperties;

    private static final Logger logger = LoggerFactory.getLogger("suggestionsLogger");

    public SuggestionResponse create(SuggestionCreateRequest request)
            throws UnathenticatedException, UserNotFoundException {

        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        User author = userService.findUserById(currentUser.getId());

        Suggestion suggestion = Suggestion.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .author(author)
                .totalSupporters(0)
                .build();

        Suggestion savedSuggestion = repository.save(suggestion);
        logger.info("Sugerencia creada con ID {} por el usuario {}", savedSuggestion.getId(), author.getUsername());

        return new SuggestionResponse(savedSuggestion);
    }

    public Page<SuggestionResponse> getAllWithFilters(SuggestionType type, String text, Boolean orderByCreationDate,
            Boolean supportedByAdmins, Boolean mySuggestions, int page, int size) {

        Sort sort = Sort.by("totalSupporters").descending();
        boolean orderByCreationDateValue = Boolean.TRUE.equals(orderByCreationDate);
        if (orderByCreationDateValue) {
            sort = Sort.by("createdAt").descending();
        }

        PageRequest pageable = PageRequest.of(page, size, sort);
        Page<Suggestion> suggestionPage;

        if (type != null || text != null || supportedByAdmins != null || mySuggestions != null) {
            suggestionPage = repository.findAllWithFilters(type, text, supportedByAdmins, mySuggestions, pageable);
        } else {
            suggestionPage = repository.findAll(pageable);
        }

        return suggestionPage.map(SuggestionResponse::new);
    }

    public SuggestionResponse getById(String id) throws SuggestionNotFoundException {
        Suggestion suggestion = findSuggestionById(id);
        return new SuggestionResponse(suggestion);
    }

    public SuggestionResponse toggleSupport(String id) throws SuggestionNotFoundException,
            SelfSupportSuggestionException, UserNotFoundException, UnathenticatedException {
        CustomUserDetails currentUserDetails = userDetailsService.getCurrentUserDetails();
        User currentUser = userService.findUserById(currentUserDetails.getId());
        Suggestion suggestion = findSuggestionById(id);
        List<User> supporters = new ArrayList<>(suggestion.getSupporters());
        boolean isSupported = supporters.stream().map(User::getId).toList().contains(currentUser.getId());

        if (isSupported) {
            supporters.remove(currentUser);
        } else {
            boolean isAuthor = suggestion.getAuthor().getId().equals(currentUser.getId());
            if (isAuthor) {
                logger.error(
                        "Error al apoyar la sugerencia: El usuario con ID {} ha intentado apoyar su propia sugerencia",
                        currentUser.getId());
                throw new SelfSupportSuggestionException();
            }

            supporters.add(currentUser);
        }

        suggestion.setSupporters(supporters);
        suggestion.setTotalSupporters(supporters.size());

        return new SuggestionResponse(repository.save(suggestion));
    }

    public void delete(String id)
            throws SuggestionNotFoundException, UnathenticatedException, UnauthorizedException, UserNotFoundException {
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        Suggestion suggestion = findSuggestionById(id);

        if (appProperties.adminRoles().contains(currentUser.getRole())) {
            logger.info("Sugerencia con ID {} eliminada por el usuario con ID {} con rol de administrador",
                    suggestion.getId(), currentUser.getId());
            repository.delete(suggestion);
            return;
        }

        boolean isAuthor = suggestion.getAuthor().getId().equals(currentUser.getId());
        if (!isAuthor) {
            logger.error(
                    "Error al eliminar la sugerencia: El usuario con ID {} ha intentado eliminar una sugerencia que no es suya",
                    currentUser.getId());
            throw new UnauthorizedException("No tienes permiso para eliminar esta sugerencia");
        }

        repository.delete(suggestion);
        logger.info("Sugerencia con ID {} eliminada por el usuario con ID {}", suggestion.getId(), currentUser.getId());
    }

    // Helpers

    Suggestion findSuggestionById(String id) throws SuggestionNotFoundException {
        Optional<Suggestion> optionalSuggestion = repository.findById(id);

        if (optionalSuggestion.isEmpty()) {
            logger.error("Error al buscar la sugerencia: No existe ninguna sugerencia con el id solicitado");
            throw new SuggestionNotFoundException(id);
        }

        return optionalSuggestion.get();
    }

}
