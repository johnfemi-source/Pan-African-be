package com.panafrican.backend.api.dto;

import java.util.List;

public record CountryDetailResponse(String regionSlug, String regionName, CountryResponse country, List<PostResponse> posts) {
}
