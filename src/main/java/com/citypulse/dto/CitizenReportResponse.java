package com.citypulse.dto;

import java.time.LocalDateTime;

public class CitizenReportResponse {

    private Long id;
    private Long userId;
    private Long roadId;
    private Long locationId;
    private String reportType;
    private String description;
    private Double latitude;
    private Double longitude;
    private String status;
    private LocalDateTime reportedAt;
    private LocalDateTime reviewedAt;

    public CitizenReportResponse() {
    }

    public CitizenReportResponse(
            Long id,
            Long userId,
            Long roadId,
            Long locationId,
            String reportType,
            String description,
            Double latitude,
            Double longitude,
            String status,
            LocalDateTime reportedAt,
            LocalDateTime reviewedAt) {

        this.id = id;
        this.userId = userId;
        this.roadId = roadId;
        this.locationId = locationId;
        this.reportType = reportType;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
        this.reportedAt = reportedAt;
        this.reviewedAt = reviewedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(LocalDateTime reportedAt) {
        this.reportedAt = reportedAt;
    }

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }
}