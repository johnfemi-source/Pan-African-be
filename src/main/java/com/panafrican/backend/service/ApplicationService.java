package com.panafrican.backend.service;

import com.panafrican.backend.api.dto.ApplicationRequest;
import com.panafrican.backend.api.dto.ApplicationResponse;
import com.panafrican.backend.api.dto.CreateApplicationResponse;
import com.panafrican.backend.api.exception.BadRequestException;
import com.panafrican.backend.api.exception.ConflictException;
import com.panafrican.backend.api.exception.ForbiddenException;
import com.panafrican.backend.api.exception.ResourceNotFoundException;
import com.panafrican.backend.api.exception.UnauthorizedException;
import com.panafrican.backend.domain.AmbassadorApplication;
import com.panafrican.backend.domain.ApplicationStatus;
import com.panafrican.backend.domain.Country;
import com.panafrican.backend.domain.UserRole;
import com.panafrican.backend.repository.AmbassadorApplicationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class ApplicationService {

    private final CountryService countryService;
    private final AmbassadorApplicationRepository applicationRepository;

    public ApplicationService(CountryService countryService, AmbassadorApplicationRepository applicationRepository) {
        this.countryService = countryService;
        this.applicationRepository = applicationRepository;
    }

    public CreateApplicationResponse create(ApplicationRequest request) {
        Country country = countryService.getRequiredEntity(request.countrySlug());
        String trimmedFullName = request.fullName().trim();
        String trimmedEmail = request.email().trim();
        String trimmedMotivation = request.motivation().trim();

        if (trimmedFullName.length() < 2 || trimmedFullName.length() > 160) {
            throw new BadRequestException("fullName must be between 2 and 160 characters");
        }
        if (trimmedMotivation.length() < 20 || trimmedMotivation.length() > 5000) {
            throw new BadRequestException("motivation must be between 20 and 5000 characters");
        }

        Instant now = Instant.now();
        AmbassadorApplication application = new AmbassadorApplication(
                UUID.randomUUID().toString(),
                trimmedFullName,
                trimmedEmail,
                request.phone() == null ? null : request.phone().trim(),
                country,
                trimmedMotivation,
                ApplicationStatus.PENDING,
                now,
                now
        );

        AmbassadorApplication saved = applicationRepository.save(application);
        return new CreateApplicationResponse(saved.getId(), saved.getStatus());
    }

    public List<ApplicationResponse> getApplications(String userRoleHeader, String regionScope) {
        UserRole role = parseRole(userRoleHeader);
        if (role == UserRole.SUPER_ADMIN) {
            return applicationRepository.findAllByOrderByCreatedAtAsc().stream().map(this::toResponse).toList();
        }
        if (role == UserRole.REGIONAL_COORDINATOR) {
            if (regionScope == null || regionScope.isBlank()) {
                throw new ForbiddenException("Region scope required for regional coordinators");
            }
            return applicationRepository.findByCountryRegionSlugOrderByCreatedAtAsc(regionScope).stream()
                    .map(this::toResponse)
                    .toList();
        }
        throw new ForbiddenException("Only coordinators and super admins can access applications");
    }

    public ApplicationResponse updateApplication(String id, String userRoleHeader, String regionScope, String statusValue) {
        UserRole role = parseRole(userRoleHeader);
        AmbassadorApplication application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));

        if (role == UserRole.REGIONAL_COORDINATOR && (regionScope == null || !regionScope.equals(application.getCountry().getRegion().getSlug()))) {
            throw new ForbiddenException("Regional coordinators may only update applications in their region");
        }
        if (role != UserRole.SUPER_ADMIN && role != UserRole.REGIONAL_COORDINATOR) {
            throw new ForbiddenException("You are not allowed to update applications");
        }
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ConflictException("Application is no longer pending");
        }

        ApplicationStatus nextStatus;
        try {
            nextStatus = ApplicationStatus.valueOf(statusValue.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException("status must be APPROVED or REJECTED");
        }

        if (nextStatus != ApplicationStatus.APPROVED && nextStatus != ApplicationStatus.REJECTED) {
            throw new BadRequestException("status must be APPROVED or REJECTED");
        }

        application.setStatus(nextStatus);
        application.setUpdatedAt(Instant.now());
        AmbassadorApplication saved = applicationRepository.save(application);
        return toResponse(saved);
    }

    private ApplicationResponse toResponse(AmbassadorApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getFullName(),
                application.getEmail(),
                application.getPhone(),
                application.getCountry().getSlug(),
                application.getMotivation(),
                application.getStatus(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }

    private UserRole parseRole(String userRoleHeader) {
        if (userRoleHeader == null || userRoleHeader.isBlank()) {
            throw new UnauthorizedException("Authentication required");
        }
        try {
            return UserRole.valueOf(userRoleHeader.trim().replace('-', '_').toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new UnauthorizedException("Unknown user role");
        }
    }
}
