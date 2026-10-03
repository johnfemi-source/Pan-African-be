package com.panafrican.backend.repository;

import com.panafrican.backend.domain.StaffAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StaffAccountRepository extends JpaRepository<StaffAccount, Long> {
    Optional<StaffAccount> findByUsernameIgnoreCase(String username);
    boolean existsByUsernameIgnoreCase(String username);
}