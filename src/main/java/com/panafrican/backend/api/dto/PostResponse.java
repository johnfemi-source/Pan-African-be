package com.panafrican.backend.api.dto;

import java.time.Instant;

public record PostResponse(String id, String title, String body, String category, Instant createdAt, String author) {
}
