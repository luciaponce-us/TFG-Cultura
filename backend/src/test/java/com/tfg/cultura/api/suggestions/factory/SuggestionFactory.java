package com.tfg.cultura.api.suggestions.factory;

import java.util.ArrayList;
import java.util.List;

import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionCreateRequest;
import com.tfg.cultura.api.suggestions.model.dto.SuggestionResponse;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserResponse;

public class SuggestionFactory {
    
    public static Suggestion validSuggestion() {
        User author = UserFactory.validUser();
        return Suggestion.builder()
            .title("testTitle")
            .description("testDescription")
            .type(SuggestionType.CATALOG)
            .authorId(author.getId())
            .supportersId(new ArrayList<>(List.of("2","3")))
            .build();
    }

    public static SuggestionCreateRequest validSuggestionCreateRequest() {
        Suggestion suggestion = validSuggestion();
        return SuggestionCreateRequest.builder()
            .title(suggestion.getTitle())
            .description(suggestion.getDescription())
            .type(suggestion.getType())
            .build();
    }

    public static SuggestionResponse validSuggestionResponse() {
        Suggestion suggestion = validSuggestion();
        UserResponse author = UserFactory.validUserResponse();
        List<String> avatarsList = List.of("avatar1.png","avatar2.png");
        List<UserResponse> supporters = suggestion.getSupportersId().stream()
                .map(UserFactory::validUserResponseWithId)
                .toList();
        return new SuggestionResponse(suggestion, author, supporters, avatarsList);
    }
}
