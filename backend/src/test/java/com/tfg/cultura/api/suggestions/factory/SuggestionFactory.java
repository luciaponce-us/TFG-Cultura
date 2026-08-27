package com.tfg.cultura.api.suggestions.factory;

import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionCreateRequest;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionResponse;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import java.util.ArrayList;
import java.util.List;

public class SuggestionFactory {

	public static Suggestion validSuggestion() {
		User author = UserFactory.validUser();
		User supporter = UserFactory.validUser2();
		List<User> supporters = new ArrayList<>(List.of(supporter));
		return Suggestion.builder().id("1").title("testTitle").description("testDescription")
				.type(SuggestionType.CATALOG).author(author).supporters(supporters).totalSupporters(supporters.size())
				.build();
	}

	public static SuggestionCreateRequest validSuggestionCreateRequest() {
		Suggestion suggestion = validSuggestion();
		return SuggestionCreateRequest.builder().title(suggestion.getTitle()).description(suggestion.getDescription())
				.type(suggestion.getType()).build();
	}

	public static SuggestionResponse validSuggestionResponse() {
		Suggestion suggestion = validSuggestion();
		return new SuggestionResponse(suggestion);
	}
}
