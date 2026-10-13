package com.citypulse.dto;

import java.time.LocalDateTime;

public class CongestionPredictionResponse {

    private Long roadId;
    private Long locationId;

    private LocalDateTime predictionTime;

    private Integer predictedVehicleCount;
    private Double predictedAverageSpeed;
    private Double predictedDensity;

    private String predictedTrafficLevel;
    private Double predictedCongestionScore;

    private Double confidenceScore;
    private Integer predictionHorizonMinutes;

    private String modelVersion;

    public CongestionPredictionResponse() {
    }

    public CongestionPredictionResponse(
            Long roadId,
            Long locationId,
            LocalDateTime predictionTime,
            Integer predictedVehicleCount,
            Double predictedAverageSpeed,
            Double predictedDensity,
            String predictedTrafficLevel,
            Double predictedCongestionScore,
            Double confidenceScore,
            Integer predictionHorizonMinutes,
            String modelVersion) {

        this.roadId = roadId;
        this.locationId = locationId;
        this.predictionTime = predictionTime;
        this.predictedVehicleCount = predictedVehicleCount;
        this.predictedAverageSpeed = predictedAverageSpeed;
        this.predictedDensity = predictedDensity;
        this.predictedTrafficLevel = predictedTrafficLevel;
        this.predictedCongestionScore = predictedCongestionScore;
        this.confidenceScore = confidenceScore;
        this.predictionHorizonMinutes = predictionHorizonMinutes;
        this.modelVersion = modelVersion;
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

    public LocalDateTime getPredictionTime() {
        return predictionTime;
    }

    public void setPredictionTime(LocalDateTime predictionTime) {
        this.predictionTime = predictionTime;
    }

    public Integer getPredictedVehicleCount() {
        return predictedVehicleCount;
    }

    public void setPredictedVehicleCount(Integer predictedVehicleCount) {
        this.predictedVehicleCount = predictedVehicleCount;
    }

    public Double getPredictedAverageSpeed() {
        return predictedAverageSpeed;
    }

    public void setPredictedAverageSpeed(Double predictedAverageSpeed) {
        this.predictedAverageSpeed = predictedAverageSpeed;
    }

    public Double getPredictedDensity() {
        return predictedDensity;
    }

    public void setPredictedDensity(Double predictedDensity) {
        this.predictedDensity = predictedDensity;
    }

    public String getPredictedTrafficLevel() {
        return predictedTrafficLevel;
    }

    public void setPredictedTrafficLevel(String predictedTrafficLevel) {
        this.predictedTrafficLevel = predictedTrafficLevel;
    }

    public Double getPredictedCongestionScore() {
        return predictedCongestionScore;
    }

    public void setPredictedCongestionScore(Double predictedCongestionScore) {
        this.predictedCongestionScore = predictedCongestionScore;
    }

    public Double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getPredictionHorizonMinutes() {
        return predictionHorizonMinutes;
    }

    public void setPredictionHorizonMinutes(
            Integer predictionHorizonMinutes) {

        this.predictionHorizonMinutes = predictionHorizonMinutes;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }
}
