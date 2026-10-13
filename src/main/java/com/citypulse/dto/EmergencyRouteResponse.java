package com.citypulse.dto;

public class EmergencyRouteResponse {

    private Long id;
    private String routeName;
    private String emergencyType;
    private Long startLocationId;
    private Long destinationLocationId;
    private Double estimatedDistanceKm;
    private Double estimatedTimeMinutes;
    private String trafficLevel;
    private String priorityStatus;
    private String simulationStatus;

    public EmergencyRouteResponse() {
    }

    public EmergencyRouteResponse(
            Long id,
            String routeName,
            String emergencyType,
            Long startLocationId,
            Long destinationLocationId,
            Double estimatedDistanceKm,
            Double estimatedTimeMinutes,
            String trafficLevel,
            String priorityStatus,
            String simulationStatus) {

        this.id = id;
        this.routeName = routeName;
        this.emergencyType = emergencyType;
        this.startLocationId = startLocationId;
        this.destinationLocationId = destinationLocationId;
        this.estimatedDistanceKm = estimatedDistanceKm;
        this.estimatedTimeMinutes = estimatedTimeMinutes;
        this.trafficLevel = trafficLevel;
        this.priorityStatus = priorityStatus;
        this.simulationStatus = simulationStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
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

    public Long getDestinationLocationId() {
        return destinationLocationId;
    }

    public void setDestinationLocationId(Long destinationLocationId) {
        this.destinationLocationId = destinationLocationId;
    }

    public Double getEstimatedDistanceKm() {
        return estimatedDistanceKm;
    }

    public void setEstimatedDistanceKm(Double estimatedDistanceKm) {
        this.estimatedDistanceKm = estimatedDistanceKm;
    }

    public Double getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(Double estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public String getTrafficLevel() {
        return trafficLevel;
    }

    public void setTrafficLevel(String trafficLevel) {
        this.trafficLevel = trafficLevel;
    }

    public String getPriorityStatus() {
        return priorityStatus;
    }

    public void setPriorityStatus(String priorityStatus) {
        this.priorityStatus = priorityStatus;
    }

    public String getSimulationStatus() {
        return simulationStatus;
    }

    public void setSimulationStatus(String simulationStatus) {
        this.simulationStatus = simulationStatus;
    }
}