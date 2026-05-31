package com.tfg.cultura.api.suggestions.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import com.tfg.cultura.api.users.model.dto.UserResponse;

import lombok.Getter;

@Getter
public class SuggestionResponse {
    private String id;
    private String title;
    private String description;
    private SuggestionType type;
    private UserResponse author;
    private List<String> someSupportersAvatars;
    private List<UserResponse> supporters;
    private int totalSupporters;
    private LocalDateTime createdAt;

    public SuggestionResponse(Suggestion suggestion, UserResponse author, List<UserResponse> supporters,
            List<String> someSupportersAvatars) {
        this.id = suggestion.getId();
        this.title = suggestion.getTitle();
        this.description = suggestion.getDescription();
        this.type = suggestion.getType();
        this.author = author;
        this.supporters = supporters;
        this.someSupportersAvatars = someSupportersAvatars;
        this.totalSupporters = suggestion.getTotalSupporters();
        this.createdAt = suggestion.getCreatedAt();
    }
}
