package com.citypulse.service;

import com.citypulse.model.Road;
import com.citypulse.model.TrafficPrediction;
import com.citypulse.model.TrafficRecord;
import com.citypulse.repository.TrafficPredictionRepository;
import com.citypulse.repository.TrafficRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class TrafficPredictionService {

    private final TrafficPredictionRepository trafficPredictionRepository;
    private final TrafficRecordRepository trafficRecordRepository;
    private final WeatherImpactService weatherImpactService;

    public TrafficPredictionService(
            TrafficPredictionRepository trafficPredictionRepository,
            TrafficRecordRepository trafficRecordRepository,
            WeatherImpactService weatherImpactService) {

        this.trafficPredictionRepository = trafficPredictionRepository;
        this.trafficRecordRepository = trafficRecordRepository;
        this.weatherImpactService = weatherImpactService;
    }

    // =========================================================
    // EXISTING CRUD / QUERY METHODS
    // =========================================================

    public List<TrafficPrediction> getAllPredictions() {
        return trafficPredictionRepository.findAll();
    }

    public Optional<TrafficPrediction> getPredictionById(Long id) {
        return trafficPredictionRepository.findById(id);
    }

    public List<TrafficPrediction> getPredictionsByRoad(Long roadId) {
        return trafficPredictionRepository.findByRoadId(roadId);
    }

    public List<TrafficPrediction> getPredictionsByLocation(Long locationId) {
        return trafficPredictionRepository.findByLocationId(locationId);
    }

    public List<TrafficPrediction> getPredictionsBetween(
            LocalDateTime start,
            LocalDateTime end) {

        return trafficPredictionRepository
                .findByPredictionTimeBetween(start, end);
    }

    public List<TrafficPrediction> getPredictionsByHorizon(
            Integer predictionHorizonMinutes) {

        return trafficPredictionRepository
                .findByPredictionHorizonMinutes(
                        predictionHorizonMinutes);
    }

    public TrafficPrediction createPrediction(
            TrafficPrediction prediction) {

        return trafficPredictionRepository.save(prediction);
    }

    public TrafficPrediction updatePrediction(
            Long id,
            TrafficPrediction updatedPrediction) {

        return trafficPredictionRepository.findById(id)
                .map(existingPrediction -> {

                    existingPrediction.setRoad(
                            updatedPrediction.getRoad());

                    existingPrediction.setLocation(
                            updatedPrediction.getLocation());

                    existingPrediction.setPredictionTime(
                            updatedPrediction.getPredictionTime());

                    existingPrediction.setPredictedVehicleCount(
                            updatedPrediction.getPredictedVehicleCount());

                    existingPrediction.setPredictedAverageSpeed(
                            updatedPrediction.getPredictedAverageSpeed());

                    existingPrediction.setPredictedDensity(
                            updatedPrediction.getPredictedDensity());

                    existingPrediction.setPredictedTrafficLevel(
                            updatedPrediction.getPredictedTrafficLevel());

                    existingPrediction.setPredictedCongestionScore(
                            updatedPrediction.getPredictedCongestionScore());

                    existingPrediction.setConfidenceScore(
                            updatedPrediction.getConfidenceScore());

                    existingPrediction.setPredictionHorizonMinutes(
                            updatedPrediction.getPredictionHorizonMinutes());

                    existingPrediction.setModelVersion(
                            updatedPrediction.getModelVersion());

                    return trafficPredictionRepository
                            .save(existingPrediction);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Traffic prediction not found with id: "
                                        + id));
    }

    public void deletePrediction(Long id) {

        if (!trafficPredictionRepository.existsById(id)) {
            throw new RuntimeException(
                    "Traffic prediction not found with id: " + id);
        }

        trafficPredictionRepository.deleteById(id);
    }

    // =========================================================
    // CONGESTION PREDICTION ENGINE
    // =========================================================

    /**
     * Generates a congestion prediction for a road.
     *
     * Baseline model:
     *
     * 1. Latest traffic record
     * 2. Historical traffic records for the road
     * 3. Recent traffic trend
     * 4. Prediction horizon
     *
     * Weather-aware layer:
     *
     * 5. Latest weather impact for the traffic location
     * 6. Explainable weather adjustment
     *
     * Supported horizons:
     * 15, 30, 45 and 60 minutes.
     */
    public TrafficPrediction generatePrediction(
            Long roadId,
            Integer predictionHorizonMinutes) {

        if (roadId == null) {
            throw new IllegalArgumentException(
                    "Road ID is required");
        }

        if (predictionHorizonMinutes == null) {
            throw new IllegalArgumentException(
                    "Prediction horizon is required");
        }

        if (!isSupportedHorizon(predictionHorizonMinutes)) {
            throw new IllegalArgumentException(
                    "Prediction horizon must be 15, 30, 45 or 60 minutes");
        }

        List<TrafficRecord> records =
                trafficRecordRepository.findByRoadId(roadId);

        if (records == null || records.isEmpty()) {
            throw new RuntimeException(
                    "No traffic data available for road: " + roadId);
        }

        TrafficRecord latestRecord = records.stream()
                .filter(record -> record.getRecordedAt() != null)
                .max(Comparator.comparing(
                        TrafficRecord::getRecordedAt))
                .orElseThrow(() ->
                        new RuntimeException(
                                "No valid traffic record available for road: "
                                        + roadId));

        Road road = latestRecord.getRoad();

        if (road == null) {
            throw new RuntimeException(
                    "Road information not available for road: " + roadId);
        }

        // ---------------------------------------------------------
        // Calculate historical averages
        // ---------------------------------------------------------

        double averageVehicleCount =
                records.stream()
                        .filter(record ->
                                record.getVehicleCount() != null)
                        .mapToInt(TrafficRecord::getVehicleCount)
                        .average()
                        .orElse(
                                latestRecord.getVehicleCount() != null
                                        ? latestRecord.getVehicleCount()
                                        : 0.0
                        );

        double averageSpeed =
                records.stream()
                        .filter(record ->
                                record.getAverageSpeed() != null)
                        .mapToDouble(record ->
                                record.getAverageSpeed().doubleValue())
                        .average()
                        .orElse(
                                latestRecord.getAverageSpeed() != null
                                        ? latestRecord.getAverageSpeed()
                                                .doubleValue()
                                        : 0.0
                        );

        double averageDensity =
                records.stream()
                        .filter(record ->
                                record.getTrafficDensity() != null)
                        .mapToDouble(record ->
                                record.getTrafficDensity().doubleValue())
                        .average()
                        .orElse(
                                latestRecord.getTrafficDensity() != null
                                        ? latestRecord.getTrafficDensity()
                                                .doubleValue()
                                        : 0.0
                        );

        double averageCongestion =
                records.stream()
                        .filter(record ->
                                record.getCongestionScore() != null)
                        .mapToDouble(record ->
                                record.getCongestionScore().doubleValue())
                        .average()
                        .orElse(
                                latestRecord.getCongestionScore() != null
                                        ? latestRecord.getCongestionScore()
                                                .doubleValue()
                                        : 0.0
                        );

        // ---------------------------------------------------------
        // Latest traffic values
        // ---------------------------------------------------------

        double currentVehicleCount =
                latestRecord.getVehicleCount() != null
                        ? latestRecord.getVehicleCount()
                        : averageVehicleCount;

        double currentSpeed =
                latestRecord.getAverageSpeed() != null
                        ? latestRecord.getAverageSpeed().doubleValue()
                        : averageSpeed;

        double currentDensity =
                latestRecord.getTrafficDensity() != null
                        ? latestRecord.getTrafficDensity().doubleValue()
                        : averageDensity;

        double currentCongestion =
                latestRecord.getCongestionScore() != null
                        ? latestRecord.getCongestionScore().doubleValue()
                        : averageCongestion;

        // ---------------------------------------------------------
        // Calculate traffic trend
        // ---------------------------------------------------------

        double vehicleTrend =
                calculateTrend(
                        currentVehicleCount,
                        averageVehicleCount);

        double speedTrend =
                calculateTrend(
                        currentSpeed,
                        averageSpeed);

        double densityTrend =
                calculateTrend(
                        currentDensity,
                        averageDensity);

        double congestionTrend =
                calculateTrend(
                        currentCongestion,
                        averageCongestion);

        /*
         * The farther the prediction horizon, the more conservative
         * the trend becomes.
         *
         * 15 min  -> 100% of trend
         * 30 min  ->  80% of trend
         * 45 min  ->  60% of trend
         * 60 min  ->  40% of trend
         */
        double trendFactor =
                getTrendFactor(predictionHorizonMinutes);

        // ---------------------------------------------------------
        // Baseline future values
        // ---------------------------------------------------------

        double predictedVehicleCount =
                currentVehicleCount
                        + ((currentVehicleCount * vehicleTrend)
                        * trendFactor);

        double predictedSpeed =
                currentSpeed
                        + ((currentSpeed * speedTrend)
                        * trendFactor);

        double predictedDensity =
                currentDensity
                        + ((currentDensity * densityTrend)
                        * trendFactor);

        double predictedCongestion =
                currentCongestion
                        + ((currentCongestion * congestionTrend)
                        * trendFactor);

        // ---------------------------------------------------------
        // Weather-aware adjustment
        // ---------------------------------------------------------

        /*
         * Weather impact is obtained from the latest weather record
         * associated with the traffic location.
         *
         * The adjustment is deliberately bounded and explainable:
         *
         * - Vehicle count: up to +15%
         * - Density: up to +15 percentage points
         * - Congestion: up to +20 points
         * - Speed: down by up to 20%
         *
         * The adjustment is scaled by the weather impact score.
         *
         * A LOW weather score therefore has little effect, while
         * HIGH/SEVERE weather can materially increase predicted
         * congestion and reduce predicted speed.
         */
        double weatherImpactScore = 0.0;

        if (latestRecord.getLocation() != null
                && latestRecord.getLocation().getId() != null) {

            weatherImpactScore =
                    weatherImpactService
                            .analyzeLocation(
                                    latestRecord.getLocation().getId())
                            .map(response ->
                                    response.getWeatherImpactScore() != null
                                            ? response
                                                    .getWeatherImpactScore()
                                            : 0.0)
                            .orElse(0.0);
        }

        double weatherFactor =
                clamp(
                        weatherImpactScore / 100.0,
                        0.0,
                        1.0);

        double vehicleWeatherAdjustment =
                1.0
                        + (0.15 * weatherFactor);

        double speedWeatherAdjustment =
                1.0
                        - (0.20 * weatherFactor);

        double densityWeatherAdjustment =
                15.0 * weatherFactor;

        double congestionWeatherAdjustment =
                20.0 * weatherFactor;

        predictedVehicleCount =
                predictedVehicleCount
                        * vehicleWeatherAdjustment;

        predictedSpeed =
                predictedSpeed
                        * speedWeatherAdjustment;

        predictedDensity =
                predictedDensity
                        + densityWeatherAdjustment;

        predictedCongestion =
                predictedCongestion
                        + congestionWeatherAdjustment;

        // ---------------------------------------------------------
        // Keep values within realistic boundaries
        // ---------------------------------------------------------

        predictedVehicleCount =
                Math.max(0, predictedVehicleCount);

        predictedSpeed =
                Math.max(0, predictedSpeed);

        predictedDensity =
                clamp(predictedDensity, 0, 100);

        predictedCongestion =
                clamp(predictedCongestion, 0, 100);

        // ---------------------------------------------------------
        // Determine predicted traffic level
        // ---------------------------------------------------------

        String predictedTrafficLevel =
                determineTrafficLevel(
                        predictedDensity,
                        predictedCongestion,
                        predictedSpeed,
                        road.getSpeedLimit());

        // ---------------------------------------------------------
        // Calculate confidence
        // ---------------------------------------------------------

        double confidenceScore =
                calculateConfidence(
                        records.size(),
                        trendFactor);

        /*
         * Weather is an additional contextual factor rather than
         * a separately trained ML model. Therefore confidence is
         * kept based on traffic-data history and horizon.
         */
        // ---------------------------------------------------------
        // Create prediction entity
        // ---------------------------------------------------------

        TrafficPrediction prediction =
                new TrafficPrediction();

        prediction.setRoad(road);
        prediction.setLocation(latestRecord.getLocation());

        prediction.setPredictionTime(
                LocalDateTime.now()
                        .plusMinutes(predictionHorizonMinutes));

        prediction.setPredictedVehicleCount(
                (int) Math.round(predictedVehicleCount));

        prediction.setPredictedAverageSpeed(
                toBigDecimal(predictedSpeed));

        prediction.setPredictedDensity(
                toBigDecimal(predictedDensity));

        prediction.setPredictedTrafficLevel(
                predictedTrafficLevel);

        prediction.setPredictedCongestionScore(
                toBigDecimal(predictedCongestion));

        prediction.setConfidenceScore(
                toBigDecimal(confidenceScore));

        prediction.setPredictionHorizonMinutes(
                predictionHorizonMinutes);

        prediction.setModelVersion(
                weatherImpactScore > 0
                        ? "CITYPULSE-TREND-WEATHER-V1"
                        : "CITYPULSE-TREND-V1");

        return trafficPredictionRepository.save(prediction);
    }

    /**
     * Generates predictions for all supported horizons:
     * 15, 30, 45 and 60 minutes.
     */
    public List<TrafficPrediction> generatePredictions(
            Long roadId) {

        return List.of(
                generatePrediction(roadId, 15),
                generatePrediction(roadId, 30),
                generatePrediction(roadId, 45),
                generatePrediction(roadId, 60)
        );
    }

    // =========================================================
    // HELPER METHODS
    // =========================================================

    private boolean isSupportedHorizon(
            Integer predictionHorizonMinutes) {

        return predictionHorizonMinutes == 15
                || predictionHorizonMinutes == 30
                || predictionHorizonMinutes == 45
                || predictionHorizonMinutes == 60;
    }

    private double getTrendFactor(
            Integer predictionHorizonMinutes) {

        return switch (predictionHorizonMinutes) {
            case 15 -> 1.00;
            case 30 -> 0.80;
            case 45 -> 0.60;
            case 60 -> 0.40;
            default -> 0.40;
        };
    }

    /**
     * Calculates how far the current value has moved from
     * the historical average.
     */
    private double calculateTrend(
            double current,
            double average) {

        if (average == 0) {
            return 0;
        }

        return (current - average) / average;
    }

    private double clamp(
            double value,
            double minimum,
            double maximum) {

        return Math.max(
                minimum,
                Math.min(maximum, value)
        );
    }

    /**
     * Determines traffic level from predicted density,
     * congestion and average speed.
     */
    private String determineTrafficLevel(
            double density,
            double congestion,
            double speed,
            BigDecimal speedLimit) {

        double speedRatio = 1.0;

        if (speedLimit != null
                && speedLimit.doubleValue() > 0) {

            speedRatio =
                    speed / speedLimit.doubleValue();
        }

        if (density >= 85
                || congestion >= 85
                || speedRatio <= 0.30) {

            return "SEVERE";
        }

        if (density >= 65
                || congestion >= 65
                || speedRatio <= 0.50) {

            return "HIGH";
        }

        if (density >= 40
                || congestion >= 40
                || speedRatio <= 0.70) {

            return "MODERATE";
        }

        return "LOW";
    }

    /**
     * Confidence is based on the amount of historical data.
     *
     * More records produce a higher baseline confidence,
     * while longer horizons reduce confidence.
     */
    private double calculateConfidence(
            int recordCount,
            double trendFactor) {

        double dataConfidence;

        if (recordCount >= 20) {
            dataConfidence = 90.0;
        } else if (recordCount >= 10) {
            dataConfidence = 80.0;
        } else if (recordCount >= 5) {
            dataConfidence = 70.0;
        } else if (recordCount >= 2) {
            dataConfidence = 60.0;
        } else {
            dataConfidence = 50.0;
        }

        double horizonAdjustment =
                trendFactor * 10.0;

        return clamp(
                dataConfidence + horizonAdjustment,
                0,
                100
        );
    }

    private BigDecimal toBigDecimal(
            double value) {

        return BigDecimal
                .valueOf(value)
                .setScale(2, RoundingMode.HALF_UP);
    }
}