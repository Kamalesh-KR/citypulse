package com.citypulse.model;

import jakarta.persistence.*;

/**
 * ============================================================
 * CITYPULSE — DISTRICT
 * ============================================================
 * An Indian district belonging to a State.
 * Each district has a headquarters city (used as the default
 * location for traffic simulation in that district).
 */
@Entity
@Table(name = "districts",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"state_id", "name"}))
public class District {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "headquarters_name", length = 100)
    private String headquartersName;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @Column(name = "location_id")
    private Long locationId;  // FK to Location (district HQ)

    @Column(nullable = false)
    private Boolean active = true;

    public District() {
    }

    public District(String name, String headquartersName,
                    Double latitude, Double longitude, State state) {
        this.name = name;
        this.headquartersName = headquartersName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.state = state;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getHeadquartersName() { return headquartersName; }
    public void setHeadquartersName(String headquartersName) { this.headquartersName = headquartersName; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public State getState() { return state; }
    public void setState(State state) { this.state = state; }

    public Long getLocationId() { return locationId; }
    public void setLocationId(Long locationId) { this.locationId = locationId; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}