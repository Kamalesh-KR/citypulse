package com.citypulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EmergencySimulationRequest {

    @NotBlank(message = "Emergency type is required")
    private String emergencyType;

    @NotNull(message = "Start location ID is required")
    private Long startLocationId;

    @NotNull(message = "Destination location ID is required")
    private Long destinationLocationId;

    public EmergencySimulationRequest() {
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
}