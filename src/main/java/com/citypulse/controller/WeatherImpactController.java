package com.citypulse.controller;

import com.citypulse.dto.WeatherImpactResponse;
import com.citypulse.service.WeatherImpactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather/impact")
public class WeatherImpactController {

    private final WeatherImpactService weatherImpactService;

    public WeatherImpactController(
            WeatherImpactService weatherImpactService) {

        this.weatherImpactService =
                weatherImpactService;
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<WeatherImpactResponse>
    getWeatherImpact(
            @PathVariable Long locationId) {

        return weatherImpactService
                .analyzeLocation(locationId)
                .map(ResponseEntity::ok)
                .orElse(
                        ResponseEntity.notFound().build());
    }
}
