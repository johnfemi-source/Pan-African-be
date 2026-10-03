package com.panafrican.backend.api.country;

import com.panafrican.backend.api.dto.CountryDetailResponse;
import com.panafrican.backend.api.dto.CountryResponse;
import com.panafrican.backend.service.CountryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping("/api/v1/countries")
    public List<CountryResponse> getCountries() {
        return countryService.findAll();
    }

    @GetMapping("/api/v1/countries/{countrySlug}")
    public CountryDetailResponse getCountry(@PathVariable String countrySlug) {
        return countryService.findDetail(countrySlug);
    }
}
