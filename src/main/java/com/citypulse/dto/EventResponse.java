package com.citypulse.dto;

import java.time.LocalDateTime;

public class EventResponse {

    private Long id;
    private String name;
    private String description;
    private String eventType;
    private Long locationId;
    private Integer expectedAttendance;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String impactLevel;
    private Boolean active;

    public EventResponse() {
    }

    public EventResponse(
            Long id,
            String name,
            String description,
            String eventType,
            Long locationId,
            Integer expectedAttendance,
            LocalDateTime startTime,
            LocalDateTime endTime,
            String impactLevel,
            Boolean active) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.eventType = eventType;
        this.locationId = locationId;
        this.expectedAttendance = expectedAttendance;
        this.startTime = startTime;
        this.endTime = endTime;
        this.impactLevel = impactLevel;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Long getLocationId() {
        return locationId;
    }

    public void setLocationId(Long locationId) {
        this.locationId = locationId;
    }

    public Integer getExpectedAttendance() {
        return expectedAttendance;
    }

    public void setExpectedAttendance(Integer expectedAttendance) {
        this.expectedAttendance = expectedAttendance;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getImpactLevel() {
        return impactLevel;
    }

    public void setImpactLevel(String impactLevel) {
        this.impactLevel = impactLevel;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}