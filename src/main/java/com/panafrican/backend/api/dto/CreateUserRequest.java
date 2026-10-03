package com.panafrican.backend.api.dto;

import com.panafrican.backend.domain.UserRole;

public record CreateUserRequest(String username, String password, UserRole role, String countrySlug, String regionSlug) {}