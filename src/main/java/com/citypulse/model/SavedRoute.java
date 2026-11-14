package com.citypulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ============================================================
 * SAVED ROUTE (C4)
 * ============================================================
 *
 * Represents a user's saved route between two locations.
 *
 * Stored per-user so different users have different lists.
 * Users can save any route they've queried; the route data
 * is denormalized (from/to names) so it survives even if the
 * locations are later deleted.
 */
@Entity
@Table(
    name = "saved_routes",
    indexes = {
        @Index(name = "idx_saved_routes_user", columnList = "user_id")
    }
)
public class SavedRoute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "route_name", length = 120)
    private String routeName;

    @Column(name = "from_location_id")
    private Long fromLocationId;

    @Column(name = "from_location_name", length = 255)
    private String fromLocationName;

    @Column(name = "to_location_id")
    private Long toLocationId;

    @Column(name = "to_location_name", length = 255)
    private String toLocationName;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "estimated_minutes")
    private Double estimatedMinutes;

    @Column(name = "traffic_level_at_save", length = 20)
    private String trafficLevelAtSave;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public SavedRoute() {
    }

    public SavedRoute(
            Long userId,
            String routeName,
            Long fromLocationId,
            String fromLocationName,
            Long toLocationId,
            String toLocationName,
            Double distanceKm,
            Double estimatedMinutes,
            String trafficLevelAtSave
    ) {
        this.userId = userId;
        this.routeName = routeName;
        this.fromLocationId = fromLocationId;
        this.fromLocationName = fromLocationName;
        this.toLocationId = toLocationId;
        this.toLocationName = toLocationName;
        this.distanceKm = distanceKm;
        this.estimatedMinutes = estimatedMinutes;
        this.trafficLevelAtSave = trafficLevelAtSave;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRouteName() { return routeName; }
    public void setRouteName(String routeName) { this.routeName = routeName; }

    public Long getFromLocationId() { return fromLocationId; }
    public void setFromLocationId(Long fromLocationId) { this.fromLocationId = fromLocationId; }

    public String getFromLocationName() { return fromLocationName; }
    public void setFromLocationName(String fromLocationName) { this.fromLocationName = fromLocationName; }

    public Long getToLocationId() { return toLocationId; }
    public void setToLocationId(Long toLocationId) { this.toLocationId = toLocationId; }

    public String getToLocationName() { return toLocationName; }
    public void setToLocationName(String toLocationName) { this.toLocationName = toLocationName; }

    public Double getDistanceKm() { return distanceKm; }
    public void setDistanceKm(Double distanceKm) { this.distanceKm = distanceKm; }

    public Double getEstimatedMinutes() { return estimatedMinutes; }
    public void setEstimatedMinutes(Double estimatedMinutes) { this.estimatedMinutes = estimatedMinutes; }

    public String getTrafficLevelAtSave() { return trafficLevelAtSave; }
    public void setTrafficLevelAtSave(String trafficLevelAtSave) { this.trafficLevelAtSave = trafficLevelAtSave; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}