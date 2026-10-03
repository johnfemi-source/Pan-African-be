package com.panafrican.backend.service;

import com.panafrican.backend.api.dto.RegionResponse;
import com.panafrican.backend.api.exception.ResourceNotFoundException;
import com.panafrican.backend.domain.Region;
import com.panafrican.backend.repository.RegionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegionService {

    private final RegionRepository regionRepository;

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    public List<RegionResponse> findAll() {
        return regionRepository.findAllByOrderByDisplayOrderAscNameAsc().stream()
                .map(this::toResponse)
                .toList();
    }

    public RegionResponse findBySlug(String slug) {
        return toResponse(getRequiredEntity(slug));
    }

    public Region getRequiredEntity(String slug) {
        return regionRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Region not found: " + slug));
    }

    private RegionResponse toResponse(Region region) {
        return new RegionResponse(region.getSlug(), region.getName());
    }
}
