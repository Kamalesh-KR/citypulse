package com.citypulse.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class TrafficRecordRequest {

    @NotNull(message = "Road ID is required")
    private Long roadId;

    @NotNull(message = "Location ID is required")
    private Long locationId;

    @NotNull(message = "Vehicle count is required")
    @Min(value = 0, message = "Vehicle count cannot be negative")
    private Integer vehicleCount;

    @DecimalMin(value = "0.0", message = "Average speed cannot be negative")
    private Double averageSpeed;

    @DecimalMin(value = "0.0", message = "Traffic density cannot be negative")
    @DecimalMax(value = "100.0", message = "Traffic density cannot exceed 100")
    private Double trafficDensity;

    @NotBlank(message = "Traffic level is required")
    private String trafficLevel;

    @DecimalMin(value = "0.0", message = "Congestion score cannot be negative")
    @DecimalMax(value = "100.0", message = "Congestion score cannot exceed 100")
    private Double congestionScore;

    private LocalDateTime recordedAt;

    @NotBlank(message = "Data source is required")
    private String dataSource;

    public TrafficRecordRequest() {
    }

    public Long getRoadId() {
        return roadId;
    }

    public void setRoadId(Long roadId) {
        this.roadId = roadId;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Integer getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(Integer vehicleCount) {
        this.vehicleCount = vehicleCount;
    }

    public Double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Double getTrafficDensity() {
        return trafficDensity;
    }

    public void setTrafficDensity(Double trafficDensity) {
        this.trafficDensity = trafficDensity;
    }

    public String getTrafficLevel() {
        return trafficLevel;
    }

    public void setTrafficLevel(String trafficLevel) {
        this.trafficLevel = trafficLevel;
    }

    public Double getCongestionScore() {
        return congestionScore;
    }

    public void setCongestionScore(Double congestionScore) {
        this.congestionScore = congestionScore;
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
