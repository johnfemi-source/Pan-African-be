package com.panafrican.backend.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ApplicationRequest(
        @NotBlank(message = "fullName is required")
        @Size(min = 2, max = 160, message = "fullName must be between 2 and 160 characters")
        String fullName,

        @NotBlank(message = "email is required")
        @Email(message = "email must be valid")
        @Size(max = 254, message = "email must be 254 characters or fewer")
        String email,

        @Size(max = 40, message = "phone must be 40 characters or fewer")
        String phone,

        @NotBlank(message = "countrySlug is required")
        String countrySlug,

        @NotBlank(message = "motivation is required")
        @Size(min = 20, max = 5000, message = "motivation must be between 20 and 5000 characters")
        String motivation
) {
}
