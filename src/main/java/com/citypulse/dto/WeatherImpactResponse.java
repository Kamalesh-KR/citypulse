package com.citypulse.dto;

import java.time.LocalDateTime;

public class WeatherImpactResponse {

    private Long locationId;
    private String locationName;

    private Long weatherRecordId;
    private LocalDateTime recordedAt;

    private Double temperature;
    private Double humidity;
    private Double rainfall;
    private Double windSpeed;
    private Double visibility;

    private String weatherCondition;

    private Double weatherImpactScore;
    private String weatherImpactLevel;
    private String impactSummary;

    public WeatherImpactResponse() {
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public Long getWeatherRecordId() {
        return weatherRecordId;
    }

    public void setWeatherRecordId(Long weatherRecordId) {
        this.weatherRecordId = weatherRecordId;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public Double getRainfall() {
        return rainfall;
    }

    public void setRainfall(Double rainfall) {
        this.rainfall = rainfall;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public Double getVisibility() {
        return visibility;
    }

    public void setVisibility(Double visibility) {
        this.visibility = visibility;
    }

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public Double getWeatherImpactScore() {
        return weatherImpactScore;
    }

    public void setWeatherImpactScore(Double weatherImpactScore) {
        this.weatherImpactScore = weatherImpactScore;
    }

    public String getWeatherImpactLevel() {
        return weatherImpactLevel;
    }

    public void setWeatherImpactLevel(String weatherImpactLevel) {
        this.weatherImpactLevel = weatherImpactLevel;
    }

    public String getImpactSummary() {
        return impactSummary;
    }

    public void setImpactSummary(String impactSummary) {
        this.impactSummary = impactSummary;
    }
}
