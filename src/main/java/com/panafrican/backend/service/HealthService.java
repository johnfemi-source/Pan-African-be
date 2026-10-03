package com.panafrican.backend.service;

import com.panafrican.backend.api.dto.HealthResponse;
import org.springframework.stereotype.Service;

@Service
public class HealthService {

    public HealthResponse getHealth() {
        return new HealthResponse("UP", "pan-african-backend");
    }
}
