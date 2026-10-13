package com.citypulse.service;

import com.citypulse.model.Alert;
import com.citypulse.model.TrafficRecord;
import com.citypulse.repository.AlertRepository;
import com.citypulse.repository.TrafficRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final TrafficRecordRepository trafficRecordRepository;

    public AlertService(
            AlertRepository alertRepository,
            TrafficRecordRepository trafficRecordRepository) {

        this.alertRepository = alertRepository;
        this.trafficRecordRepository = trafficRecordRepository;
    }

    // =========================================================
    // EXISTING CRUD / QUERY METHODS
    // =========================================================

    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    public Optional<Alert> getAlertById(Long id) {
        return alertRepository.findById(id);
    }

    public List<Alert> getAlertsByRoad(Long roadId) {
        return alertRepository.findByRoadId(roadId);
    }

    public List<Alert> getAlertsByLocation(Long locationId) {
        return alertRepository.findByLocationId(locationId);
    }

    public List<Alert> getAlertsByType(String alertType) {
        return alertRepository.findByAlertTypeIgnoreCase(alertType);
    }

    public List<Alert> getAlertsBySeverity(String severity) {
        return alertRepository.findBySeverityIgnoreCase(severity);
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByActiveTrue();
    }

    public Alert createAlert(Alert alert) {
        return alertRepository.save(alert);
    }

    public Alert updateAlert(
            Long id,
            Alert updatedAlert) {

        return alertRepository.findById(id)
                .map(existingAlert -> {

                    existingAlert.setRoad(
                            updatedAlert.getRoad());

                    existingAlert.setLocation(
                            updatedAlert.getLocation());

                    existingAlert.setAlertType(
                            updatedAlert.getAlertType());

                    existingAlert.setTitle(
                            updatedAlert.getTitle());

                    existingAlert.setMessage(
                            updatedAlert.getMessage());

                    existingAlert.setSeverity(
                            updatedAlert.getSeverity());

                    existingAlert.setActive(
                            updatedAlert.getActive());

                    existingAlert.setExpiresAt(
                            updatedAlert.getExpiresAt());

                    return alertRepository.save(existingAlert);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Alert not found with id: " + id));
    }

    public void deleteAlert(Long id) {

        if (!alertRepository.existsById(id)) {
            throw new RuntimeException(
                    "Alert not found with id: " + id);
        }

        alertRepository.deleteById(id);
    }

    // =========================================================
    // SMART TRAFFIC ALERT ENGINE
    // =========================================================

    /**
     * Generates a smart traffic alert from the latest
     * traffic condition for a road.
     *
     * The alert engine checks:
     *
     * 1. Traffic level
     * 2. Congestion score
     *
     * Severe conditions create CRITICAL alerts.
     * High conditions create HIGH alerts.
     * Moderate conditions create MODERATE alerts.
     */
    public Alert generateCongestionAlert(Long roadId) {

        if (roadId == null) {
            throw new IllegalArgumentException(
                    "Road ID is required");
        }

        TrafficRecord trafficRecord =
                trafficRecordRepository
                        .findFirstByRoadIdOrderByRecordedAtDesc(roadId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No traffic data available for road: "
                                                + roadId));

        String trafficLevel =
                trafficRecord.getTrafficLevel();

        double congestionScore =
                trafficRecord.getCongestionScore() != null
                        ? trafficRecord.getCongestionScore().doubleValue()
                        : 0.0;

        String severity =
                determineSeverity(
                        trafficLevel,
                        congestionScore);

        String title =
                buildCongestionTitle(
                        trafficLevel,
                        severity);

        String message =
                buildCongestionMessage(
                        trafficRecord,
                        severity);

        Alert alert = new Alert();

        alert.setRoad(trafficRecord.getRoad());
        alert.setLocation(trafficRecord.getLocation());

        alert.setAlertType("CONGESTION");
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setSeverity(severity);
        alert.setActive(true);

        alert.setExpiresAt(
                LocalDateTime.now().plusMinutes(30));

        return alertRepository.save(alert);
    }

    /**
     * Generates an anomaly alert using the latest traffic
     * record and the same baseline thresholds used by the
     * TrafficRecordService anomaly detection module.
     *
     * Baseline:
     * Expected speed = 40 km/h
     * Expected density = 60%
     */
    public Alert generateAnomalyAlert(Long roadId) {

        if (roadId == null) {
            throw new IllegalArgumentException(
                    "Road ID is required");
        }

        TrafficRecord trafficRecord =
                trafficRecordRepository
                        .findFirstByRoadIdOrderByRecordedAtDesc(roadId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "No traffic data available for road: "
                                                + roadId));

        double expectedSpeed = 40.0;
        double expectedDensity = 60.0;

        double currentSpeed =
                trafficRecord.getAverageSpeed() != null
                        ? trafficRecord.getAverageSpeed().doubleValue()
                        : expectedSpeed;

        double currentDensity =
                trafficRecord.getTrafficDensity() != null
                        ? trafficRecord.getTrafficDensity().doubleValue()
                        : expectedDensity;

        double speedChangePercentage =
                ((currentSpeed - expectedSpeed)
                        / expectedSpeed) * 100.0;

        double densityChangePercentage =
                ((currentDensity - expectedDensity)
                        / expectedDensity) * 100.0;

        boolean anomalyDetected =
                speedChangePercentage <= -30.0
                        || densityChangePercentage >= 30.0;

        if (!anomalyDetected) {
            return null;
        }

        String severity =
                determineAnomalySeverity(
                        speedChangePercentage,
                        densityChangePercentage);

        Alert alert = new Alert();

        alert.setRoad(trafficRecord.getRoad());
        alert.setLocation(trafficRecord.getLocation());

        alert.setAlertType("ANOMALY");

        alert.setTitle(
                "Traffic Anomaly Detected");

        alert.setMessage(
                buildAnomalyMessage(
                        currentSpeed,
                        currentDensity,
                        speedChangePercentage,
                        densityChangePercentage,
                        severity));

        alert.setSeverity(severity);
        alert.setActive(true);

        alert.setExpiresAt(
                LocalDateTime.now().plusMinutes(30));

        return alertRepository.save(alert);
    }

    /**
     * Generates all applicable smart alerts for a road.
     *
     * The returned list may contain:
     *
     * - A congestion alert
     * - An anomaly alert
     *
     * If no anomaly is detected, only the congestion alert
     * is returned.
     */
    public List<Alert> generateSmartAlerts(Long roadId) {

        Alert congestionAlert =
                generateCongestionAlert(roadId);

        Alert anomalyAlert =
                generateAnomalyAlert(roadId);

        if (anomalyAlert == null) {
            return List.of(congestionAlert);
        }

        return List.of(
                congestionAlert,
                anomalyAlert
        );
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private String determineSeverity(
            String trafficLevel,
            double congestionScore) {

        if ("SEVERE".equalsIgnoreCase(trafficLevel)
                || congestionScore >= 85.0) {

            return "CRITICAL";
        }

        if ("HIGH".equalsIgnoreCase(trafficLevel)
                || congestionScore >= 65.0) {

            return "HIGH";
        }

        if ("MODERATE".equalsIgnoreCase(trafficLevel)
                || congestionScore >= 40.0) {

            return "MODERATE";
        }

        return "LOW";
    }

    private String buildCongestionTitle(
            String trafficLevel,
            String severity) {

        if ("CRITICAL".equalsIgnoreCase(severity)) {
            return "Critical Traffic Congestion";
        }

        if ("HIGH".equalsIgnoreCase(severity)) {
            return "High Traffic Congestion";
        }

        if ("MODERATE".equalsIgnoreCase(severity)) {
            return "Moderate Traffic Congestion";
        }

        return "Traffic Condition Alert";
    }

    private String buildCongestionMessage(
            TrafficRecord trafficRecord,
            String severity) {

        String roadName =
                trafficRecord.getRoad() != null
                        ? trafficRecord.getRoad().getName()
                        : "Unknown road";

        String trafficLevel =
                trafficRecord.getTrafficLevel() != null
                        ? trafficRecord.getTrafficLevel()
                        : "UNKNOWN";

        double density =
                trafficRecord.getTrafficDensity() != null
                        ? trafficRecord.getTrafficDensity().doubleValue()
                        : 0.0;

        double congestionScore =
                trafficRecord.getCongestionScore() != null
                        ? trafficRecord.getCongestionScore().doubleValue()
                        : 0.0;

        return String.format(
                "%s traffic congestion detected on %s. "
                        + "Traffic level: %s, density: %.2f%%, "
                        + "congestion score: %.2f.",
                severity,
                roadName,
                trafficLevel,
                density,
                congestionScore
        );
    }

    private String determineAnomalySeverity(
            double speedChangePercentage,
            double densityChangePercentage) {

        boolean critical =
                speedChangePercentage <= -50.0
                        || densityChangePercentage >= 50.0;

        if (critical) {
            return "CRITICAL";
        }

        boolean high =
                speedChangePercentage <= -40.0
                        || densityChangePercentage >= 40.0;

        if (high) {
            return "HIGH";
        }

        return "MODERATE";
    }

    private String buildAnomalyMessage(
            double currentSpeed,
            double currentDensity,
            double speedChangePercentage,
            double densityChangePercentage,
            String severity) {

        return String.format(
                "%s traffic anomaly detected. "
                        + "Current speed: %.2f km/h "
                        + "(%.2f%% change from baseline). "
                        + "Current density: %.2f%% "
                        + "(%.2f%% change from baseline).",
                severity,
                currentSpeed,
                speedChangePercentage,
                currentDensity,
                densityChangePercentage
        );
    }
}
