package com.citypulse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "route_analysis")
public class RouteAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "start_location_id", nullable = false)
    private Location startLocation;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "destination_location_id", nullable = false)
    private Location destinationLocation;

    @Column(name = "route_name", nullable = false, length = 200)
    private String routeName;

    @Column(name = "distance_km", precision = 8, scale = 2)
    private BigDecimal distanceKm;

    @Column(name = "estimated_time_minutes", precision = 8, scale = 2)
    private BigDecimal estimatedTimeMinutes;

    @Column(name = "current_traffic_level", nullable = false, length = 20)
    private String currentTrafficLevel;

    @Column(name = "predicted_traffic_level", nullable = false, length = 20)
    private String predictedTrafficLevel;

    @Column(name = "congestion_score", precision = 6, scale = 2)
    private BigDecimal congestionScore;

    @Column(name = "route_score", precision = 6, scale = 2)
    private BigDecimal routeScore;

    @Column(name = "recommendation_reason", columnDefinition = "TEXT")
    private String recommendationReason;

    @Column(name = "analysis_type", nullable = false, length = 30)
    private String analysisType;

    @Column(name = "analyzed_at", nullable = false)
    private LocalDateTime analyzedAt;

    public RouteAnalysis() {
    }

    @PrePersist
    protected void onCreate() {
        if (analyzedAt == null) {
            analyzedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Location getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(Location startLocation) {
        this.startLocation = startLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(Location destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public BigDecimal getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(BigDecimal distanceKm) {
        this.distanceKm = distanceKm;
    }

    public BigDecimal getEstimatedTimeMinutes() {
        return estimatedTimeMinutes;
    }

    public void setEstimatedTimeMinutes(BigDecimal estimatedTimeMinutes) {
        this.estimatedTimeMinutes = estimatedTimeMinutes;
    }

    public String getCurrentTrafficLevel() {
        return currentTrafficLevel;
    }

    public void setCurrentTrafficLevel(String currentTrafficLevel) {
        this.currentTrafficLevel = currentTrafficLevel;
    }

    public String getPredictedTrafficLevel() {
        return predictedTrafficLevel;
    }

    public void setPredictedTrafficLevel(String predictedTrafficLevel) {
        this.predictedTrafficLevel = predictedTrafficLevel;
    }

    public BigDecimal getCongestionScore() {
        return congestionScore;
    }

    public void setCongestionScore(BigDecimal congestionScore) {
        this.congestionScore = congestionScore;
    }

    public BigDecimal getRouteScore() {
        return routeScore;
    }

    public void setRouteScore(BigDecimal routeScore) {
        this.routeScore = routeScore;
    }

    public String getRecommendationReason() {
        return recommendationReason;
    }

    public void setRecommendationReason(String recommendationReason) {
        this.recommendationReason = recommendationReason;
    }

    public String getAnalysisType() {
        return analysisType;
    }

    public void setAnalysisType(String analysisType) {
        this.analysisType = analysisType;
    }

    public LocalDateTime getAnalyzedAt() {
        return analyzedAt;
    }

    public void setAnalyzedAt(LocalDateTime analyzedAt) {
        this.analyzedAt = analyzedAt;
    }
}
