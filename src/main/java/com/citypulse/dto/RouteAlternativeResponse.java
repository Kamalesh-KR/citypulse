package com.citypulse.dto;

public class RouteAlternativeResponse {

    private String routeName;
    private Double distanceKm;
    private Double estimatedTimeMinutes;
    private String geometry;
    private Boolean trafficAvailable;
    private String currentTrafficLevel;
    private String predictedTrafficLevel;
    private Double congestionScore;
    private Double routeScore;
    private String recommendationReason;
    private Boolean recommended;

    public RouteAlternativeResponse() {
    }

    public RouteAlternativeResponse(
            String routeName,
            Double distanceKm,
            Double estimatedTimeMinutes,
            String geometry,
            Boolean trafficAvailable,
            String currentTrafficLevel,
            String predictedTrafficLevel,
            Double congestionScore,
            Double routeScore,
            String recommendationReason,
            Boolean recommended) {

        this.routeName = routeName;
        this.distanceKm = distanceKm;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.geometry = geometry;
        this.trafficAvailable = trafficAvailable;
        this.currentTrafficLevel = currentTrafficLevel;
        this.predictedTrafficLevel = predictedTrafficLevel;
        this.congestionScore = congestionScore;
        this.routeScore = routeScore;
        this.recommendationReason = recommendationReason;
        this.recommended = recommended;
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

    public String getGeometry() {
        return geometry;
    }

    public void setGeometry(String geometry) {
        this.geometry = geometry;
    }

    public Boolean getTrafficAvailable() {
        return trafficAvailable;
    }

    public void setTrafficAvailable(Boolean trafficAvailable) {
        this.trafficAvailable = trafficAvailable;
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

    public Boolean getRecommended() {
        return recommended;
    }

    public void setRecommended(Boolean recommended) {
        this.recommended = recommended;
    }
}