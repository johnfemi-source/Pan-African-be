package com.panafrican.backend.service;

import com.panafrican.backend.api.dto.CountryDetailResponse;
import com.panafrican.backend.api.dto.CountryResponse;
import com.panafrican.backend.api.dto.PostResponse;
import com.panafrican.backend.api.exception.ResourceNotFoundException;
import com.panafrican.backend.domain.Country;
import com.panafrican.backend.repository.CountryRepository;
import com.panafrican.backend.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {

    private final CountryRepository countryRepository;
    private final PostRepository postRepository;

    public CountryService(CountryRepository countryRepository, PostRepository postRepository) {
        this.countryRepository = countryRepository;
        this.postRepository = postRepository;
    }

    public List<CountryResponse> findAll() {
        return countryRepository.findAllByOrderByNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public Country getRequiredEntity(String slug) {
        return countryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Country not found: " + slug));
    }

    public CountryDetailResponse findDetail(String slug) {
        Country country = getRequiredEntity(slug);
        List<PostResponse> posts = postRepository.findByCountryOrderByCreatedAtDesc(country).stream()
                .map(post -> new PostResponse(post.getId(), post.getTitle(), post.getBody(), post.getCategory().name(), post.getCreatedAt(), post.getAuthor()))
                .toList();

        return new CountryDetailResponse(country.getRegion().getSlug(), country.getRegion().getName(), toResponse(country), posts);
    }

    public CountryResponse toResponse(Country country) {
        return new CountryResponse(country.getSlug(), country.getName(), country.getIso(), country.getRegion().getSlug());
    }
}
