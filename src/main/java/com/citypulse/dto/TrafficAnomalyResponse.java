package com.citypulse.dto;

public class TrafficAnomalyResponse {

    private Long roadId;
    private Long locationId;

    private Double currentSpeed;
    private Double expectedSpeed;

    private Double currentDensity;
    private Double expectedDensity;

    private Double speedChangePercentage;
    private Double densityChangePercentage;

    private Boolean anomalyDetected;
    private String anomalySeverity;
    private String anomalyMessage;

    public TrafficAnomalyResponse() {
    }

    public TrafficAnomalyResponse(
            Long roadId,
            Long locationId,
            Double currentSpeed,
            Double expectedSpeed,
            Double currentDensity,
            Double expectedDensity,
            Double speedChangePercentage,
            Double densityChangePercentage,
            Boolean anomalyDetected,
            String anomalySeverity,
            String anomalyMessage) {

        this.roadId = roadId;
        this.locationId = locationId;
        this.currentSpeed = currentSpeed;
        this.expectedSpeed = expectedSpeed;
        this.currentDensity = currentDensity;
        this.expectedDensity = expectedDensity;
        this.speedChangePercentage = speedChangePercentage;
        this.densityChangePercentage = densityChangePercentage;
        this.anomalyDetected = anomalyDetected;
        this.anomalySeverity = anomalySeverity;
        this.anomalyMessage = anomalyMessage;
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

    public Double getCurrentSpeed() {
        return currentSpeed;
    }

    public void setCurrentSpeed(Double currentSpeed) {
        this.currentSpeed = currentSpeed;
    }

    public Double getExpectedSpeed() {
        return expectedSpeed;
    }

    public void setExpectedSpeed(Double expectedSpeed) {
        this.expectedSpeed = expectedSpeed;
    }

    public Double getCurrentDensity() {
        return currentDensity;
    }

    public void setCurrentDensity(Double currentDensity) {
        this.currentDensity = currentDensity;
    }

    public Double getExpectedDensity() {
        return expectedDensity;
    }

    public void setExpectedDensity(Double expectedDensity) {
        this.expectedDensity = expectedDensity;
    }

    public Double getSpeedChangePercentage() {
        return speedChangePercentage;
    }

    public void setSpeedChangePercentage(Double speedChangePercentage) {
        this.speedChangePercentage = speedChangePercentage;
    }

    public Double getDensityChangePercentage() {
        return densityChangePercentage;
    }

    public void setDensityChangePercentage(Double densityChangePercentage) {
        this.densityChangePercentage = densityChangePercentage;
    }

    public Boolean getAnomalyDetected() {
        return anomalyDetected;
    }

    public void setAnomalyDetected(Boolean anomalyDetected) {
        this.anomalyDetected = anomalyDetected;
    }

    public String getAnomalySeverity() {
        return anomalySeverity;
    }

    public void setAnomalySeverity(String anomalySeverity) {
        this.anomalySeverity = anomalySeverity;
    }

    public String getAnomalyMessage() {
        return anomalyMessage;
    }

    public void setAnomalyMessage(String anomalyMessage) {
        this.anomalyMessage = anomalyMessage;
    }
}
