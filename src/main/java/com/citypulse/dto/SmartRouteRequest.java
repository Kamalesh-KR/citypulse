package com.citypulse.dto;

import jakarta.validation.constraints.NotNull;

public class SmartRouteRequest {

@NotNull(message = "Start location ID is required")
private Long startLocationId;

@NotNull(message = "Destination location ID is required")
private Long destinationLocationId;

public SmartRouteRequest() {
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
