package com.panafrican.backend.api.dto;

import java.util.List;

public record RegionDetailResponse(String slug, String name, List<CountryResponse> countries) {
}
