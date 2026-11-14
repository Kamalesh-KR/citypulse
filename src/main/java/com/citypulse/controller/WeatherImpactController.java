package com.citypulse.controller;

import com.citypulse.dto.WeatherImpactResponse;
import com.citypulse.service.WeatherImpactService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

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
                .orElseGet(() ->
                        ResponseEntity.ok(
                                synthesizeDefaultImpact(locationId)));
    }

    /**
     * Returns a realistic default weather profile for locations
     * that have no recorded weather yet. Prevents the frontend
     * from showing "NOT AVAILABLE" and lets every road display
     * a weather impact score.
     *
     * Values reflect a typical Bengaluru / Karnataka climate:
     *   - 26.5°C, 65% humidity, 0.0mm rainfall (no rain)
     *   - Clear / Partly-Cloudy condition
     *   - 10.0 km visibility, 5.0 m/s wind
     *   - Weather impact score = 20.0 (LOW impact on traffic)
     */
    private WeatherImpactResponse synthesizeDefaultImpact(
            Long locationId) {

        WeatherImpactResponse response =
                new WeatherImpactResponse();

        response.setLocationId(locationId);
        response.setLocationName("Selected Location");

        response.setWeatherRecordId(null);
        response.setRecordedAt(LocalDateTime.now());

        response.setTemperature(26.5);
        response.setHumidity(65.0);
        response.setRainfall(0.0);
        response.setWindSpeed(5.0);
        response.setVisibility(10.0);

        response.setWeatherCondition("CLEAR");

        response.setWeatherImpactScore(20.0);
        response.setWeatherImpactLevel("LOW");

        response.setImpactSummary(
                "No active weather record for this location. "
                        + "Using CityPulse default clear-weather profile. "
                        + "Weather impact on traffic is expected to be low.");

        return response;
    }
}