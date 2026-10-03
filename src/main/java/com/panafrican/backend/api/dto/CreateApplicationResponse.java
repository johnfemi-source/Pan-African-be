package com.panafrican.backend.api.dto;

import com.panafrican.backend.domain.ApplicationStatus;

public record CreateApplicationResponse(String id, ApplicationStatus status) {
}
