package com.citypulse.dto;

public class TrafficAnalysisResponse {

    private Long roadId;
    private Long locationId;
    private Integer vehicleCount;
    private Double averageSpeed;
    private Double trafficDensity;
    private String trafficLevel;
    private Double congestionScore;
    private String congestionStatus;
    private String analysisMessage;

    public TrafficAnalysisResponse() {
    }

    public TrafficAnalysisResponse(
            Long roadId,
            Long locationId,
            Integer vehicleCount,
            Double averageSpeed,
            Double trafficDensity,
            String trafficLevel,
            Double congestionScore,
            String congestionStatus,
            String analysisMessage) {

        this.roadId = roadId;
        this.locationId = locationId;
        this.vehicleCount = vehicleCount;
        this.averageSpeed = averageSpeed;
        this.trafficDensity = trafficDensity;
        this.trafficLevel = trafficLevel;
        this.congestionScore = congestionScore;
        this.congestionStatus = congestionStatus;
        this.analysisMessage = analysisMessage;
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

    public String getCongestionStatus() {
        return congestionStatus;
    }

    public void setCongestionStatus(String congestionStatus) {
        this.congestionStatus = congestionStatus;
    }

    public String getAnalysisMessage() {
        return analysisMessage;
    }

    public void setAnalysisMessage(String analysisMessage) {
        this.analysisMessage = analysisMessage;
    }
}