package com.citypulse.service;

import com.citypulse.dto.WeatherImpactResponse;
import com.citypulse.model.WeatherRecord;
import com.citypulse.repository.WeatherRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherImpactService {

    private final WeatherRecordRepository weatherRecordRepository;

    public WeatherImpactService(
            WeatherRecordRepository weatherRecordRepository) {

        this.weatherRecordRepository = weatherRecordRepository;
    }

    public Optional<WeatherImpactResponse> analyzeLocation(
            Long locationId) {

        List<WeatherRecord> records =
                weatherRecordRepository.findByLocationId(locationId);

        if (records.isEmpty()) {
            return Optional.empty();
        }

        WeatherRecord latestRecord =
                records.stream()
                        .filter(record -> record.getRecordedAt() != null)
                        .max(Comparator.comparing(
                                WeatherRecord::getRecordedAt))
                        .orElse(records.get(records.size() - 1));

        return Optional.of(buildResponse(latestRecord));
    }

    private WeatherImpactResponse buildResponse(
            WeatherRecord record) {

        double rainfallScore =
                calculateRainfallScore(record.getRainfall());

        double visibilityScore =
                calculateVisibilityScore(record.getVisibility());

        double windScore =
                calculateWindScore(record.getWindSpeed());

        double conditionScore =
                calculateConditionScore(
                        record.getWeatherCondition());

        double humidityScore =
                calculateHumidityScore(record.getHumidity());

        double score =
                (rainfallScore * 0.35)
                + (visibilityScore * 0.30)
                + (windScore * 0.15)
                + (conditionScore * 0.15)
                + (humidityScore * 0.05);

        score = roundAndClamp(score);

        String impactLevel =
                determineImpactLevel(score);

        String summary =
                buildSummary(
                        record,
                        impactLevel,
                        score);

        WeatherImpactResponse response =
                new WeatherImpactResponse();

        response.setLocationId(
                record.getLocation().getId());

        response.setLocationName(
                record.getLocation().getName());

        response.setWeatherRecordId(
                record.getId());

        response.setRecordedAt(
                record.getRecordedAt());

        response.setTemperature(
                toDouble(record.getTemperature()));

        response.setHumidity(
                toDouble(record.getHumidity()));

        response.setRainfall(
                toDouble(record.getRainfall()));

        response.setWindSpeed(
                toDouble(record.getWindSpeed()));

        response.setVisibility(
                toDouble(record.getVisibility()));

        response.setWeatherCondition(
                record.getWeatherCondition());

        response.setWeatherImpactScore(score);

        response.setWeatherImpactLevel(
                impactLevel);

        response.setImpactSummary(summary);

        return response;
    }

    private double calculateRainfallScore(
            BigDecimal rainfall) {

        if (rainfall == null) {
            return 0.0;
        }

        double value = rainfall.doubleValue();

        if (value <= 0) {
            return 0.0;
        }

        if (value <= 2) {
            return 20.0;
        }

        if (value <= 10) {
            return 50.0;
        }

        if (value <= 25) {
            return 75.0;
        }

        return 100.0;
    }

    private double calculateVisibilityScore(
            BigDecimal visibility) {

        if (visibility == null) {
            return 0.0;
        }

        double value = visibility.doubleValue();

        if (value >= 10) {
            return 0.0;
        }

        if (value >= 7) {
            return 25.0;
        }

        if (value >= 4) {
            return 50.0;
        }

        if (value >= 2) {
            return 75.0;
        }

        return 100.0;
    }

    private double calculateWindScore(
            BigDecimal windSpeed) {

        if (windSpeed == null) {
            return 0.0;
        }

        double value = windSpeed.doubleValue();

        if (value <= 10) {
            return 0.0;
        }

        if (value <= 20) {
            return 30.0;
        }

        if (value <= 30) {
            return 60.0;
        }

        return 100.0;
    }

    private double calculateConditionScore(
            String condition) {

        if (condition == null ||
                condition.isBlank()) {

            return 0.0;
        }

        String normalized =
                condition.trim().toUpperCase();

        if (normalized.contains("CLEAR") ||
                normalized.contains("SUNNY")) {

            return 0.0;
        }

        if (normalized.contains("CLOUD")) {
            return 10.0;
        }

        if (normalized.contains("DRIZZLE")) {
            return 35.0;
        }

        if (normalized.contains("RAIN")) {
            return 65.0;
        }

        if (normalized.contains("FOG") ||
                normalized.contains("MIST")) {

            return 70.0;
        }

        if (normalized.contains("STORM") ||
                normalized.contains("THUNDER")) {

            return 90.0;
        }

        return 25.0;
    }

    private double calculateHumidityScore(
            BigDecimal humidity) {

        if (humidity == null) {
            return 0.0;
        }

        double value = humidity.doubleValue();

        if (value <= 60) {
            return 0.0;
        }

        if (value <= 75) {
            return 25.0;
        }

        if (value <= 90) {
            return 50.0;
        }

        return 75.0;
    }

    private String determineImpactLevel(
            double score) {

        if (score >= 75) {
            return "SEVERE";
        }

        if (score >= 50) {
            return "HIGH";
        }

        if (score >= 25) {
            return "MODERATE";
        }

        return "LOW";
    }

    private String buildSummary(
            WeatherRecord record,
            String impactLevel,
            double score) {

        String condition =
                record.getWeatherCondition();

        if (condition == null ||
                condition.isBlank()) {

            condition = "current weather conditions";
        }

        if ("LOW".equals(impactLevel)) {

            return String.format(
                    "Weather conditions are currently having low estimated impact on traffic. Current condition: %s. Weather impact score: %.2f.",
                    condition,
                    score);
        }

        if ("MODERATE".equals(impactLevel)) {

            return String.format(
                    "Weather conditions may have a moderate effect on traffic. Current condition: %s. Weather impact score: %.2f.",
                    condition,
                    score);
        }

        if ("HIGH".equals(impactLevel)) {

            return String.format(
                    "Weather conditions may significantly affect traffic conditions. Current condition: %s. Weather impact score: %.2f.",
                    condition,
                    score);
        }

        return String.format(
                "Severe weather conditions may strongly affect traffic conditions. Current condition: %s. Weather impact score: %.2f.",
                condition,
                score);
    }

    private double roundAndClamp(
            double value) {

        double clamped =
                Math.max(
                        0.0,
                        Math.min(100.0, value));

        return BigDecimal
                .valueOf(clamped)
                .setScale(
                        2,
                        RoundingMode.HALF_UP)
                .doubleValue();
    }

    private Double toDouble(
            BigDecimal value) {

        return value == null
                ? null
                : value.doubleValue();
    }
}
