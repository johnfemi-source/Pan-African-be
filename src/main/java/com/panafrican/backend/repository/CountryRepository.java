package com.panafrican.backend.repository;

import com.panafrican.backend.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Optional<Country> findBySlug(String slug);
    List<Country> findAllByOrderByNameAsc();
    boolean existsBySlug(String slug);
}
