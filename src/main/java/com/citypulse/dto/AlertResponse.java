package com.citypulse.dto;

import java.time.LocalDateTime;

public class AlertResponse {

    private Long id;
    private Long roadId;
    private Long locationId;
    private String alertType;
    private String title;
    private String message;
    private String severity;
    private Boolean active;
    private LocalDateTime expiresAt;

    public AlertResponse() {
    }

    public AlertResponse(
            Long id,
            Long roadId,
            Long locationId,
            String alertType,
            String title,
            String message,
            String severity,
            Boolean active,
            LocalDateTime expiresAt) {

        this.id = id;
        this.roadId = roadId;
        this.locationId = locationId;
        this.alertType = alertType;
        this.title = title;
        this.message = message;
        this.severity = severity;
        this.active = active;
        this.expiresAt = expiresAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getAlertType() {
        return alertType;
    }

    public void setAlertType(String alertType) {
        this.alertType = alertType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}