package com.panafrican.backend.api.auth;

import com.panafrican.backend.api.dto.CreateUserRequest;
import com.panafrican.backend.api.dto.UserProfileResponse;
import com.panafrican.backend.service.StaffAccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminAccountController {

    private final StaffAccountService accountService;

    public AdminAccountController(StaffAccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/api/v1/admin/users")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserProfileResponse> create(@Valid @RequestBody CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(accountService.create(request));
    }
}