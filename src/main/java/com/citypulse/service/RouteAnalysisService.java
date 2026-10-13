package com.citypulse.service;

import com.citypulse.dto.SmartRouteResponse;
import com.citypulse.model.RouteAnalysis;
import com.citypulse.model.TrafficPrediction;
import com.citypulse.model.TrafficRecord;
import com.citypulse.repository.RouteAnalysisRepository;
import com.citypulse.repository.TrafficPredictionRepository;
import com.citypulse.repository.TrafficRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class RouteAnalysisService {

    private final RouteAnalysisRepository routeAnalysisRepository;
    private final TrafficRecordRepository trafficRecordRepository;
    private final TrafficPredictionRepository trafficPredictionRepository;

    public RouteAnalysisService(
            RouteAnalysisRepository routeAnalysisRepository,
            TrafficRecordRepository trafficRecordRepository,
            TrafficPredictionRepository trafficPredictionRepository) {

        this.routeAnalysisRepository = routeAnalysisRepository;
        this.trafficRecordRepository = trafficRecordRepository;
        this.trafficPredictionRepository = trafficPredictionRepository;
    }

    public List<RouteAnalysis> getAllRouteAnalyses() {
        return routeAnalysisRepository.findAll();
    }

    public Optional<RouteAnalysis> getRouteAnalysisById(Long id) {
        return routeAnalysisRepository.findById(id);
    }

    public List<RouteAnalysis> getAnalysesByStartLocation(Long startLocationId) {
        return routeAnalysisRepository.findByStartLocationId(startLocationId);
    }

    public List<RouteAnalysis> getAnalysesByDestinationLocation(Long destinationLocationId) {
        return routeAnalysisRepository.findByDestinationLocationId(destinationLocationId);
    }

    public List<RouteAnalysis> getAnalysesByType(String analysisType) {
        return routeAnalysisRepository.findByAnalysisTypeIgnoreCase(analysisType);
    }

    public List<RouteAnalysis> getAnalysesBetween(
            LocalDateTime start,
            LocalDateTime end) {

        return routeAnalysisRepository.findByAnalyzedAtBetween(start, end);
    }

    public RouteAnalysis createRouteAnalysis(RouteAnalysis routeAnalysis) {
        return routeAnalysisRepository.save(routeAnalysis);
    }

    public RouteAnalysis updateRouteAnalysis(
            Long id,
            RouteAnalysis updatedAnalysis) {

        return routeAnalysisRepository.findById(id)
                .map(existingAnalysis -> {
                    existingAnalysis.setStartLocation(
                            updatedAnalysis.getStartLocation());

                    existingAnalysis.setDestinationLocation(
                            updatedAnalysis.getDestinationLocation());

                    existingAnalysis.setRouteName(
                            updatedAnalysis.getRouteName());

                    existingAnalysis.setDistanceKm(
                            updatedAnalysis.getDistanceKm());

                    existingAnalysis.setEstimatedTimeMinutes(
                            updatedAnalysis.getEstimatedTimeMinutes());

                    existingAnalysis.setCurrentTrafficLevel(
                            updatedAnalysis.getCurrentTrafficLevel());

                    existingAnalysis.setPredictedTrafficLevel(
                            updatedAnalysis.getPredictedTrafficLevel());

                    existingAnalysis.setCongestionScore(
                            updatedAnalysis.getCongestionScore());

                    existingAnalysis.setRouteScore(
                            updatedAnalysis.getRouteScore());

                    existingAnalysis.setRecommendationReason(
                            updatedAnalysis.getRecommendationReason());

                    existingAnalysis.setAnalysisType(
                            updatedAnalysis.getAnalysisType());

                    existingAnalysis.setAnalyzedAt(
                            updatedAnalysis.getAnalyzedAt());

                    return routeAnalysisRepository.save(existingAnalysis);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Route analysis not found with id: " + id));
    }

    public void deleteRouteAnalysis(Long id) {
        if (!routeAnalysisRepository.existsById(id)) {
            throw new RuntimeException(
                    "Route analysis not found with id: " + id);
        }

        routeAnalysisRepository.deleteById(id);
    }

    public List<SmartRouteResponse> recommendRoutes(
            Long startLocationId,
            Long destinationLocationId) {

        if (startLocationId == null || destinationLocationId == null) {
            throw new IllegalArgumentException(
                    "Start location ID and destination location ID are required");
        }

        if (startLocationId.equals(destinationLocationId)) {
            throw new IllegalArgumentException(
                    "Start location and destination location must be different");
        }

        List<RouteAnalysis> routeAnalyses =
                routeAnalysisRepository
                        .findByStartLocationId(startLocationId)
                        .stream()
                        .filter(route ->
                                route.getDestinationLocation() != null
                                        && destinationLocationId.equals(
                                        route.getDestinationLocation().getId()))
                        .toList();

        if (routeAnalyses.isEmpty()) {
            throw new RuntimeException(
                    "No route options are available between the selected locations");
        }

        List<SmartRouteResponse> responses = new ArrayList<>();

        for (RouteAnalysis route : routeAnalyses) {

            TrafficRecord trafficRecord =
                    findLatestTrafficRecord(route);

            TrafficPrediction trafficPrediction =
                    findLatestPrediction(route);

            String currentTrafficLevel =
                    getCurrentTrafficLevel(
                            route,
                            trafficRecord);

            String predictedTrafficLevel =
                    getPredictedTrafficLevel(
                            route,
                            trafficPrediction);

            Double averageSpeed =
                    getAverageSpeed(trafficRecord);

            Double distanceKm =
                    toDouble(route.getDistanceKm());

            Double estimatedTimeMinutes =
                    calculateTravelTime(
                            distanceKm,
                            averageSpeed);

            if (estimatedTimeMinutes == null) {
                estimatedTimeMinutes =
                        toDouble(route.getEstimatedTimeMinutes());
            }

            Double congestionScore =
                    getCongestionScore(
                            route,
                            trafficRecord,
                            trafficPrediction);

            Double routeScore =
                    calculateRouteScore(
                            estimatedTimeMinutes,
                            congestionScore,
                            currentTrafficLevel,
                            predictedTrafficLevel);

            String reason =
                    buildRecommendationReason(
                            currentTrafficLevel,
                            predictedTrafficLevel,
                            congestionScore,
                            estimatedTimeMinutes,
                            averageSpeed);

            SmartRouteResponse response =
                    new SmartRouteResponse(
                            route.getId(),
                            route.getRouteName(),
                            startLocationId,
                            destinationLocationId,
                            distanceKm,
                            averageSpeed,
                            estimatedTimeMinutes,
                            currentTrafficLevel,
                            predictedTrafficLevel,
                            congestionScore,
                            routeScore,
                            reason,
                            false);

            responses.add(response);
        }

        double minimumScore =
                responses.stream()
                        .map(SmartRouteResponse::getRouteScore)
                        .filter(score -> score != null)
                        .min(Double::compareTo)
                        .orElse(Double.MAX_VALUE);

        for (SmartRouteResponse response : responses) {

            if (response.getRouteScore() != null
                    && Double.compare(
                    response.getRouteScore(),
                    minimumScore) == 0) {

                response.setRecommended(true);

                response.setRecommendationReason(
                        response.getRecommendationReason()
                                + " This route currently has the lowest calculated route score among the available options.");
            }
        }

        responses.sort(
                Comparator.comparing(
                        SmartRouteResponse::getRouteScore,
                        Comparator.nullsLast(Double::compareTo)));

        return responses;
    }

    private String getCurrentTrafficLevel(
            RouteAnalysis route,
            TrafficRecord trafficRecord) {

        if (route.getCurrentTrafficLevel() != null
                && !route.getCurrentTrafficLevel().isBlank()) {

            return route.getCurrentTrafficLevel();
        }

        if (trafficRecord != null
                && trafficRecord.getTrafficLevel() != null) {

            return trafficRecord.getTrafficLevel();
        }

        return null;
    }

    private String getPredictedTrafficLevel(
            RouteAnalysis route,
            TrafficPrediction trafficPrediction) {

        if (route.getPredictedTrafficLevel() != null
                && !route.getPredictedTrafficLevel().isBlank()) {

            return route.getPredictedTrafficLevel();
        }

        if (trafficPrediction != null
                && trafficPrediction.getPredictedTrafficLevel() != null) {

            return trafficPrediction.getPredictedTrafficLevel();
        }

        return null;
    }

    private TrafficRecord findLatestTrafficRecord(
            RouteAnalysis route) {

        if (route.getStartLocation() == null
                || route.getDestinationLocation() == null) {

            return null;
        }

        Long destinationLocationId =
                route.getDestinationLocation().getId();

        Long startLocationId =
                route.getStartLocation().getId();

        Optional<TrafficRecord> destinationTraffic =
                trafficRecordRepository
                        .findFirstByLocationIdOrderByRecordedAtDesc(
                                destinationLocationId);

        if (destinationTraffic.isPresent()) {
            return destinationTraffic.get();
        }

        return trafficRecordRepository
                .findFirstByLocationIdOrderByRecordedAtDesc(
                        startLocationId)
                .orElse(null);
    }

    private TrafficPrediction findLatestPrediction(
            RouteAnalysis route) {

        if (route.getStartLocation() == null
                || route.getDestinationLocation() == null) {

            return null;
        }

        Long destinationLocationId =
                route.getDestinationLocation().getId();

        Long startLocationId =
                route.getStartLocation().getId();

        List<TrafficPrediction> destinationPredictions =
                trafficPredictionRepository
                        .findByLocationId(destinationLocationId);

        Optional<TrafficPrediction> latestDestinationPrediction =
                destinationPredictions.stream()
                        .max(Comparator.comparing(
                                TrafficPrediction::getPredictionTime,
                                Comparator.nullsLast(
                                        Comparator.naturalOrder())));

        if (latestDestinationPrediction.isPresent()) {
            return latestDestinationPrediction.get();
        }

        List<TrafficPrediction> startPredictions =
                trafficPredictionRepository
                        .findByLocationId(startLocationId);

        return startPredictions.stream()
                .max(Comparator.comparing(
                        TrafficPrediction::getPredictionTime,
                        Comparator.nullsLast(
                                Comparator.naturalOrder())))
                .orElse(null);
    }

    private Double getAverageSpeed(
            TrafficRecord trafficRecord) {

        if (trafficRecord != null
                && trafficRecord.getAverageSpeed() != null
                && trafficRecord.getAverageSpeed().doubleValue() > 0) {

            return trafficRecord.getAverageSpeed().doubleValue();
        }

        return null;
    }

    private Double calculateTravelTime(
            Double distanceKm,
            Double averageSpeed) {

        if (distanceKm == null
                || averageSpeed == null
                || distanceKm <= 0
                || averageSpeed <= 0) {

            return null;
        }

        double time =
                (distanceKm / averageSpeed) * 60.0;

        return round(time);
    }

    private Double getCongestionScore(
            RouteAnalysis route,
            TrafficRecord trafficRecord,
            TrafficPrediction trafficPrediction) {

        if (route.getCongestionScore() != null) {
            return round(
                    route.getCongestionScore().doubleValue());
        }

        if (trafficRecord != null
                && trafficRecord.getCongestionScore() != null) {

            return round(
                    trafficRecord.getCongestionScore().doubleValue());
        }

        if (trafficPrediction != null
                && trafficPrediction.getPredictedCongestionScore() != null) {

            return round(
                    trafficPrediction
                            .getPredictedCongestionScore()
                            .doubleValue());
        }

        return null;
    }

    private Double calculateRouteScore(
            Double estimatedTimeMinutes,
            Double congestionScore,
            String currentTrafficLevel,
            String predictedTrafficLevel) {

        double timeScore =
                estimatedTimeMinutes != null
                        ? estimatedTimeMinutes
                        : 100.0;

        double congestion =
                congestionScore != null
                        ? congestionScore
                        : trafficLevelScore(currentTrafficLevel);

        double currentLevelScore =
                trafficLevelScore(currentTrafficLevel);

        double predictedLevelScore =
                trafficLevelScore(predictedTrafficLevel);

        double score =
                (timeScore * 0.45)
                        + (congestion * 0.30)
                        + (currentLevelScore * 0.10)
                        + (predictedLevelScore * 0.15);

        return round(score);
    }

    private double trafficLevelScore(String trafficLevel) {

        if (trafficLevel == null) {
            return 50.0;
        }

        return switch (trafficLevel.trim().toUpperCase()) {
            case "LOW" -> 20.0;
            case "MODERATE" -> 45.0;
            case "HIGH" -> 70.0;
            case "SEVERE" -> 95.0;
            default -> 50.0;
        };
    }

    private String buildRecommendationReason(
            String currentTrafficLevel,
            String predictedTrafficLevel,
            Double congestionScore,
            Double estimatedTimeMinutes,
            Double averageSpeed) {

        StringBuilder reason =
                new StringBuilder();

        if (currentTrafficLevel != null) {
            reason.append("Current traffic is ")
                    .append(currentTrafficLevel.toUpperCase())
                    .append(".");
        }

        if (predictedTrafficLevel != null) {
            reason.append(" Predicted traffic is ")
                    .append(predictedTrafficLevel.toUpperCase())
                    .append(".");
        }

        if (congestionScore != null) {
            reason.append(" Congestion score is ")
                    .append(formatNumber(congestionScore))
                    .append(".");
        }

        if (averageSpeed != null) {
            reason.append(" Current average speed is ")
                    .append(formatNumber(averageSpeed))
                    .append(" km/h.");
        }

        if (estimatedTimeMinutes != null) {
            reason.append(" Estimated travel time is ")
                    .append(formatNumber(estimatedTimeMinutes))
                    .append(" minutes.");
        }

        return reason.toString().trim();
    }

    private Double toDouble(BigDecimal value) {
        return value != null
                ? value.doubleValue()
                : null;
    }

    private Double round(Double value) {
        if (value == null) {
            return null;
        }

        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private String formatNumber(Double value) {
        if (value == null) {
            return "N/A";
        }

        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }
}
