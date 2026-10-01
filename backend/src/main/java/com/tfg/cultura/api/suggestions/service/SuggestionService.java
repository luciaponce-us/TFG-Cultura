package com.tfg.cultura.api.suggestions.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.dto.*;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.service.UserService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SuggestionService {

	private final SuggestionRepository repository;
	private final CustomUserDetailsService userDetailsService;
	private final UserService userService;
	private final AppProperties appProperties;

	private static final Logger logger = LoggerFactory.getLogger("suggestionsLogger");

	public SuggestionResponse create(SuggestionCreateRequest request)
			throws UnathenticatedException, NotFoundException {

		CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
		User author = userService.findUserById(currentUser.getId());

		Suggestion suggestion = Suggestion.builder().title(request.getTitle()).description(request.getDescription())
				.type(request.getType()).author(author).totalSupporters(0).build();

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

	public SuggestionResponse getById(String id) throws NotFoundException {
		Suggestion suggestion = findSuggestionById(id);
		return new SuggestionResponse(suggestion);
	}

	public SuggestionResponse toggleSupport(String id)
			throws ValidationException, NotFoundException, UnathenticatedException {
		CustomUserDetails currentUserDetails = userDetailsService.getCurrentUserDetails();
		User currentUser = userService.findUserById(currentUserDetails.getId());
		Suggestion suggestion = findSuggestionById(id);
		List<User> supporters = new ArrayList<>(suggestion.getSupporters());
		boolean isSupported = supporters.stream().map(User::getId).toList().contains(currentUser.getId());

		if (isSupported) {
			supporters.removeIf(supporter -> Objects.equals(supporter.getId(), currentUser.getId()));
		} else {
			boolean isAuthor = suggestion.getAuthor().getId().equals(currentUser.getId());
			if (isAuthor) {
				String errorMessage = "El usuario con ID " + currentUser.getId()
						+ " ha intentado apoyar su propia sugerencia";
				throw new ValidationException(logger, Map.of("supporters", errorMessage));
			}

			supporters.add(currentUser);
		}

		suggestion.setSupporters(supporters);
		suggestion.setTotalSupporters(supporters.size());

		Suggestion response = repository.save(suggestion);

		return new SuggestionResponse(response);
	}

	public void delete(String id) throws NotFoundException, UnathenticatedException, UnauthorizedException {
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
			throw new UnauthorizedException("Error al eliminar la sugerencia: El usuario con ID " + currentUser.getId()
					+ " ha intentado eliminar una sugerencia que no es suya");
		}

		repository.delete(suggestion);
		logger.info("Sugerencia con ID {} eliminada por el usuario con ID {}", suggestion.getId(), currentUser.getId());
	}

	// Helpers

	Suggestion findSuggestionById(String id) throws NotFoundException {
		Optional<Suggestion> optionalSuggestion = repository.findById(id);

		if (optionalSuggestion.isEmpty()) {
			String errorMessage = "No existe ninguna sugerencia con el id solicitado: " + sanitize(id);
			throw new NotFoundException(errorMessage, logger);
		}

		return optionalSuggestion.get();
	}

}
