package com.citypulse.service;

import com.citypulse.dto.EmergencySimulationRequest;
import com.citypulse.dto.EmergencySimulationResponse;
import com.citypulse.model.Location;
import com.citypulse.model.TrafficRecord;
import com.citypulse.repository.LocationRepository;
import com.citypulse.repository.TrafficRecordRepository;
import com.citypulse.routing.GraphHopperRoutingService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class EmergencySimulationService {

    private final LocationRepository locationRepository;
    private final TrafficRecordRepository trafficRecordRepository;
    private final GraphHopperRoutingService graphHopperRoutingService;

    public EmergencySimulationService(
            LocationRepository locationRepository,
            TrafficRecordRepository trafficRecordRepository,
            GraphHopperRoutingService graphHopperRoutingService) {

        this.locationRepository = locationRepository;
        this.trafficRecordRepository = trafficRecordRepository;
        this.graphHopperRoutingService = graphHopperRoutingService;
    }

    public EmergencySimulationResponse simulate(
            EmergencySimulationRequest request) {

        Location startLocation =
                locationRepository.findById(
                        request.getStartLocationId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Start location not found with id: "
                                                + request.getStartLocationId()));

        Location destinationLocation =
                locationRepository.findById(
                        request.getDestinationLocationId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Destination location not found with id: "
                                                + request.getDestinationLocationId()));

        validateCoordinates(startLocation, "Start");
        validateCoordinates(destinationLocation, "Destination");

        if (startLocation.getId()
                .equals(destinationLocation.getId())) {

            throw new IllegalArgumentException(
                    "Start and destination locations must be different.");
        }

        Map<?, ?> routeResponse =
                graphHopperRoutingService.calculateRoute(
                        startLocation.getLatitude().doubleValue(),
                        startLocation.getLongitude().doubleValue(),
                        destinationLocation.getLatitude().doubleValue(),
                        destinationLocation.getLongitude().doubleValue());

        Map<?, ?> path =
                extractFirstPath(routeResponse);

        double distanceKm =
                extractDouble(path.get("distance")) / 1000.0;

        double normalTravelTimeMinutes =
                extractDouble(path.get("time")) / 60000.0;

        TrafficAnalysis trafficAnalysis =
                analyzeTraffic(
                        startLocation,
                        destinationLocation);

        double emergencyTravelTimeMinutes =
                calculateEmergencyTravelTime(
                        normalTravelTimeMinutes,
                        trafficAnalysis.trafficLevel());

        EmergencySimulationResponse response =
                new EmergencySimulationResponse();

        response.setEmergencyType(
                normalizeEmergencyType(
                        request.getEmergencyType()));

        response.setStartLocationId(
                startLocation.getId());

        response.setStartLocationName(
                startLocation.getName());

        response.setDestinationLocationId(
                destinationLocation.getId());

        response.setDestinationLocationName(
                destinationLocation.getName());

        response.setRouteDistanceKm(
                round(distanceKm));

        response.setNormalTravelTimeMinutes(
                round(normalTravelTimeMinutes));

        response.setEmergencyTravelTimeMinutes(
                round(emergencyTravelTimeMinutes));

        response.setTrafficLevel(
                trafficAnalysis.trafficLevel());

        response.setCongestionScore(
                trafficAnalysis.congestionScore());

        response.setPriorityStatus(
                "PRIORITY_SIMULATED");

        response.setSignalPriority(
                "SIMULATED");

        response.setSimulationStatus(
                "COMPLETED");

        response.setMessage(
                "Emergency route simulation completed using "
                        + "the local OSM road network. Signal priority "
                        + "is simulated and does not control real traffic signals.");

        return response;
    }

    private TrafficAnalysis analyzeTraffic(
            Location startLocation,
            Location destinationLocation) {

        TrafficRecord startTraffic =
                trafficRecordRepository
                        .findFirstByLocationIdOrderByRecordedAtDesc(
                                startLocation.getId())
                        .orElse(null);

        TrafficRecord destinationTraffic =
                trafficRecordRepository
                        .findFirstByLocationIdOrderByRecordedAtDesc(
                                destinationLocation.getId())
                        .orElse(null);

        TrafficRecord selectedTraffic =
                selectTrafficRecord(
                        startTraffic,
                        destinationTraffic);

        if (selectedTraffic == null) {

            return new TrafficAnalysis(
                    "UNKNOWN",
                    null);
        }

        Double congestionScore = null;

        if (selectedTraffic.getCongestionScore() != null) {
            congestionScore =
                    selectedTraffic
                            .getCongestionScore()
                            .doubleValue();
        }

        String trafficLevel =
                selectedTraffic.getTrafficLevel();

        if (trafficLevel == null
                || trafficLevel.isBlank()) {

            trafficLevel = "UNKNOWN";
        }

        return new TrafficAnalysis(
                trafficLevel.toUpperCase(),
                congestionScore);
    }

    private TrafficRecord selectTrafficRecord(
            TrafficRecord startTraffic,
            TrafficRecord destinationTraffic) {

        if (startTraffic == null) {
            return destinationTraffic;
        }

        if (destinationTraffic == null) {
            return startTraffic;
        }

        if (startTraffic.getRecordedAt() == null) {
            return destinationTraffic;
        }

        if (destinationTraffic.getRecordedAt() == null) {
            return startTraffic;
        }

        if (startTraffic.getRecordedAt()
                .isAfter(destinationTraffic.getRecordedAt())) {

            return startTraffic;
        }

        return destinationTraffic;
    }

    private double calculateEmergencyTravelTime(
            double normalTravelTimeMinutes,
            String trafficLevel) {

        double reductionFactor;

        switch (trafficLevel) {

            case "SEVERE":
                reductionFactor = 0.25;
                break;

            case "HIGH":
                reductionFactor = 0.20;
                break;

            case "MODERATE":
                reductionFactor = 0.15;
                break;

            case "LOW":
                reductionFactor = 0.10;
                break;

            default:
                reductionFactor = 0.10;
                break;
        }

        double simulatedTime =
                normalTravelTimeMinutes
                        * (1.0 - reductionFactor);

        return Math.max(simulatedTime, 0.1);
    }

    private Map<?, ?> extractFirstPath(
            Map<?, ?> routeResponse) {

        if (routeResponse == null) {
            throw new IllegalStateException(
                    "GraphHopper returned no route response.");
        }

        Object pathsObject =
                routeResponse.get("paths");

        if (!(pathsObject instanceof List<?> paths)
                || paths.isEmpty()) {

            throw new IllegalStateException(
                    "GraphHopper did not return a valid route.");
        }

        Object firstPathObject =
                paths.get(0);

        if (!(firstPathObject instanceof Map<?, ?> firstPath)) {

            throw new IllegalStateException(
                    "GraphHopper returned an invalid route path.");
        }

        return firstPath;
    }

    private double extractDouble(Object value) {

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        if (value instanceof String string) {

            try {
                return Double.parseDouble(string);

            } catch (NumberFormatException exception) {

                throw new IllegalStateException(
                        "Invalid numeric value returned by GraphHopper.");
            }
        }

        throw new IllegalStateException(
                "GraphHopper response is missing a required numeric value.");
    }

    private void validateCoordinates(
            Location location,
            String locationLabel) {

        if (location.getLatitude() == null
                || location.getLongitude() == null) {

            throw new IllegalStateException(
                    locationLabel
                            + " location does not have valid coordinates.");
        }
    }

    private String normalizeEmergencyType(
            String emergencyType) {

        if (emergencyType == null
                || emergencyType.isBlank()) {

            throw new IllegalArgumentException(
                    "Emergency type is required.");
        }

        return emergencyType
                .trim()
                .toUpperCase();
    }

    private double round(double value) {

        return Math.round(value * 100.0) / 100.0;
    }

    private record TrafficAnalysis(
            String trafficLevel,
            Double congestionScore) {
    }
}