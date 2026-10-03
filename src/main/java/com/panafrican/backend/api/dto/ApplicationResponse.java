package com.panafrican.backend.api.dto;

import com.panafrican.backend.domain.ApplicationStatus;

import java.time.Instant;

public record ApplicationResponse(
        String id,
        String fullName,
        String email,
        String phone,
        String countrySlug,
        String motivation,
        ApplicationStatus status,
        Instant createdAt,
        Instant updatedAt
) {
}
