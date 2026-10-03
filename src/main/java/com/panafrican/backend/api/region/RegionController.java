package com.panafrican.backend.api.region;

import com.panafrican.backend.api.dto.CountryResponse;
import com.panafrican.backend.api.dto.RegionDetailResponse;
import com.panafrican.backend.api.dto.RegionResponse;
import com.panafrican.backend.domain.Country;
import com.panafrican.backend.domain.Region;
import com.panafrican.backend.repository.CountryRepository;
import com.panafrican.backend.repository.RegionRepository;
import com.panafrican.backend.service.RegionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class RegionController {

    private final RegionService regionService;
    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;

    public RegionController(RegionService regionService, RegionRepository regionRepository, CountryRepository countryRepository) {
        this.regionService = regionService;
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
    }

    @GetMapping("/api/v1/regions")
    public List<RegionResponse> getRegions() {
        return regionService.findAll();
    }

    @GetMapping("/api/v1/regions/{regionSlug}")
    public RegionDetailResponse getRegion(@PathVariable String regionSlug) {
        Region region = regionRepository.findBySlug(regionSlug)
                .orElseThrow(() -> new IllegalArgumentException("Region not found: " + regionSlug));

        List<CountryResponse> countries = countryRepository.findAllByOrderByNameAsc().stream()
                .filter(country -> country.getRegion().getSlug().equals(regionSlug))
                .map(country -> new CountryResponse(country.getSlug(), country.getName(), country.getIso(), country.getRegion().getSlug()))
                .toList();

        return new RegionDetailResponse(region.getSlug(), region.getName(), countries);
    }
}
