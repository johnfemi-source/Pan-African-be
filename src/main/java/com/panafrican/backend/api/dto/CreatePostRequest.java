package com.panafrican.backend.api.dto;

import com.panafrican.backend.domain.PostCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "title is required")
        @Size(min = 3, max = 180, message = "title must be between 3 and 180 characters")
        String title,

        @NotBlank(message = "body is required")
        @Size(min = 1, max = 10000, message = "body must be between 1 and 10000 characters")
        String body,

        @NotNull(message = "category is required")
        PostCategory category,

        String countrySlug
) {
}
