package com.panafrican.backend.api.auth;

import com.panafrican.backend.api.dto.UserProfileResponse;
import com.panafrican.backend.domain.StaffAccount;
import com.panafrican.backend.service.StaffAccountService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MeController {

    private final StaffAccountService accountService;

    public MeController(StaffAccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/api/v1/me")
    public UserProfileResponse me(@AuthenticationPrincipal StaffAccount account) {
        return accountService.toResponse(account);
    }
}