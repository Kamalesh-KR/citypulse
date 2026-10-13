package com.citypulse.service;

import com.citypulse.dto.CongestionResponse;
import com.citypulse.dto.TrafficAnalysisResponse;
import com.citypulse.dto.TrafficAnomalyResponse;
import com.citypulse.dto.TrafficHistoricalAnalyticsResponse;
import com.citypulse.model.TrafficRecord;
import com.citypulse.repository.TrafficRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class TrafficRecordService {

    private final TrafficRecordRepository trafficRecordRepository;

    public TrafficRecordService(
            TrafficRecordRepository trafficRecordRepository) {

        this.trafficRecordRepository = trafficRecordRepository;
    }

    public List<TrafficRecord> getAllTrafficRecords() {

        return trafficRecordRepository.findAll();
    }

    public Optional<TrafficRecord> getTrafficRecordById(Long id) {

        return trafficRecordRepository.findById(id);
    }

    public List<TrafficRecord> getTrafficRecordsByRoad(Long roadId) {

        return trafficRecordRepository.findByRoadId(roadId);
    }

    public List<TrafficRecord> getTrafficRecordsByLocation(Long locationId) {

        return trafficRecordRepository.findByLocationId(locationId);
    }

    public List<TrafficRecord> getTrafficRecordsByTrafficLevel(
            String trafficLevel) {

        return trafficRecordRepository
                .findByTrafficLevelIgnoreCase(trafficLevel);
    }

    public List<TrafficRecord> getTrafficRecordsBetween(
            LocalDateTime start,
            LocalDateTime end) {

        return trafficRecordRepository
                .findByRecordedAtBetween(start, end);
    }

    public Optional<TrafficRecord> getLatestTrafficRecordByRoad(
            Long roadId) {

        return trafficRecordRepository
                .findFirstByRoadIdOrderByRecordedAtDesc(roadId);
    }

    public Optional<TrafficRecord> getLatestTrafficRecordByLocation(
            Long locationId) {

        return trafficRecordRepository
                .findFirstByLocationIdOrderByRecordedAtDesc(locationId);
    }

    /**
     * Returns the latest traffic record for every road.
     */
    public List<TrafficRecord> getLiveTrafficRecords() {

        List<TrafficRecord> allRecords =
                trafficRecordRepository.findAllByOrderByRecordedAtDesc();

        List<TrafficRecord> latestRecords =
                new ArrayList<>();

        Set<Long> processedRoadIds =
                new HashSet<>();

        for (TrafficRecord record : allRecords) {

            Long roadId = record.getRoad().getId();

            if (!processedRoadIds.contains(roadId)) {

                latestRecords.add(record);
                processedRoadIds.add(roadId);
            }
        }

        return latestRecords;
    }

    /**
     * Analyzes the latest traffic condition for a road.
     */
    public Optional<TrafficAnalysisResponse> analyzeTrafficByRoad(
            Long roadId) {

        Optional<TrafficRecord> latestRecord =
                getLatestTrafficRecordByRoad(roadId);

        if (latestRecord.isEmpty()) {
            return Optional.empty();
        }

        TrafficRecord record = latestRecord.get();

        String trafficLevel = record.getTrafficLevel();

        Double congestionScore =
                record.getCongestionScore() != null
                        ? record.getCongestionScore().doubleValue()
                        : null;

        String congestionStatus;
        String analysisMessage;

        if (congestionScore != null) {

            if (congestionScore >= 80) {

                congestionStatus = "CRITICAL";

                analysisMessage =
                        "Traffic conditions are severely congested. "
                        + "Immediate congestion management is recommended.";

            } else if (congestionScore >= 60) {

                congestionStatus = "HIGH";

                analysisMessage =
                        "Traffic congestion is high. "
                        + "Drivers should consider alternative routes.";

            } else if (congestionScore >= 40) {

                congestionStatus = "MODERATE";

                analysisMessage =
                        "Traffic congestion is moderate. "
                        + "Traffic conditions should be monitored.";

            } else {

                congestionStatus = "LOW";

                analysisMessage =
                        "Traffic conditions are relatively normal.";
            }

        } else {

            congestionStatus = trafficLevel != null
                    ? trafficLevel.toUpperCase()
                    : "UNKNOWN";

            analysisMessage =
                    "Traffic analysis is based on the available "
                    + "traffic level data.";
        }

        return Optional.of(
                new TrafficAnalysisResponse(
                        record.getRoad().getId(),
                        record.getLocation().getId(),
                        record.getVehicleCount(),
                        record.getAverageSpeed() != null
                                ? record.getAverageSpeed().doubleValue()
                                : null,
                        record.getTrafficDensity() != null
                                ? record.getTrafficDensity().doubleValue()
                                : null,
                        trafficLevel,
                        congestionScore,
                        congestionStatus,
                        analysisMessage
                )
        );
    }

    /**
     * Detects the congestion condition of the latest traffic
     * record for a road.
     */
    public Optional<CongestionResponse> detectCongestionByRoad(
            Long roadId) {

        Optional<TrafficRecord> latestRecord =
                getLatestTrafficRecordByRoad(roadId);

        if (latestRecord.isEmpty()) {
            return Optional.empty();
        }

        TrafficRecord record = latestRecord.get();

        Double trafficDensity =
                record.getTrafficDensity() != null
                        ? record.getTrafficDensity().doubleValue()
                        : null;

        Double congestionScore =
                record.getCongestionScore() != null
                        ? record.getCongestionScore().doubleValue()
                        : null;

        String congestionStatus;
        String congestionMessage;

        if (congestionScore == null) {

            congestionStatus =
                    record.getTrafficLevel() != null
                            ? record.getTrafficLevel().toUpperCase()
                            : "UNKNOWN";

            congestionMessage =
                    "Congestion score is unavailable. "
                    + "The result is based on the recorded traffic level.";

        } else if (congestionScore >= 80) {

            congestionStatus = "CRITICAL";

            congestionMessage =
                    "Critical congestion detected. "
                    + "The road is experiencing very heavy traffic.";

        } else if (congestionScore >= 60) {

            congestionStatus = "HIGH";

            congestionMessage =
                    "High congestion detected. "
                    + "Traffic conditions may significantly increase travel time.";

        } else if (congestionScore >= 40) {

            congestionStatus = "MODERATE";

            congestionMessage =
                    "Moderate congestion detected. "
                    + "Traffic movement may be slower than normal.";

        } else {

            congestionStatus = "NORMAL";

            congestionMessage =
                    "No significant congestion detected. "
                    + "Traffic conditions are within the normal range.";
        }

        return Optional.of(
                new CongestionResponse(
                        record.getRoad().getId(),
                        record.getLocation().getId(),
                        record.getTrafficLevel(),
                        trafficDensity,
                        congestionScore,
                        congestionStatus,
                        congestionMessage
                )
        );
    }

    /**
     * Detects unusual traffic behavior by comparing the
     * latest traffic condition against the configured
     * expected baseline.
     *
     * Initial project baseline:
     * Expected speed = 40 km/h
     * Expected density = 60%
     *
     * Anomaly thresholds:
     * Speed decrease >= 30%
     * OR density increase >= 30%
     */
    public Optional<TrafficAnomalyResponse> detectTrafficAnomalyByRoad(
            Long roadId) {

        Optional<TrafficRecord> latestRecord =
                getLatestTrafficRecordByRoad(roadId);

        if (latestRecord.isEmpty()) {
            return Optional.empty();
        }

        TrafficRecord record = latestRecord.get();

        final double expectedSpeed = 40.0;
        final double expectedDensity = 60.0;

        Double currentSpeed =
                record.getAverageSpeed() != null
                        ? record.getAverageSpeed().doubleValue()
                        : null;

        Double currentDensity =
                record.getTrafficDensity() != null
                        ? record.getTrafficDensity().doubleValue()
                        : null;

        Double speedChangePercentage = null;
        Double densityChangePercentage = null;

        if (currentSpeed != null && expectedSpeed > 0) {

            speedChangePercentage =
                    ((currentSpeed - expectedSpeed)
                            / expectedSpeed) * 100.0;
        }

        if (currentDensity != null && expectedDensity > 0) {

            densityChangePercentage =
                    ((currentDensity - expectedDensity)
                            / expectedDensity) * 100.0;
        }

        boolean speedAnomaly =
                speedChangePercentage != null
                        && speedChangePercentage <= -30.0;

        boolean densityAnomaly =
                densityChangePercentage != null
                        && densityChangePercentage >= 30.0;

        boolean anomalyDetected =
                speedAnomaly || densityAnomaly;

        String anomalySeverity;
        String anomalyMessage;

        if (!anomalyDetected) {

            anomalySeverity = "NONE";

            anomalyMessage =
                    "Traffic conditions are within the expected range. "
                    + "No significant anomaly detected.";

        } else if (
                (speedChangePercentage != null
                        && speedChangePercentage <= -50.0)
                        || (densityChangePercentage != null
                        && densityChangePercentage >= 60.0)) {

            anomalySeverity = "CRITICAL";

            anomalyMessage =
                    "A critical traffic anomaly has been detected. "
                    + "Current traffic conditions differ significantly "
                    + "from the expected baseline.";

        } else if (
                (speedChangePercentage != null
                        && speedChangePercentage <= -40.0)
                        || (densityChangePercentage != null
                        && densityChangePercentage >= 45.0)) {

            anomalySeverity = "HIGH";

            anomalyMessage =
                    "A high traffic anomaly has been detected. "
                    + "Traffic conditions are considerably different "
                    + "from the expected baseline.";

        } else {

            anomalySeverity = "MODERATE";

            anomalyMessage =
                    "A moderate traffic anomaly has been detected. "
                    + "Traffic conditions differ from the expected baseline.";
        }

        return Optional.of(
                new TrafficAnomalyResponse(
                        record.getRoad().getId(),
                        record.getLocation().getId(),
                        currentSpeed,
                        expectedSpeed,
                        currentDensity,
                        expectedDensity,
                        speedChangePercentage,
                        densityChangePercentage,
                        anomalyDetected,
                        anomalySeverity,
                        anomalyMessage
                )
        );
    }

    // =========================================================
    // HISTORICAL TRAFFIC ANALYTICS
    // =========================================================

    /**
     * Calculates historical analytics using all traffic records.
     */
    public Optional<TrafficHistoricalAnalyticsResponse>
    getHistoricalAnalytics() {

        List<TrafficRecord> records =
                trafficRecordRepository.findAllByOrderByRecordedAtDesc();

        return buildHistoricalAnalytics(records);
    }

    /**
     * Calculates historical analytics for a specific road.
     */
    public Optional<TrafficHistoricalAnalyticsResponse>
    getHistoricalAnalyticsByRoad(Long roadId) {

        List<TrafficRecord> records =
                trafficRecordRepository.findByRoadId(roadId);

        return buildHistoricalAnalytics(records);
    }

    /**
     * Calculates historical analytics for a specific location.
     */
    public Optional<TrafficHistoricalAnalyticsResponse>
    getHistoricalAnalyticsByLocation(Long locationId) {

        List<TrafficRecord> records =
                trafficRecordRepository.findByLocationId(locationId);

        return buildHistoricalAnalytics(records);
    }

    /**
     * Calculates historical analytics for a specific time range.
     */
    public Optional<TrafficHistoricalAnalyticsResponse>
    getHistoricalAnalyticsBetween(
            LocalDateTime start,
            LocalDateTime end) {

        List<TrafficRecord> records =
                trafficRecordRepository
                        .findByRecordedAtBetween(start, end);

        return buildHistoricalAnalytics(records);
    }

    /**
     * Builds historical analytics from the supplied traffic records.
     */
    private Optional<TrafficHistoricalAnalyticsResponse>
    buildHistoricalAnalytics(
            List<TrafficRecord> records) {

        if (records == null || records.isEmpty()) {
            return Optional.empty();
        }

        double totalVehicles = 0.0;
        int vehicleCountRecords = 0;

        double totalSpeed = 0.0;
        int speedRecords = 0;

        double totalDensity = 0.0;
        int densityRecords = 0;

        double totalCongestionScore = 0.0;
        int congestionRecords = 0;

        Double minimumSpeed = null;
        Double maximumSpeed = null;

        Double minimumDensity = null;
        Double maximumDensity = null;

        Map<String, Integer> trafficLevelDistribution =
                new HashMap<>();

        LocalDateTime analysisStart = null;
        LocalDateTime analysisEnd = null;

        Long roadId = null;
        Long locationId = null;

        for (TrafficRecord record : records) {

            if (record.getRoad() != null) {
                roadId = record.getRoad().getId();
            }

            if (record.getLocation() != null) {
                locationId = record.getLocation().getId();
            }

            if (record.getVehicleCount() != null) {

                totalVehicles += record.getVehicleCount();
                vehicleCountRecords++;
            }

            if (record.getAverageSpeed() != null) {

                double speed =
                        record.getAverageSpeed().doubleValue();

                totalSpeed += speed;
                speedRecords++;

                if (minimumSpeed == null
                        || speed < minimumSpeed) {

                    minimumSpeed = speed;
                }

                if (maximumSpeed == null
                        || speed > maximumSpeed) {

                    maximumSpeed = speed;
                }
            }

            if (record.getTrafficDensity() != null) {

                double density =
                        record.getTrafficDensity().doubleValue();

                totalDensity += density;
                densityRecords++;

                if (minimumDensity == null
                        || density < minimumDensity) {

                    minimumDensity = density;
                }

                if (maximumDensity == null
                        || density > maximumDensity) {

                    maximumDensity = density;
                }
            }

            if (record.getCongestionScore() != null) {

                totalCongestionScore +=
                        record.getCongestionScore().doubleValue();

                congestionRecords++;
            }

            if (record.getTrafficLevel() != null
                    && !record.getTrafficLevel().isBlank()) {

                String level =
                        record.getTrafficLevel().toUpperCase();

                trafficLevelDistribution.merge(
                        level,
                        1,
                        Integer::sum
                );
            }

            if (record.getRecordedAt() != null) {

                if (analysisStart == null
                        || record.getRecordedAt()
                        .isBefore(analysisStart)) {

                    analysisStart = record.getRecordedAt();
                }

                if (analysisEnd == null
                        || record.getRecordedAt()
                        .isAfter(analysisEnd)) {

                    analysisEnd = record.getRecordedAt();
                }
            }
        }

        Double averageVehicleCount =
                vehicleCountRecords > 0
                        ? totalVehicles / vehicleCountRecords
                        : null;

        Double averageSpeed =
                speedRecords > 0
                        ? totalSpeed / speedRecords
                        : null;

        Double averageDensity =
                densityRecords > 0
                        ? totalDensity / densityRecords
                        : null;

        Double averageCongestionScore =
                congestionRecords > 0
                        ? totalCongestionScore / congestionRecords
                        : null;

        TrafficHistoricalAnalyticsResponse response =
                new TrafficHistoricalAnalyticsResponse(
                        roadId,
                        locationId,
                        records.size(),
                        averageVehicleCount,
                        averageSpeed,
                        averageDensity,
                        averageCongestionScore,
                        minimumSpeed,
                        maximumSpeed,
                        minimumDensity,
                        maximumDensity,
                        trafficLevelDistribution,
                        analysisStart,
                        analysisEnd
                );

        return Optional.of(response);
    }

    // =========================================================
    // CRUD OPERATIONS
    // =========================================================

    public TrafficRecord createTrafficRecord(
            TrafficRecord trafficRecord) {

        return trafficRecordRepository.save(trafficRecord);
    }

    public TrafficRecord updateTrafficRecord(
            Long id,
            TrafficRecord updatedTrafficRecord) {

        return trafficRecordRepository.findById(id)
                .map(existingRecord -> {

                    existingRecord.setRoad(
                            updatedTrafficRecord.getRoad()
                    );

                    existingRecord.setLocation(
                            updatedTrafficRecord.getLocation()
                    );

                    existingRecord.setVehicleCount(
                            updatedTrafficRecord.getVehicleCount()
                    );

                    existingRecord.setAverageSpeed(
                            updatedTrafficRecord.getAverageSpeed()
                    );

                    existingRecord.setTrafficDensity(
                            updatedTrafficRecord.getTrafficDensity()
                    );

                    existingRecord.setTrafficLevel(
                            updatedTrafficRecord.getTrafficLevel()
                    );

                    existingRecord.setCongestionScore(
                            updatedTrafficRecord.getCongestionScore()
                    );

                    /*
                     * Keep the existing recordedAt timestamp
                     * when the update request does not provide one.
                     */
                    if (updatedTrafficRecord.getRecordedAt() != null) {

                        existingRecord.setRecordedAt(
                                updatedTrafficRecord.getRecordedAt()
                        );
                    }

                    existingRecord.setDataSource(
                            updatedTrafficRecord.getDataSource()
                    );

                    return trafficRecordRepository.save(
                            existingRecord
                    );
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Traffic record not found with id: " + id
                        ));
    }

    public void deleteTrafficRecord(Long id) {

        if (!trafficRecordRepository.existsById(id)) {

            throw new RuntimeException(
                    "Traffic record not found with id: " + id
            );
        }

        trafficRecordRepository.deleteById(id);
    }
}
