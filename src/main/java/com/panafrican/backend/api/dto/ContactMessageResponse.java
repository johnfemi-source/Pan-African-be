package com.panafrican.backend.api.dto;

import java.time.Instant;

public record ContactMessageResponse(String id, String name, String email, String body, Instant createdAt) {
}
