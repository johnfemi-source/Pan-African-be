package com.panafrican.backend.service;

import com.panafrican.backend.domain.*;
import com.panafrican.backend.repository.CountryRepository;
import com.panafrican.backend.repository.PostRepository;
import com.panafrican.backend.repository.RegionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Component
public class SeedDataService {

    private final RegionRepository regionRepository;
    private final CountryRepository countryRepository;
    private final PostRepository postRepository;

    public SeedDataService(RegionRepository regionRepository, CountryRepository countryRepository, PostRepository postRepository) {
        this.regionRepository = regionRepository;
        this.countryRepository = countryRepository;
        this.postRepository = postRepository;
    }

    @PostConstruct
    @Transactional
    public void init() {
        Region westAfrica = ensureRegion("west-africa", "West Africa", 1);
        Region eastAfrica = ensureRegion("east-africa", "East Africa", 2);
        Region southernAfrica = ensureRegion("southern-africa", "Southern Africa", 3);
        Region centralAfrica = ensureRegion("central-africa", "Central Africa", 4);
        Region northAfrica = ensureRegion("north-africa", "North Africa", 5);

        List<CountrySeed> countries = List.of(
                new CountrySeed("algeria", "Algeria", "dz", northAfrica),
                new CountrySeed("angola", "Angola", "ao", centralAfrica),
                new CountrySeed("benin", "Benin", "bj", westAfrica),
                new CountrySeed("botswana", "Botswana", "bw", southernAfrica),
                new CountrySeed("burkina-faso", "Burkina Faso", "bf", westAfrica),
                new CountrySeed("burundi", "Burundi", "bi", eastAfrica),
                new CountrySeed("cabo-verde", "Cabo Verde", "cv", westAfrica),
                new CountrySeed("cameroon", "Cameroon", "cm", centralAfrica),
                new CountrySeed("central-african-republic", "Central African Republic", "cf", centralAfrica),
                new CountrySeed("chad", "Chad", "td", centralAfrica),
                new CountrySeed("comoros", "Comoros", "km", eastAfrica),
                new CountrySeed("democratic-republic-of-the-congo", "Democratic Republic of the Congo", "cd", centralAfrica),
                new CountrySeed("republic-of-the-congo", "Republic of the Congo", "cg", centralAfrica),
                new CountrySeed("cote-divoire", "Cote d'Ivoire", "ci", westAfrica),
                new CountrySeed("djibouti", "Djibouti", "dj", eastAfrica),
                new CountrySeed("egypt", "Egypt", "eg", northAfrica),
                new CountrySeed("equatorial-guinea", "Equatorial Guinea", "gq", centralAfrica),
                new CountrySeed("eritrea", "Eritrea", "er", eastAfrica),
                new CountrySeed("eswatini", "Eswatini", "sz", southernAfrica),
                new CountrySeed("ethiopia", "Ethiopia", "et", eastAfrica),
                new CountrySeed("gabon", "Gabon", "ga", centralAfrica),
                new CountrySeed("gambia", "The Gambia", "gm", westAfrica),
                new CountrySeed("ghana", "Ghana", "gh", westAfrica),
                new CountrySeed("guinea", "Guinea", "gn", westAfrica),
                new CountrySeed("guinea-bissau", "Guinea-Bissau", "gw", westAfrica),
                new CountrySeed("kenya", "Kenya", "ke", eastAfrica),
                new CountrySeed("lesotho", "Lesotho", "ls", southernAfrica),
                new CountrySeed("liberia", "Liberia", "lr", westAfrica),
                new CountrySeed("libya", "Libya", "ly", northAfrica),
                new CountrySeed("madagascar", "Madagascar", "mg", eastAfrica),
                new CountrySeed("malawi", "Malawi", "mw", eastAfrica),
                new CountrySeed("mali", "Mali", "ml", westAfrica),
                new CountrySeed("mauritania", "Mauritania", "mr", westAfrica),
                new CountrySeed("mauritius", "Mauritius", "mu", eastAfrica),
                new CountrySeed("morocco", "Morocco", "ma", northAfrica),
                new CountrySeed("mozambique", "Mozambique", "mz", eastAfrica),
                new CountrySeed("namibia", "Namibia", "na", southernAfrica),
                new CountrySeed("niger", "Niger", "ne", westAfrica),
                new CountrySeed("nigeria", "Nigeria", "ng", westAfrica),
                new CountrySeed("rwanda", "Rwanda", "rw", eastAfrica),
                new CountrySeed("sao-tome-and-principe", "Sao Tome and Principe", "st", centralAfrica),
                new CountrySeed("senegal", "Senegal", "sn", westAfrica),
                new CountrySeed("seychelles", "Seychelles", "sc", eastAfrica),
                new CountrySeed("sierra-leone", "Sierra Leone", "sl", westAfrica),
                new CountrySeed("somalia", "Somalia", "so", eastAfrica),
                new CountrySeed("south-africa", "South Africa", "za", southernAfrica),
                new CountrySeed("south-sudan", "South Sudan", "ss", eastAfrica),
                new CountrySeed("sudan", "Sudan", "sd", northAfrica),
                new CountrySeed("tanzania", "Tanzania", "tz", eastAfrica),
                new CountrySeed("togo", "Togo", "tg", westAfrica),
                new CountrySeed("tunisia", "Tunisia", "tn", northAfrica),
                new CountrySeed("uganda", "Uganda", "ug", eastAfrica),
                new CountrySeed("zambia", "Zambia", "zm", eastAfrica),
                new CountrySeed("zimbabwe", "Zimbabwe", "zw", eastAfrica)
        );

        for (CountrySeed seed : countries) {
            if (countryRepository.findBySlug(seed.slug()).isEmpty()) {
                countryRepository.save(new Country(seed.slug(), seed.name(), seed.iso(), seed.region()));
            }
        }

        if (postRepository.count() == 0) {
            Country ghana = countryRepository.findBySlug("ghana").orElseThrow();
            Country nigeria = countryRepository.findBySlug("nigeria").orElseThrow();
            Country kenya = countryRepository.findBySlug("kenya").orElseThrow();
            Country southAfrica = countryRepository.findBySlug("south-africa").orElseThrow();
            postRepository.save(new Post(UUID.randomUUID().toString(), "Youth leadership forum", "A regional youth leadership forum opens next week.", PostCategory.EVENT, ghana, "Ambassador Network", Instant.now()));
            postRepository.save(new Post(UUID.randomUUID().toString(), "Regional update", "Local programmes continue across the region.", PostCategory.NEWS, nigeria, "Regional Team", Instant.now()));
            postRepository.save(new Post(UUID.randomUUID().toString(), "Community outreach", "Our outreach programme highlights youth opportunities in East Africa.", PostCategory.NEWS, kenya, "Regional Team", Instant.now()));
            postRepository.save(new Post(UUID.randomUUID().toString(), "Pan-African collaboration", "Regional partners are strengthening local networks.", PostCategory.EVENT, southAfrica, "Ambassador Network", Instant.now()));
        }
    }

    private Region ensureRegion(String slug, String name, int displayOrder) {
        return regionRepository.findBySlug(slug)
                .orElseGet(() -> regionRepository.save(new Region(slug, name, displayOrder)));
    }

    private record CountrySeed(String slug, String name, String iso, Region region) {}
}
