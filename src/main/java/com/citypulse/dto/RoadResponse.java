package com.citypulse.dto;

public class RoadResponse {

    private Long id;
    private String name;
    private String roadCode;
    private String startLocation;
    private String endLocation;
    private Double lengthKm;
    private Integer lanes;
    private Double speedLimit;
    private Boolean active;

    public RoadResponse() {
    }

    public RoadResponse(
            Long id,
            String name,
            String roadCode,
            String startLocation,
            String endLocation,
            Double lengthKm,
            Integer lanes,
            Double speedLimit,
            Boolean active) {

        this.id = id;
        this.name = name;
        this.roadCode = roadCode;
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.lengthKm = lengthKm;
        this.lanes = lanes;
        this.speedLimit = speedLimit;
        this.active = active;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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