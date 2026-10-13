package com.citypulse.controller;

import com.citypulse.model.Alert;
import com.citypulse.service.AlertService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    // =========================================================
    // EXISTING QUERY ENDPOINTS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts() {

        return ResponseEntity.ok(
                alertService.getAllAlerts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(
            @PathVariable Long id) {

        return alertService.getAlertById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/road/{roadId}")
    public ResponseEntity<List<Alert>> getAlertsByRoad(
            @PathVariable Long roadId) {

        return ResponseEntity.ok(
                alertService.getAlertsByRoad(roadId));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<Alert>> getAlertsByLocation(
            @PathVariable Long locationId) {

        return ResponseEntity.ok(
                alertService.getAlertsByLocation(locationId));
    }

    @GetMapping("/type/{alertType}")
    public ResponseEntity<List<Alert>> getAlertsByType(
            @PathVariable String alertType) {

        return ResponseEntity.ok(
                alertService.getAlertsByType(alertType));
    }

    @GetMapping("/severity/{severity}")
    public ResponseEntity<List<Alert>> getAlertsBySeverity(
            @PathVariable String severity) {

        return ResponseEntity.ok(
                alertService.getAlertsBySeverity(severity));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Alert>> getActiveAlerts() {

        return ResponseEntity.ok(
                alertService.getActiveAlerts());
    }

    // =========================================================
    // SMART ALERT GENERATION
    // =========================================================

    /**
     * Generates a congestion alert from the latest
     * traffic condition for a road.
     *
     * Example:
     * POST /api/alerts/generate/congestion/1
     */
    @PostMapping("/generate/congestion/{roadId}")
    public ResponseEntity<Alert> generateCongestionAlert(
            @PathVariable Long roadId) {

        Alert alert =
                alertService.generateCongestionAlert(roadId);

        return ResponseEntity.ok(alert);
    }

    /**
     * Generates an anomaly alert from the latest
     * traffic condition for a road.
     *
     * If no anomaly is detected, the service returns null.
     *
     * Example:
     * POST /api/alerts/generate/anomaly/1
     */
    @PostMapping("/generate/anomaly/{roadId}")
    public ResponseEntity<Alert> generateAnomalyAlert(
            @PathVariable Long roadId) {

        Alert alert =
                alertService.generateAnomalyAlert(roadId);

        if (alert == null) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(alert);
    }

    /**
     * Generates all applicable smart alerts for a road.
     *
     * This can generate:
     * - Congestion alert
     * - Anomaly alert
     *
     * Example:
     * POST /api/alerts/generate/1
     */
    @PostMapping("/generate/{roadId}")
    public ResponseEntity<List<Alert>> generateSmartAlerts(
            @PathVariable Long roadId) {

        List<Alert> alerts =
                alertService.generateSmartAlerts(roadId);

        return ResponseEntity.ok(alerts);
    }

    // =========================================================
    // EXISTING CRUD ENDPOINTS
    // =========================================================

    @PostMapping
    public ResponseEntity<Alert> createAlert(
            @RequestBody Alert alert) {

        return ResponseEntity.ok(
                alertService.createAlert(alert));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Alert> updateAlert(
            @PathVariable Long id,
            @RequestBody Alert alert) {

        return ResponseEntity.ok(
                alertService.updateAlert(id, alert));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlert(
            @PathVariable Long id) {

        alertService.deleteAlert(id);

        return ResponseEntity.noContent().build();
    }
}