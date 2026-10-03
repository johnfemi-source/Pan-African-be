package com.panafrican.backend.repository;

import com.panafrican.backend.domain.Country;
import com.panafrican.backend.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, String> {
    List<Post> findByCountryOrderByCreatedAtDesc(Country country);
}
