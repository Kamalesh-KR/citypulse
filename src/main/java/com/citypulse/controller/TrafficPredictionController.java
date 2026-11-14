package com.citypulse.controller;

import com.citypulse.model.TrafficPrediction;
import com.citypulse.prediction.CongestionPredictor;
import com.citypulse.service.TrafficPredictionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/predictions")
public class TrafficPredictionController {

    private final TrafficPredictionService trafficPredictionService;
    private final CongestionPredictor congestionPredictor;

    public TrafficPredictionController(
            TrafficPredictionService trafficPredictionService,
            CongestionPredictor congestionPredictor) {

        this.trafficPredictionService = trafficPredictionService;
        this.congestionPredictor = congestionPredictor;
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

    // =========================================================
    // ML-ENHANCED PREDICTION (B1)
    // =========================================================
    //
    // Delegates forecasting to CongestionPredictor, which
    // uses a lightweight LSTM-lite model (weighted moving
    // average + day-of-week seasonality + exponential
    // smoothing) instead of the baseline heuristic.
    //
    // Returns forecasts for +15, +30, +45, +60 minutes,
    // each with a confidence score.
    //
    // The endpoint is intentionally separate from the
    // existing /generate/{roadId} route so the frontend
    // can switch over once ML predictions have been
    // validated in production. Old endpoints are
    // untouched and continue to work exactly as before.
    //
    // Example:
    // POST /api/predictions/generate-ml/1
    //
    @PostMapping("/generate-ml/{roadId}")
    public ResponseEntity<Map<String, Object>> generateMlPredictions(
            @PathVariable Long roadId) {

        /* -------------------------------------------------
         * 1. Fetch the freshest observations for this road
         *    so the ML model always forecasts from real data.
         * ------------------------------------------------- */

        List<TrafficPrediction> recent =
                trafficPredictionService
                        .getPredictionsByRoad(roadId);

        /* Fallback: if the road has no history, ask the
         * service to generate its usual predictions and
         * use those as the basis for ML. */
        if (recent == null || recent.isEmpty()) {

            try {
                recent =
                        trafficPredictionService
                                .generatePredictions(roadId);
            } catch (RuntimeException ex) {
                recent = List.of();
            }
        }

        /* -------------------------------------------------
         * 2. Convert model rows into CongestionPredictor
         *    observations (score + timestamp).
         * ------------------------------------------------- */

        List<CongestionPredictor.Observation> observations =
                new ArrayList<>();

        if (recent != null) {

            for (TrafficPrediction row : recent) {

                if (row == null) continue;

                double score = resolveCongestionScore(row);
                java.time.ZonedDateTime recordedAt =
                        resolveRecordedAt(row);

                if (recordedAt == null) {
                    recordedAt = java.time.ZonedDateTime.now();
                }

                observations.add(
                        new CongestionPredictor.Observation(
                                score,
                                recordedAt
                        )
                );
            }
        }

        /* -------------------------------------------------
         * 3. Run the ML forecast.
         * ------------------------------------------------- */

        List<CongestionPredictor.Forecast> forecasts =
                congestionPredictor.predict(observations);

        /* -------------------------------------------------
         * 4. Shape the response so the frontend can
         *    display it with minimal changes.
         * ------------------------------------------------- */

        List<Map<String, Object>> forecastRows =
                new ArrayList<>();

        for (CongestionPredictor.Forecast f : forecasts) {

            Map<String, Object> row = new LinkedHashMap<>();

            row.put("horizonMinutes", f.horizonMinutes());
            row.put("predictedScore", f.predictedScore());
            row.put("predictedTrafficLevel", f.predictedTrafficLevel());
            row.put("confidencePercent", f.confidencePercent());

            forecastRows.add(row);
        }

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("roadId", roadId);
        response.put("model", "CITYPULSE-ML-LSTM-LITE-V1");
        response.put("observationCount", observations.size());
        response.put("forecasts", forecastRows);
        response.put("generatedAt",
                java.time.ZonedDateTime.now().toString());

        return ResponseEntity.ok(response);
    }


    /* =========================================================
       HELPERS FOR ML ENDPOINT
       ========================================================= */

    /**
     * Resolves a congestion score from a TrafficPrediction
     * row, trying the most specific field first.
     * The TrafficPrediction entity uses BigDecimal for
     * density and congestion fields, so we convert via
     * doubleValue() before clamping.
     * Falls back to 50 (neutral) if nothing usable is present.
     */
    private double resolveCongestionScore(
            TrafficPrediction row) {

        try {

            /* Preferred: predicted congestion score (BigDecimal). */
            BigDecimal predicted =
                    row.getPredictedCongestionScore();

            if (predicted != null) {
                double value = predicted.doubleValue();
                if (!Double.isNaN(value)) {
                    return Math.max(0.0, Math.min(100.0, value));
                }
            }

        } catch (RuntimeException ignored) { }

        try {

            /* Fallback: predicted density as a rough proxy (BigDecimal). */
            BigDecimal density =
                    row.getPredictedDensity();

            if (density != null) {
                double value = density.doubleValue();
                if (!Double.isNaN(value)) {
                    return Math.max(0.0, Math.min(100.0, value));
                }
            }

        } catch (RuntimeException ignored) { }

        return 50.0;
    }


    /**
     * Resolves the observation timestamp from a
     * TrafficPrediction row, preferring prediction time
     * then falling back to creation time.
     */
    private java.time.ZonedDateTime resolveRecordedAt(
            TrafficPrediction row) {

        try {
            LocalDateTime t = row.getPredictionTime();
            if (t != null) {
                return t.atZone(java.time.ZoneId.systemDefault());
            }
        } catch (RuntimeException ignored) { }

        try {
            LocalDateTime t = row.getCreatedAt();
            if (t != null) {
                return t.atZone(java.time.ZoneId.systemDefault());
            }
        } catch (RuntimeException ignored) { }

        return null;
    }
}