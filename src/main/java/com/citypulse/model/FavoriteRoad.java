package com.citypulse.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * ============================================================
 * FAVORITE ROAD (C4)
 * ============================================================
 *
 * Represents a road a user has starred.
 *
 * One row per (user, road) pair. The road data is denormalized
 * (name, code) so the favorite survives even if the road is
 * deleted. Unique constraint prevents duplicates.
 */
@Entity
@Table(
    name = "favorite_roads",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_favorite_user_road",
        columnNames = {"user_id", "road_id"}
    ),
    indexes = {
        @Index(name = "idx_favorite_road_user", columnList = "user_id")
    }
)
public class FavoriteRoad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "road_id", nullable = false)
    private Long roadId;

    @Column(name = "road_name", length = 255)
    private String roadName;

    @Column(name = "road_code", length = 50)
    private String roadCode;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public FavoriteRoad() {
    }

    public FavoriteRoad(
            Long userId,
            Long roadId,
            String roadName,
            String roadCode
    ) {
        this.userId = userId;
        this.roadId = roadId;
        this.roadName = roadName;
        this.roadCode = roadCode;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getRoadId() { return roadId; }
    public void setRoadId(Long roadId) { this.roadId = roadId; }

    public String getRoadName() { return roadName; }
    public void setRoadName(String roadName) { this.roadName = roadName; }

    public String getRoadCode() { return roadCode; }
    public void setRoadCode(String roadCode) { this.roadCode = roadCode; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}