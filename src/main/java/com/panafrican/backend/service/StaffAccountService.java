package com.panafrican.backend.service;

import com.panafrican.backend.api.dto.CreateUserRequest;
import com.panafrican.backend.api.dto.UserProfileResponse;
import com.panafrican.backend.api.exception.BadRequestException;
import com.panafrican.backend.api.exception.ConflictException;
import com.panafrican.backend.domain.StaffAccount;
import com.panafrican.backend.domain.UserRole;
import com.panafrican.backend.repository.CountryRepository;
import com.panafrican.backend.repository.RegionRepository;
import com.panafrican.backend.repository.StaffAccountRepository;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
public class StaffAccountService implements UserDetailsService {

    private final StaffAccountRepository accountRepository;
    private final CountryRepository countryRepository;
    private final RegionRepository regionRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffAccountService(StaffAccountRepository accountRepository, CountryRepository countryRepository,
                               RegionRepository regionRepository, PasswordEncoder passwordEncoder) {
        this.accountRepository = accountRepository;
        this.countryRepository = countryRepository;
        this.regionRepository = regionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public StaffAccount loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Account not found"));
    }

    @Transactional
    public UserProfileResponse create(CreateUserRequest request) {
        if (request == null || request.role() == null) {
            throw new BadRequestException("username, password, and role are required");
        }
        String username = normalizeUsername(request.username());
        String password = request.password();
        if (password == null || password.length() < 8) {
            throw new BadRequestException("password must be at least 8 characters");
        }
        if (accountRepository.existsByUsernameIgnoreCase(username)) {
            throw new ConflictException("Username is already in use");
        }

        String countrySlug = trimToNull(request.countrySlug());
        String regionSlug = trimToNull(request.regionSlug());
        validateScope(request.role(), countrySlug, regionSlug);
        StaffAccount saved = accountRepository.save(new StaffAccount(
                username, passwordEncoder.encode(password), request.role(), regionSlug, countrySlug));
        return toResponse(saved);
    }

    @Transactional
    public void bootstrapFirstAdmin(String username, String password) {
        if (accountRepository.count() != 0) {
            return;
        }
        create(new CreateUserRequest(username, password, UserRole.SUPER_ADMIN, null, null));
    }

    public UserProfileResponse toResponse(StaffAccount account) {
        return new UserProfileResponse(account.getId(), account.getUsername(), account.getRole(),
                account.getRegionSlug(), account.getCountrySlug());
    }

    private void validateScope(UserRole role, String countrySlug, String regionSlug) {
        switch (role) {
            case COUNTRY_REP -> {
                if (countrySlug == null || regionSlug != null || !countryRepository.existsBySlug(countrySlug)) {
                    throw new BadRequestException("COUNTRY_REP requires a valid countrySlug only");
                }
            }
            case REGIONAL_COORDINATOR -> {
                if (regionSlug == null || countrySlug != null || !regionRepository.existsBySlug(regionSlug)) {
                    throw new BadRequestException("REGIONAL_COORDINATOR requires a valid regionSlug only");
                }
            }
            case SUPER_ADMIN -> {
                if (countrySlug != null || regionSlug != null) {
                    throw new BadRequestException("SUPER_ADMIN must not have a geographic scope");
                }
            }
        }
    }

    private String normalizeUsername(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("username is required");
        }
        String username = value.trim().toLowerCase(Locale.ROOT);
        if (username.length() > 80) {
            throw new BadRequestException("username must be 80 characters or fewer");
        }
        return username;
    }

    private String trimToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}