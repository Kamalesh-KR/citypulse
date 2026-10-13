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
@Table(name = "traffic_predictions")
public class TrafficPrediction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "road_id", nullable = false)
    private Road road;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    @Column(name = "prediction_time", nullable = false)
    private LocalDateTime predictionTime;

    @Column(name = "predicted_vehicle_count")
    private Integer predictedVehicleCount;

    @Column(name = "predicted_average_speed", precision = 6, scale = 2)
    private BigDecimal predictedAverageSpeed;

    @Column(name = "predicted_density", precision = 6, scale = 2)
    private BigDecimal predictedDensity;

    @Column(name = "predicted_traffic_level", nullable = false, length = 20)
    private String predictedTrafficLevel;

    @Column(name = "predicted_congestion_score", precision = 6, scale = 2)
    private BigDecimal predictedCongestionScore;

    @Column(name = "confidence_score", precision = 6, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "prediction_horizon_minutes", nullable = false)
    private Integer predictionHorizonMinutes;

    @Column(name = "model_version", nullable = false, length = 50)
    private String modelVersion;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TrafficPrediction() {
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Road getRoad() {
        return road;
    }

    public void setRoad(Road road) {
        this.road = road;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public LocalDateTime getPredictionTime() {
        return predictionTime;
    }

    public void setPredictionTime(LocalDateTime predictionTime) {
        this.predictionTime = predictionTime;
    }

    public Integer getPredictedVehicleCount() {
        return predictedVehicleCount;
    }

    public void setPredictedVehicleCount(Integer predictedVehicleCount) {
        this.predictedVehicleCount = predictedVehicleCount;
    }

    public BigDecimal getPredictedAverageSpeed() {
        return predictedAverageSpeed;
    }

    public void setPredictedAverageSpeed(BigDecimal predictedAverageSpeed) {
        this.predictedAverageSpeed = predictedAverageSpeed;
    }

    public BigDecimal getPredictedDensity() {
        return predictedDensity;
    }

    public void setPredictedDensity(BigDecimal predictedDensity) {
        this.predictedDensity = predictedDensity;
    }

    public String getPredictedTrafficLevel() {
        return predictedTrafficLevel;
    }

    public void setPredictedTrafficLevel(String predictedTrafficLevel) {
        this.predictedTrafficLevel = predictedTrafficLevel;
    }

    public BigDecimal getPredictedCongestionScore() {
        return predictedCongestionScore;
    }

    public void setPredictedCongestionScore(BigDecimal predictedCongestionScore) {
        this.predictedCongestionScore = predictedCongestionScore;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getPredictionHorizonMinutes() {
        return predictionHorizonMinutes;
    }

    public void setPredictionHorizonMinutes(Integer predictionHorizonMinutes) {
        this.predictionHorizonMinutes = predictionHorizonMinutes;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
