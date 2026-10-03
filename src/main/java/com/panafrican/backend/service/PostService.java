package com.panafrican.backend.service;

import com.panafrican.backend.api.dto.CreatePostRequest;
import com.panafrican.backend.api.dto.PostResponse;
import com.panafrican.backend.api.exception.BadRequestException;
import com.panafrican.backend.api.exception.ForbiddenException;
import com.panafrican.backend.api.exception.UnauthorizedException;
import com.panafrican.backend.domain.Country;
import com.panafrican.backend.domain.Post;
import com.panafrican.backend.domain.PostCategory;
import com.panafrican.backend.domain.UserRole;
import com.panafrican.backend.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PostService {

    private final CountryService countryService;
    private final PostRepository postRepository;

    public PostService(CountryService countryService, PostRepository postRepository) {
        this.countryService = countryService;
        this.postRepository = postRepository;
    }

    public PostResponse createPost(String countrySlug, CreatePostRequest request, String userRoleHeader,
                                 String countryScope, String regionScope, String author) {
        Country country = countryService.getRequiredEntity(countrySlug);
        if (request == null) {
            throw new BadRequestException("Request body is required");
        }
        if (request.countrySlug() != null && !request.countrySlug().equals(countrySlug)) {
            throw new BadRequestException("countrySlug in body must match the path");
        }

        UserRole role = parseRole(userRoleHeader);
        if (role == UserRole.COUNTRY_REP && (countryScope == null || !countryScope.equals(country.getSlug()))) {
            throw new ForbiddenException("Country reps may publish only to their assigned country");
        }
        if (role == UserRole.REGIONAL_COORDINATOR && (regionScope == null || !regionScope.equals(country.getRegion().getSlug()))) {
            throw new ForbiddenException("Regional coordinators may publish only within their region");
        }

        String trimmedTitle = request.title().trim();
        String trimmedBody = request.body().trim();
        if (request.category() == null) {
            throw new BadRequestException("category is required");
        }
        if (request.category() != PostCategory.NEWS && request.category() != PostCategory.EVENT) {
            throw new BadRequestException("category must be NEWS or EVENT");
        }

        Post post = new Post(UUID.randomUUID().toString(), trimmedTitle, trimmedBody, request.category(), country, author, Instant.now());
        Post saved = postRepository.save(post);
        return new PostResponse(saved.getId(), saved.getTitle(), saved.getBody(), saved.getCategory().name(), saved.getCreatedAt(), saved.getAuthor());
    }

    private UserRole parseRole(String userRoleHeader) {
        if (userRoleHeader == null || userRoleHeader.isBlank()) {
            throw new UnauthorizedException("Authentication required");
        }
        try {
            return UserRole.valueOf(userRoleHeader.trim().replace('-', '_').toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Unknown user role");
        }
    }
}
