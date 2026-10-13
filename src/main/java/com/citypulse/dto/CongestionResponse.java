package com.citypulse.dto;

public class CongestionResponse {

    private Long roadId;
    private Long locationId;
    private String trafficLevel;
    private Double trafficDensity;
    private Double congestionScore;
    private String congestionStatus;
    private String congestionMessage;

    public CongestionResponse() {
    }

    public CongestionResponse(
            Long roadId,
            Long locationId,
            String trafficLevel,
            Double trafficDensity,
            Double congestionScore,
            String congestionStatus,
            String congestionMessage) {

        this.roadId = roadId;
        this.locationId = locationId;
        this.trafficLevel = trafficLevel;
        this.trafficDensity = trafficDensity;
        this.congestionScore = congestionScore;
        this.congestionStatus = congestionStatus;
        this.congestionMessage = congestionMessage;
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

    public String getTrafficLevel() {
        return trafficLevel;
    }

    public void setTrafficLevel(String trafficLevel) {
        this.trafficLevel = trafficLevel;
    }

    public Double getTrafficDensity() {
        return trafficDensity;
    }

    public void setTrafficDensity(Double trafficDensity) {
        this.trafficDensity = trafficDensity;
    }

    public Double getCongestionScore() {
        return congestionScore;
    }

    public void setCongestionScore(Double congestionScore) {
        this.congestionScore = congestionScore;
    }

    public String getCongestionStatus() {
        return congestionStatus;
    }

    public void setCongestionStatus(String congestionStatus) {
        this.congestionStatus = congestionStatus;
    }

    public String getCongestionMessage() {
        return congestionMessage;
    }

    public void setCongestionMessage(String congestionMessage) {
        this.congestionMessage = congestionMessage;
    }
}
