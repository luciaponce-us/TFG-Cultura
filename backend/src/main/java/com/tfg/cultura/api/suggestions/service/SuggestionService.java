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
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.enumerators.Role;
import com.tfg.cultura.api.users.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SuggestionService {

    private final SuggestionRepository repository;
    private final UserRepository userRepository;
    private final CustomUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger("suggestionsLogger");

    public SuggestionResponse create(SuggestionCreateRequest request)
            throws UnathenticatedException, UserNotFoundException {

        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();

        String authorId = currentUser.getId();

        Suggestion suggestion = Suggestion.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .type(request.getType())
                .authorId(authorId)
                .totalSupporters(0)
                .build();

        Suggestion savedSuggestion = repository.save(suggestion);
        logger.info("Sugerencia creada con ID {} por el usuario con ID {}", savedSuggestion.getId(), authorId);

        return toResponse(savedSuggestion);
    }

    public Page<SuggestionResponse> getAllWithFilters(SuggestionType type, String text, Boolean orderByCreationDate,
            Boolean supportedByAdmins,Boolean mySuggestions, int page, int size) {

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

        return suggestionPage.map(this::toResponse);
    }

    public SuggestionResponse getById(String id) throws SuggestionNotFoundException {
        Suggestion suggestion = findSuggestionById(id);
        return toResponse(suggestion);
    }

    public SuggestionResponse supportSuggestion(String id)
            throws SuggestionNotFoundException, SuggestionAlreadySupportedException, SelfSupportSuggestionException,
            UserNotFoundException, UnathenticatedException {
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        Suggestion suggestion = findSuggestionById(id);

        if (suggestion.getSupportersId().contains(currentUser.getId())) {
            logger.error("Error al apoyar la sugerencia: El usuario con ID {} ya apoya esta sugerencia",
                    currentUser.getId());
            throw new SuggestionAlreadySupportedException();
        }

        if (suggestion.getAuthorId().equals(currentUser.getId())) {
            logger.error("Error al apoyar la sugerencia: El usuario con ID {} ha intentado apoyar su propia sugerencia",
                    currentUser.getId());
            throw new SelfSupportSuggestionException();
        }
        List<String> supporters = new ArrayList<>(suggestion.getSupportersId());
        supporters.add(currentUser.getId());

        suggestion.setSupportersId(supporters);
        suggestion.setTotalSupporters(supporters.size());

        return toResponse(repository.save(suggestion));
    }

    public SuggestionResponse stopSupportingSuggestion(String id)
            throws SuggestionNotFoundException, SuggestionNotSupportedException {
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        Suggestion suggestion = findSuggestionById(id);
        List<String> supporters = suggestion.getSupportersId();

        if (!supporters.contains(currentUser.getId())) {
            logger.error(
                    "Error al dejar de apoyar la sugerencia: El usuario con ID {} no está apoyando esta sugerencia",
                    currentUser.getId());
            throw new SuggestionNotSupportedException();
        }

        supporters.remove(currentUser.getId());

        suggestion.setSupportersId(supporters);
        suggestion.setTotalSupporters(supporters.size());

        return toResponse(repository.save(suggestion));
    }

    public void delete(String id) throws SuggestionNotFoundException, UnathenticatedException, UnauthorizedException, UserNotFoundException {
        CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
        Suggestion suggestion = findSuggestionById(id);

        if (Role.getAdminRoles().contains(currentUser.getRole())) {
            logger.info("Sugerencia con ID {} eliminada por el usuario con ID {} con rol de administrador", suggestion.getId(), currentUser.getId());
            repository.delete(suggestion);
            return;
        }

        if (!suggestion.getAuthorId().equals(currentUser.getId())) {
            logger.error("Error al eliminar la sugerencia: El usuario con ID {} ha intentado eliminar una sugerencia que no es suya",
                    currentUser.getId());
            throw new UnauthorizedException("No tienes permiso para eliminar esta sugerencia");
        }

        repository.delete(suggestion);
        logger.info("Sugerencia con ID {} eliminada por el usuario con ID {}", suggestion.getId(), currentUser.getId());
    }

    // Helpers

    private User getAuthor(String id) throws UserNotFoundException {
        Optional<User> optionalAuthor = userRepository.findById(id);

        if (optionalAuthor.isEmpty()) {
            logger.warn("Error al convertir la sugerencia a respuesta: El autor de la sugerencia no existe");
            throw new UserNotFoundException("El autor de la sugerencia no existe");
        }

        return optionalAuthor.get();
    }

    private SuggestionResponse toResponse(Suggestion suggestion) throws UserNotFoundException {
        User author = getAuthor(suggestion.getAuthorId());

        UserResponse authorResponse = new UserResponse(author);

        List<UserResponse> supporters = getAllSupporters(suggestion);

        List<String> avatars = supporters.stream()
                .limit(3)
                .map(UserResponse::getAvatar)
                .toList();

        return new SuggestionResponse(suggestion, authorResponse, supporters, avatars);
    }

    private List<UserResponse> getAllSupporters(Suggestion suggestion) {
        return userRepository.findAllById(suggestion.getSupportersId()).stream()
                .map(UserResponse::new)
                .toList();
    }

    Suggestion findSuggestionById(String id) throws SuggestionNotFoundException {
        Optional<Suggestion> optionalSuggestion = repository.findById(id);

        if (optionalSuggestion.isEmpty()) {
            logger.warn("Error al buscar la sugerencia: No existe ninguna sugerencia con el id solicitado");
            throw new SuggestionNotFoundException(id);
        }

        return optionalSuggestion.get();
    }

}
