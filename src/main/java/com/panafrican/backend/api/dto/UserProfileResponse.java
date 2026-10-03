package com.panafrican.backend.api.dto;

import com.panafrican.backend.domain.UserRole;

public record UserProfileResponse(Long id, String username, UserRole role, String regionSlug, String countrySlug) {}