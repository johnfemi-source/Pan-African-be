package com.panafrican.backend.api;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RegionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void healthEndpointReturnsUp() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void regionsEndpointReturnsSeedData() throws Exception {
        mockMvc.perform(get("/api/v1/regions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("west-africa"));
    }

    @Test
    void regionsAndCountriesIncludeFullAfricanDirectory() throws Exception {
        mockMvc.perform(get("/api/v1/regions").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.slug == 'north-africa')] ").exists())
                .andExpect(jsonPath("$[?(@.slug == 'central-africa')] ").exists());

        mockMvc.perform(get("/api/v1/countries").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").isNotEmpty())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(54)))
                .andExpect(jsonPath("$[?(@.slug == 'tunisia')].regionSlug").value(org.hamcrest.Matchers.hasItem("north-africa")))
                .andExpect(jsonPath("$[?(@.slug == 'cameroon')].regionSlug").value(org.hamcrest.Matchers.hasItem("central-africa")))
                .andExpect(jsonPath("$[?(@.slug == 'nigeria')].regionSlug").value(org.hamcrest.Matchers.hasItem("west-africa")))
                .andExpect(jsonPath("$[?(@.slug == 'kenya')].regionSlug").value(org.hamcrest.Matchers.hasItem("east-africa")))
                .andExpect(jsonPath("$[?(@.slug == 'south-africa')].regionSlug").value(org.hamcrest.Matchers.hasItem("southern-africa")));
    }
}
