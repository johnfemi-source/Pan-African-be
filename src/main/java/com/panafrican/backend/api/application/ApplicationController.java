package com.panafrican.backend.api.application;

import com.panafrican.backend.api.dto.ApplicationRequest;
import com.panafrican.backend.api.dto.ApplicationResponse;
import com.panafrican.backend.api.dto.CreateApplicationResponse;
import com.panafrican.backend.domain.StaffAccount;
import com.panafrican.backend.service.ApplicationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/api/v1/applications")
    public ResponseEntity<CreateApplicationResponse> submitApplication(@Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(applicationService.create(request));
    }

    @GetMapping("/api/v1/admin/applications")
    public List<ApplicationResponse> getApplications(@AuthenticationPrincipal StaffAccount account) {
        return applicationService.getApplications(account.getRole().name(), account.getRegionSlug());
    }

    @PatchMapping("/api/v1/admin/applications/{applicationId}")
    public ApplicationResponse updateApplication(
            @PathVariable String applicationId,
            @AuthenticationPrincipal StaffAccount account,
            @RequestBody java.util.Map<String, String> payload) {
        String status = payload.get("status");
        return applicationService.updateApplication(applicationId, account.getRole().name(), account.getRegionSlug(), status);
    }
}
