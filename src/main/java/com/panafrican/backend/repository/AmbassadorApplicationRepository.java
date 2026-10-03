package com.panafrican.backend.repository;

import com.panafrican.backend.domain.AmbassadorApplication;
import com.panafrican.backend.domain.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AmbassadorApplicationRepository extends JpaRepository<AmbassadorApplication, String> {
    List<AmbassadorApplication> findAllByOrderByCreatedAtAsc();
    List<AmbassadorApplication> findByCountryRegionSlugOrderByCreatedAtAsc(String regionSlug);
    List<AmbassadorApplication> findByCountryOrderByCreatedAtAsc(Country country);
}
