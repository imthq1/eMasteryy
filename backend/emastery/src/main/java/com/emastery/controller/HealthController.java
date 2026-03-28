package com.emastery.controller;

import com.emastery.service.HealthcheckService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/healthcheck/check-api-key")
public class HealthController {

    private final HealthcheckService healthcheckService;

    public HealthController(HealthcheckService healthcheckService) {
        this.healthcheckService = healthcheckService;
    }

    @PostMapping
    public boolean check(@RequestParam(name = "ApiKey") String ApiKey) {
        System.out.println("ApiKey: " + ApiKey);
        return healthcheckService.healthcheck(ApiKey);
    }
}