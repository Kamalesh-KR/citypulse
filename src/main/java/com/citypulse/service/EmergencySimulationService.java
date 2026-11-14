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

    /**
     * Straight-line distance is multiplied by this factor to approximate
     * real road distance. 1.3 is a common average for urban road networks.
     */
    private static final double ROAD_DISTANCE_FACTOR = 1.3;

    /**
     * Average urban driving speed used for the Haversine fallback ETA.
     * 40 km/h is a reasonable assumption for a mixed-speed city trip.
     */
    private static final double FALLBACK_AVERAGE_SPEED_KMH = 40.0;

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

        RouteEstimate route = resolveRoute(
                startLocation,
                destinationLocation);

        TrafficAnalysis trafficAnalysis =
                analyzeTraffic(
                        startLocation,
                        destinationLocation);

        double emergencyTravelTimeMinutes =
                calculateEmergencyTravelTime(
                        route.normalTravelTimeMinutes(),
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
                round(route.distanceKm()));

        response.setNormalTravelTimeMinutes(
                round(route.normalTravelTimeMinutes()));

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
                        + route.sourceDescription()
                        + ". Signal priority is simulated and does not control "
                        + "real traffic signals.");

        return response;
    }

    /* =========================================================
       ROUTE RESOLUTION — OSRM with Haversine fallback
       ========================================================= */

    private RouteEstimate resolveRoute(
            Location startLocation,
            Location destinationLocation) {

        try {

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

            return new RouteEstimate(
                    distanceKm,
                    normalTravelTimeMinutes,
                    "the local OSM road network");

        } catch (RuntimeException routingFailure) {

            // OSRM/GraphHopper unavailable — fall back to Haversine
            double straightLineKm =
                    haversineKm(
                            startLocation.getLatitude().doubleValue(),
                            startLocation.getLongitude().doubleValue(),
                            destinationLocation.getLatitude().doubleValue(),
                            destinationLocation.getLongitude().doubleValue());

            double roadDistanceKm =
                    straightLineKm * ROAD_DISTANCE_FACTOR;

            double travelTimeMinutes =
                    (roadDistanceKm / FALLBACK_AVERAGE_SPEED_KMH) * 60.0;

            // Guard against zero/degenerate results
            if (roadDistanceKm <= 0.05) {
                roadDistanceKm = 0.05;
            }
            if (travelTimeMinutes < 0.5) {
                travelTimeMinutes = 0.5;
            }

            return new RouteEstimate(
                    roadDistanceKm,
                    travelTimeMinutes,
                    "a straight-line distance estimate (routing server "
                            + "unavailable)");
        }
    }

    /**
     * Great-circle distance between two lat/lon points, in kilometres.
     */
    private double haversineKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double deltaLat = Math.toRadians(lat2 - lat1);
        double deltaLon = Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(deltaLat / 2.0) * Math.sin(deltaLat / 2.0)
                        + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                        * Math.sin(deltaLon / 2.0) * Math.sin(deltaLon / 2.0);

        double c = 2.0 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));

        return EARTH_RADIUS_KM * c;
    }

    /* =========================================================
       TRAFFIC ANALYSIS (unchanged)
       ========================================================= */

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

    /* =========================================================
       GraphHopper / OSRM response parsing (unchanged)
       ========================================================= */

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

    /* =========================================================
       VALIDATION + UTILITIES (unchanged)
       ========================================================= */

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

    /* =========================================================
       INTERNAL VALUE TYPES
       ========================================================= */

    private record TrafficAnalysis(
            String trafficLevel,
            Double congestionScore) {
    }

    private record RouteEstimate(
            double distanceKm,
            double normalTravelTimeMinutes,
            String sourceDescription) {
    }
}