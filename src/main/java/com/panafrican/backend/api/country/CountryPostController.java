package com.panafrican.backend.api.country;

import com.panafrican.backend.api.dto.CreatePostRequest;
import com.panafrican.backend.api.dto.PostResponse;
import com.panafrican.backend.domain.StaffAccount;
import com.panafrican.backend.service.PostService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
public class CountryPostController {

    private final PostService postService;

    public CountryPostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping("/api/v1/countries/{countrySlug}/posts")
    public ResponseEntity<PostResponse> createPost(
            @PathVariable String countrySlug,
            @Valid @RequestBody CreatePostRequest request,
            @AuthenticationPrincipal StaffAccount account) {

        PostResponse response = postService.createPost(countrySlug, request, account.getRole().name(), account.getCountrySlug(), account.getRegionSlug(), account.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
