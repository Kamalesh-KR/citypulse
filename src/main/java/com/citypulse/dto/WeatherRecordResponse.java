package com.citypulse.dto;

import java.time.LocalDateTime;

public class WeatherRecordResponse {

    private Long id;
    private Long locationId;
    private Double temperature;
    private Double humidity;
    private Double rainfall;
    private Double windSpeed;
    private String weatherCondition;
    private Double visibility;
    private LocalDateTime recordedAt;
    private String dataSource;

    public WeatherRecordResponse() {
    }

    public WeatherRecordResponse(
            Long id,
            Long locationId,
            Double temperature,
            Double humidity,
            Double rainfall,
            Double windSpeed,
            String weatherCondition,
            Double visibility,
            LocalDateTime recordedAt,
            String dataSource) {

        this.id = id;
        this.locationId = locationId;
        this.temperature = temperature;
        this.humidity = humidity;
        this.rainfall = rainfall;
        this.windSpeed = windSpeed;
        this.weatherCondition = weatherCondition;
        this.visibility = visibility;
        this.recordedAt = recordedAt;
        this.dataSource = dataSource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
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

    public String getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(String weatherCondition) {
        this.weatherCondition = weatherCondition;
    }

    public Double getVisibility() {
        return visibility;
    }

    public void setVisibility(Double visibility) {
        this.visibility = visibility;
    }

    public LocalDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(LocalDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }
}