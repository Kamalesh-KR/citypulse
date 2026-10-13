package com.citypulse.controller;

import com.citypulse.model.TrafficPrediction;
import com.citypulse.service.TrafficPredictionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/predictions")
public class TrafficPredictionController {

    private final TrafficPredictionService trafficPredictionService;

    public TrafficPredictionController(
            TrafficPredictionService trafficPredictionService) {

        this.trafficPredictionService = trafficPredictionService;
    }

    // =========================================================
    // EXISTING QUERY ENDPOINTS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<TrafficPrediction>> getAllPredictions() {

        return ResponseEntity.ok(
                trafficPredictionService.getAllPredictions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TrafficPrediction> getPredictionById(
            @PathVariable Long id) {

        return trafficPredictionService.getPredictionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/road/{roadId}")
    public ResponseEntity<List<TrafficPrediction>> getPredictionsByRoad(
            @PathVariable Long roadId) {

        return ResponseEntity.ok(
                trafficPredictionService
                        .getPredictionsByRoad(roadId));
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<TrafficPrediction>> getPredictionsByLocation(
            @PathVariable Long locationId) {

        return ResponseEntity.ok(
                trafficPredictionService
                        .getPredictionsByLocation(locationId));
    }

    @GetMapping("/between")
    public ResponseEntity<List<TrafficPrediction>> getPredictionsBetween(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        return ResponseEntity.ok(
                trafficPredictionService
                        .getPredictionsBetween(start, end));
    }

    @GetMapping("/horizon/{minutes}")
    public ResponseEntity<List<TrafficPrediction>> getPredictionsByHorizon(
            @PathVariable Integer minutes) {

        return ResponseEntity.ok(
                trafficPredictionService
                        .getPredictionsByHorizon(minutes));
    }

    // =========================================================
    // PREDICTION GENERATION
    // =========================================================

    /**
     * Generates one congestion prediction for a specific
     * prediction horizon.
     *
     * Supported horizons:
     * 15, 30, 45 and 60 minutes.
     *
     * Example:
     * POST /api/predictions/generate/1/15
     */
    @PostMapping("/generate/{roadId}/{minutes}")
    public ResponseEntity<TrafficPrediction> generatePrediction(
            @PathVariable Long roadId,
            @PathVariable Integer minutes) {

        TrafficPrediction prediction =
                trafficPredictionService.generatePrediction(
                        roadId,
                        minutes);

        return ResponseEntity.ok(prediction);
    }

    /**
     * Generates predictions for all supported horizons:
     * 15, 30, 45 and 60 minutes.
     *
     * Example:
     * POST /api/predictions/generate/1
     */
    @PostMapping("/generate/{roadId}")
    public ResponseEntity<List<TrafficPrediction>> generatePredictions(
            @PathVariable Long roadId) {

        List<TrafficPrediction> predictions =
                trafficPredictionService
                        .generatePredictions(roadId);

        return ResponseEntity.ok(predictions);
    }

    // =========================================================
    // EXISTING CRUD ENDPOINTS
    // =========================================================

    @PostMapping
    public ResponseEntity<TrafficPrediction> createPrediction(
            @RequestBody TrafficPrediction prediction) {

        return ResponseEntity.ok(
                trafficPredictionService
                        .createPrediction(prediction));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TrafficPrediction> updatePrediction(
            @PathVariable Long id,
            @RequestBody TrafficPrediction prediction) {

        return ResponseEntity.ok(
                trafficPredictionService.updatePrediction(
                        id,
                        prediction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrediction(
            @PathVariable Long id) {

        trafficPredictionService.deletePrediction(id);

        return ResponseEntity.noContent().build();
    }
}
