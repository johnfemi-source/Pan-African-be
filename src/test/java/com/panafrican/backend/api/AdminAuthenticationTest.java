package com.panafrican.backend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "DB_URL=jdbc:h2:mem:payan-auth-test;DB_CLOSE_DELAY=-1",
        "DB_USER=sa",
        "DB_PASSWORD=",
        "DB_DDL_AUTO=create-drop",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "payan.bootstrap-admin.username=test-admin",
        "payan.bootstrap-admin.password=test-password-123"
})
@AutoConfigureMockMvc
class AdminAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void currentUserRequiresValidBasicCredentials() throws Exception {
        mockMvc.perform(get("/api/v1/me"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/me").header("Authorization", basicCredentials("test-admin", "test-password-123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("test-admin"))
                .andExpect(jsonPath("$.role").value("SUPER_ADMIN"));
    }

    @Test
    void roleHeadersDoNotGrantAdminAccess() throws Exception {
        mockMvc.perform(get("/api/v1/admin/messages").header("X-User-Role", "SUPER_ADMIN"))
                .andExpect(status().isUnauthorized());
    }

    private String basicCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}