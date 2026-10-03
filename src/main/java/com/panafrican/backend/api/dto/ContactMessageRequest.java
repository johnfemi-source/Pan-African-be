package com.panafrican.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactMessageRequest(
        @NotBlank(message = "name is required")
        @Size(min = 2, max = 160, message = "name must be between 2 and 160 characters")
        String name,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 254, message = "email must be 254 characters or fewer")
        String email,

        @NotBlank(message = "body is required")
        @Size(min = 10, max = 5000, message = "body must be between 10 and 5000 characters")
        String body
) {
}
