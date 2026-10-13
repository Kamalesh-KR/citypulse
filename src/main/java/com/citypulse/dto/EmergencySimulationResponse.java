package com.citypulse.dto;

public class EmergencySimulationResponse {

    private String emergencyType;

    private Long startLocationId;
    private String startLocationName;

    private Long destinationLocationId;
    private String destinationLocationName;

    private Double routeDistanceKm;

    private Double normalTravelTimeMinutes;

    private Double emergencyTravelTimeMinutes;

    private String trafficLevel;

    private Double congestionScore;

    private String priorityStatus;

    private String signalPriority;

    private String simulationStatus;

    private String message;

    public EmergencySimulationResponse() {
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public void setEmergencyType(String emergencyType) {
        this.emergencyType = emergencyType;
    }

    public Long getStartLocationId() {
        return startLocationId;
    }

    public void setStartLocationId(Long startLocationId) {
        this.startLocationId = startLocationId;
    }

    public String getStartLocationName() {
        return startLocationName;
    }

    public void setStartLocationName(String startLocationName) {
        this.startLocationName = startLocationName;
    }

    public Long getDestinationLocationId() {
        return destinationLocationId;
    }

    public void setDestinationLocationId(Long destinationLocationId) {
        this.destinationLocationId = destinationLocationId;
    }

    public String getDestinationLocationName() {
        return destinationLocationName;
    }

    public void setDestinationLocationName(String destinationLocationName) {
        this.destinationLocationName = destinationLocationName;
    }

    public Double getRouteDistanceKm() {
        return routeDistanceKm;
    }

    public void setRouteDistanceKm(Double routeDistanceKm) {
        this.routeDistanceKm = routeDistanceKm;
    }

    public Double getNormalTravelTimeMinutes() {
        return normalTravelTimeMinutes;
    }

    public void setNormalTravelTimeMinutes(Double normalTravelTimeMinutes) {
        this.normalTravelTimeMinutes = normalTravelTimeMinutes;
    }

    public Double getEmergencyTravelTimeMinutes() {
        return emergencyTravelTimeMinutes;
    }

    public void setEmergencyTravelTimeMinutes(
            Double emergencyTravelTimeMinutes) {

        this.emergencyTravelTimeMinutes =
                emergencyTravelTimeMinutes;
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

    public String getPriorityStatus() {
        return priorityStatus;
    }

    public void setPriorityStatus(String priorityStatus) {
        this.priorityStatus = priorityStatus;
    }

    public String getSignalPriority() {
        return signalPriority;
    }

    public void setSignalPriority(String signalPriority) {
        this.signalPriority = signalPriority;
    }

    public String getSimulationStatus() {
        return simulationStatus;
    }

    public void setSimulationStatus(String simulationStatus) {
        this.simulationStatus = simulationStatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}