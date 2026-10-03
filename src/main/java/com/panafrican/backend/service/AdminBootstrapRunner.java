package com.panafrican.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminBootstrapRunner implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(AdminBootstrapRunner.class);

    private final StaffAccountService accountService;
    private final String username;
    private final String password;

    public AdminBootstrapRunner(StaffAccountService accountService,
                               @Value("${payan.bootstrap-admin.username:}") String username,
                               @Value("${payan.bootstrap-admin.password:}") String password) {
        this.accountService = accountService;
        this.username = username;
        this.password = password;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (username.isBlank() && password.isBlank()) {
            return;
        }
        if (username.isBlank() || password.isBlank()) {
            throw new IllegalStateException("Both PAYAN_BOOTSTRAP_ADMIN_USERNAME and PAYAN_BOOTSTRAP_ADMIN_PASSWORD must be set together");
        }
        accountService.bootstrapFirstAdmin(username, password);
        logger.info("Initial admin bootstrap checked; an account is created only when no staff accounts exist.");
    }
}