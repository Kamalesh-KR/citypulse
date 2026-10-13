package com.citypulse.dto;

import java.time.LocalDateTime;

public class TrafficRecordResponse {

    private Long id;
    private Long roadId;
    private Long locationId;
    private Integer vehicleCount;
    private Double averageSpeed;
    private Double trafficDensity;
    private String trafficLevel;
    private Double congestionScore;
    private LocalDateTime recordedAt;
    private String dataSource;

    public TrafficRecordResponse() {
    }

    public TrafficRecordResponse(
            Long id,
            Long roadId,
            Long locationId,
            Integer vehicleCount,
            Double averageSpeed,
            Double trafficDensity,
            String trafficLevel,
            Double congestionScore,
            LocalDateTime recordedAt,
            String dataSource) {

        this.id = id;
        this.roadId = roadId;
        this.locationId = locationId;
        this.vehicleCount = vehicleCount;
        this.averageSpeed = averageSpeed;
        this.trafficDensity = trafficDensity;
        this.trafficLevel = trafficLevel;
        this.congestionScore = congestionScore;
        this.recordedAt = recordedAt;
        this.dataSource = dataSource;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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