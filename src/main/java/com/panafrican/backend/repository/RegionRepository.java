package com.panafrican.backend.repository;

import com.panafrican.backend.domain.Region;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {
    Optional<Region> findBySlug(String slug);
    boolean existsBySlug(String slug);
    java.util.List<Region> findAllByOrderByDisplayOrderAscNameAsc();
}
