package com.citypulse.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class RouteAnalysisRequest {

    @NotNull(message = "Start location ID is required")
    private Long startLocationId;

    @NotNull(message = "Destination location ID is required")
    private Long destinationLocationId;

    private String routeName;
    private Double distanceKm;
    private Double estimatedTimeMinutes;
    private String currentTrafficLevel;
    private String predictedTrafficLevel;
    private Double congestionScore;
    private Double routeScore;
    private String recommendationReason;
    private String analysisType;
    private LocalDateTime analyzedAt;

    public RouteAnalysisRequest() {
    }

    public Long getStartLocationId() {
        return startLocationId;
    }

    public void setStartLocationId(Long startLocationId) {
        this.startLocationId = startLocationId;
    }

    public Long getDestinationLocationId() {
        return destinationLocationId;
    }

    public void setDestinationLocationId(Long destinationLocationId) {
        this.destinationLocationId = destinationLocationId;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public Double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(Double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public Double getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Double estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public String getCurrentTrafficLevel() {
        return currentTrafficLevel;
    }

    public void setCurrentTrafficLevel(String currentTrafficLevel) {
        this.currentTrafficLevel = currentTrafficLevel;
    }

    public String getPredictedTrafficLevel() {
        return predictedTrafficLevel;
    }

    public void setPredictedTrafficLevel(String predictedTrafficLevel) {
        this.predictedTrafficLevel = predictedTrafficLevel;
    }

    public Double getCongestionScore() {
        return congestionScore;
    }

    public void setCongestionScore(Double congestionScore) {
        this.congestionScore = congestionScore;
    }

    public Double getRouteScore() {
        return routeScore;
    }

    public void setRouteScore(Double routeScore) {
        this.routeScore = routeScore;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}