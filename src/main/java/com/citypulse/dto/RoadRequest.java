package com.citypulse.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class RoadRequest {

    @NotBlank(message = "Road name is required")
    private String name;

    @NotBlank(message = "Road code is required")
    private String roadCode;

    private String startLocation;

    private String endLocation;

    @NotNull(message = "Length is required")
    @Positive(message = "Length must be positive")
    private Double lengthKm;

    private Integer lanes;

    private Double speedLimit;

    private Boolean active;

    public RoadRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRoadCode() {
        return roadCode;
    }

    public void setRoadCode(String roadCode) {
        this.roadCode = roadCode;
    }

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public Double getLengthKm() {
        return lengthKm;
    }

    public void setLengthKm(Double lengthKm) {
        this.lengthKm = lengthKm;
    }

    public Integer getLanes() {
        return lanes;
    }

    public void setLanes(Integer lanes) {
        this.lanes = lanes;
    }

    public Double getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(Double speedLimit) {
        this.speedLimit = speedLimit;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}