package com.citypulse.dto;

import java.time.LocalDateTime;
import java.util.Map;

public class TrafficHistoricalAnalyticsResponse {

    private Long roadId;
    private Long locationId;

    private Integer totalRecords;

    private Double averageVehicleCount;
    private Double averageSpeed;
    private Double averageDensity;
    private Double averageCongestionScore;

    private Double minimumSpeed;
    private Double maximumSpeed;

    private Double minimumDensity;
    private Double maximumDensity;

    private Map<String, Integer> trafficLevelDistribution;

    private LocalDateTime analysisStart;
    private LocalDateTime analysisEnd;

    public TrafficHistoricalAnalyticsResponse() {
    }

    public TrafficHistoricalAnalyticsResponse(
            Long roadId,
            Long locationId,
            Integer totalRecords,
            Double averageVehicleCount,
            Double averageSpeed,
            Double averageDensity,
            Double averageCongestionScore,
            Double minimumSpeed,
            Double maximumSpeed,
            Double minimumDensity,
            Double maximumDensity,
            Map<String, Integer> trafficLevelDistribution,
            LocalDateTime analysisStart,
            LocalDateTime analysisEnd) {

        this.roadId = roadId;
        this.locationId = locationId;
        this.totalRecords = totalRecords;
        this.averageVehicleCount = averageVehicleCount;
        this.averageSpeed = averageSpeed;
        this.averageDensity = averageDensity;
        this.averageCongestionScore = averageCongestionScore;
        this.minimumSpeed = minimumSpeed;
        this.maximumSpeed = maximumSpeed;
        this.minimumDensity = minimumDensity;
        this.maximumDensity = maximumDensity;
        this.trafficLevelDistribution = trafficLevelDistribution;
        this.analysisStart = analysisStart;
        this.analysisEnd = analysisEnd;
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

    public Integer getTotalRecords() {
        return totalRecords;
    }

    public void setTotalRecords(Integer totalRecords) {
        this.totalRecords = totalRecords;
    }

    public Double getAverageVehicleCount() {
        return averageVehicleCount;
    }

    public void setAverageVehicleCount(Double averageVehicleCount) {
        this.averageVehicleCount = averageVehicleCount;
    }

    public Double getAverageSpeed() {
        return averageSpeed;
    }

    public void setAverageSpeed(Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public Double getAverageDensity() {
        return averageDensity;
    }

    public void setAverageDensity(Double averageDensity) {
        this.averageDensity = averageDensity;
    }

    public Double getAverageCongestionScore() {
        return averageCongestionScore;
    }

    public void setAverageCongestionScore(Double averageCongestionScore) {
        this.averageCongestionScore = averageCongestionScore;
    }

    public Double getMinimumSpeed() {
        return minimumSpeed;
    }

    public void setMinimumSpeed(Double minimumSpeed) {
        this.minimumSpeed = minimumSpeed;
    }

    public Double getMaximumSpeed() {
        return maximumSpeed;
    }

    public void setMaximumSpeed(Double maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

    public Double getMinimumDensity() {
        return minimumDensity;
    }

    public void setMinimumDensity(Double minimumDensity) {
        this.minimumDensity = minimumDensity;
    }

    public Double getMaximumDensity() {
        return maximumDensity;
    }

    public void setMaximumDensity(Double maximumDensity) {
        this.maximumDensity = maximumDensity;
    }

    public Map<String, Integer> getTrafficLevelDistribution() {
        return trafficLevelDistribution;
    }

    public void setTrafficLevelDistribution(
            Map<String, Integer> trafficLevelDistribution) {

        this.trafficLevelDistribution =
                trafficLevelDistribution;
    }

    public LocalDateTime getAnalysisStart() {
        return analysisStart;
    }

    public void setAnalysisStart(LocalDateTime analysisStart) {
        this.analysisStart = analysisStart;
    }

    public LocalDateTime getAnalysisEnd() {
        return analysisEnd;
    }

    public void setAnalysisEnd(LocalDateTime analysisEnd) {
        this.analysisEnd = analysisEnd;
    }
}
