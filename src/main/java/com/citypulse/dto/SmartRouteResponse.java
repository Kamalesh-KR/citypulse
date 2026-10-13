package com.citypulse.dto;

public class SmartRouteResponse {

private Long routeId;
private String routeName;

private Long startLocationId;
private Long destinationLocationId;

private Double distanceKm;
private Double averageSpeed;
private Double estimatedTimeMinutes;

private String currentTrafficLevel;
private String predictedTrafficLevel;

private Double congestionScore;
private Double routeScore;

private String recommendationReason;
private Boolean recommended;

public SmartRouteResponse() {
}

public SmartRouteResponse(
        Long routeId,
        String routeName,
        Long startLocationId,
        Long destinationLocationId,
        Double distanceKm,
        Double averageSpeed,
        Double estimatedTimeMinutes,
        String currentTrafficLevel,
        String predictedTrafficLevel,
        Double congestionScore,
        Double routeScore,
        String recommendationReason,
        Boolean recommended) {

    this.routeId = routeId;
    this.routeName = routeName;
    this.startLocationId = startLocationId;
    this.destinationLocationId = destinationLocationId;
    this.distanceKm = distanceKm;
    this.averageSpeed = averageSpeed;
    this.estimatedTimeMinutes = estimatedTimeMinutes;
    this.currentTrafficLevel = currentTrafficLevel;
    this.predictedTrafficLevel = predictedTrafficLevel;
    this.congestionScore = congestionScore;
    this.routeScore = routeScore;
    this.recommendationReason = recommendationReason;
    this.recommended = recommended;
}

public Long getRouteId() {
    return routeId;
}

public void setRouteId(Long routeId) {
    this.routeId = routeId;
}

public String getRouteName() {
    return routeName;
}

public void setRouteName(String routeName) {
    this.routeName = routeName;
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

public Double getDistanceKm() {
    return distanceKm;
}

public void setDistanceKm(Double distanceKm) {
    this.distanceKm = distanceKm;
}

public Double getAverageSpeed() {
    return averageSpeed;
}

public void setAverageSpeed(Double averageSpeed) {
    this.averageSpeed = averageSpeed;
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

public Boolean getRecommended() {
    return recommended;
}

public void setRecommended(Boolean recommended) {
    this.recommended = recommended;
}

}
