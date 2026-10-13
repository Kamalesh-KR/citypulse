package com.citypulse.service;

import com.citypulse.model.Location;
import com.citypulse.model.TrafficRecord;
import com.citypulse.repository.LocationRepository;
import com.citypulse.repository.TrafficRecordRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class RouteTrafficAnalysisService {

    private static final double EARTH_RADIUS_KM = 6371.0;

    /*
     * A GraphHopper route point is considered geographically
     * related to a CityPulse Location when it is within this radius.
     */
    private static final double MATCHING_RADIUS_KM = 1.0;

    /*
     * Distance weighting gives closer CityPulse locations
     * stronger influence on route-level traffic analysis.
     *
     * weight = 1 / (1 + distanceFromRouteKm)
     */
    private static final double DISTANCE_WEIGHT_BASE = 1.0;

    /*
     * Route recommendation score weights.
     *
     * Lower score = better route.
     *
     * Travel time       = 40%
     * Congestion impact = 40%
     * Severity impact   = 20%
     */
    private static final double TIME_WEIGHT = 0.40;
    private static final double CONGESTION_WEIGHT = 0.40;
    private static final double SEVERITY_WEIGHT = 0.20;

    private final LocationRepository locationRepository;
    private final TrafficRecordRepository trafficRecordRepository;

    public RouteTrafficAnalysisService(
            LocationRepository locationRepository,
            TrafficRecordRepository trafficRecordRepository) {

        this.locationRepository = locationRepository;
        this.trafficRecordRepository = trafficRecordRepository;
    }

    /**
     * Analyzes GraphHopper route alternatives against the
     * geographic locations and traffic records already stored
     * in CityPulse.
     *
     * This method does NOT generate or invent traffic values.
     */
    public List<Map<String, Object>> analyzeRoutes(
            Map<?, ?> graphHopperResponse) {

        if (graphHopperResponse == null) {
            throw new IllegalArgumentException(
                    "GraphHopper response is required.");
        }

        Object pathsObject =
                graphHopperResponse.get("paths");

        if (!(pathsObject instanceof List<?> paths)
                || paths.isEmpty()) {

            return List.of();
        }

        List<Location> activeLocations =
                locationRepository.findAll()
                        .stream()
                        .filter(location ->
                                Boolean.TRUE.equals(
                                        location.getActive()))
                        .toList();

        List<TrafficRecord> trafficRecords =
                trafficRecordRepository
                        .findAllByOrderByRecordedAtDesc();

        Map<Long, TrafficRecord> latestTrafficByLocation =
                buildLatestTrafficMap(trafficRecords);

        List<Map<String, Object>> routeResults =
                new ArrayList<>();

        for (int routeIndex = 0;
             routeIndex < paths.size();
             routeIndex++) {

            Object pathObject =
                    paths.get(routeIndex);

            if (!(pathObject instanceof Map<?, ?> path)) {
                continue;
            }

            Map<String, Object> result =
                    analyzeSingleRoute(
                            routeIndex + 1,
                            path,
                            activeLocations,
                            latestTrafficByLocation);

            routeResults.add(result);
        }

        /*
         * Route scoring must happen after all routes have
         * been analyzed because travel-time normalization
         * depends on the complete set of alternatives.
         */
        applyRouteRecommendationScores(
                routeResults);

        return routeResults;
    }

    private Map<String, Object> analyzeSingleRoute(
            int routeNumber,
            Map<?, ?> path,
            List<Location> locations,
            Map<Long, TrafficRecord> latestTrafficByLocation) {

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put(
                "routeNumber",
                routeNumber);

        result.put(
                "distanceKm",
                convertMetersToKilometers(
                        path.get("distance")));

        result.put(
                "timeMinutes",
                convertMillisecondsToMinutes(
                        path.get("time")));

        List<double[]> routeCoordinates =
                extractRouteCoordinates(path);

        Map<Long, Double> nearestDistanceByLocation =
                findNearbyLocations(
                        routeCoordinates,
                        locations);

        List<Map<String, Object>> matchedLocations =
                new ArrayList<>();

        List<TrafficDataPoint> matchedTrafficData =
                new ArrayList<>();

        for (Map.Entry<Long, Double> entry :
                nearestDistanceByLocation.entrySet()) {

            Long locationId =
                    entry.getKey();

            Double distanceKm =
                    entry.getValue();

            Location location =
                    findLocationById(
                            locations,
                            locationId);

            if (location == null) {
                continue;
            }

            TrafficRecord trafficRecord =
                    latestTrafficByLocation.get(locationId);

            Map<String, Object> locationResult =
                    new LinkedHashMap<>();

            locationResult.put(
                    "locationId",
                    location.getId());

            locationResult.put(
                    "locationName",
                    location.getName());

            locationResult.put(
                    "distanceFromRouteKm",
                    round(distanceKm));

            if (trafficRecord != null) {

                locationResult.put(
                        "trafficDataAvailable",
                        true);

                locationResult.put(
                        "trafficLevel",
                        trafficRecord.getTrafficLevel());

                locationResult.put(
                        "averageSpeed",
                        trafficRecord.getAverageSpeed());

                locationResult.put(
                        "trafficDensity",
                        trafficRecord.getTrafficDensity());

                locationResult.put(
                        "congestionScore",
                        trafficRecord.getCongestionScore());

                locationResult.put(
                        "recordedAt",
                        trafficRecord.getRecordedAt());

                double distanceWeight =
                        calculateDistanceWeight(
                                distanceKm);

                locationResult.put(
                        "distanceWeight",
                        round(distanceWeight));

                matchedTrafficData.add(
                        new TrafficDataPoint(
                                trafficRecord,
                                distanceKm,
                                distanceWeight));

            } else {

                locationResult.put(
                        "trafficDataAvailable",
                        false);

                locationResult.put(
                        "trafficLevel",
                        null);

                locationResult.put(
                        "averageSpeed",
                        null);

                locationResult.put(
                        "trafficDensity",
                        null);

                locationResult.put(
                        "congestionScore",
                        null);

                locationResult.put(
                        "recordedAt",
                        null);

                locationResult.put(
                        "distanceWeight",
                        null);
            }

            matchedLocations.add(locationResult);
        }

        matchedLocations.sort(
                Comparator.comparing(
                        location ->
                                ((Number) location.get(
                                        "distanceFromRouteKm"))
                                        .doubleValue()));

        result.put(
                "matchedLocationCount",
                matchedLocations.size());

        result.put(
                "matchedLocations",
                matchedLocations);

        result.put(
                "trafficRecordCount",
                matchedTrafficData.size());

        addTrafficSummary(
                result,
                matchedTrafficData);

        return result;
    }

    /**
     * Calculates the final route recommendation score
     * after all GraphHopper alternatives have been analyzed.
     *
     * Lower score = better route.
     */
    private void applyRouteRecommendationScores(
            List<Map<String, Object>> routeResults) {

        if (routeResults.isEmpty()) {
            return;
        }

        double maximumTimeMinutes =
                routeResults.stream()
                        .map(route ->
                                convertToDouble(
                                        route.get(
                                                "timeMinutes")))
                        .filter(value ->
                                value != null
                                        && value > 0.0)
                        .max(Double::compareTo)
                        .orElse(0.0);

        /*
         * First calculate every route's score.
         */
        for (Map<String, Object> route :
                routeResults) {

            double timeScore =
                    calculateTimeScore(
                            route,
                            maximumTimeMinutes);

            Double weightedCongestion =
                    convertToDouble(
                            route.get(
                                    "weightedCongestionScore"));

            Double trafficInfluence =
                    convertToDouble(
                            route.get(
                                    "trafficInfluenceScore"));

            String trafficLevel =
                    route.get("trafficLevel") != null
                            ? route.get("trafficLevel").toString()
                            : null;

            boolean trafficAvailable =
                    "AVAILABLE".equals(
                            route.get(
                                    "trafficDataStatus"));

            Double congestionImpact = null;
            Double severityImpact = null;
            Double routeScore;

            if (trafficAvailable
                    && weightedCongestion != null
                    && trafficInfluence != null) {

                /*
                 * Higher traffic influence means the actual
                 * CityPulse traffic data is more relevant
                 * to this route.
                 *
                 * Therefore influence scales the traffic
                 * impact instead of acting as a penalty itself.
                 */
                congestionImpact =
                        weightedCongestion
                                * trafficInfluence
                                / 100.0;

                double severityScore =
                        getTrafficLevelScore(
                                trafficLevel);

                severityImpact =
                        severityScore
                                * trafficInfluence
                                / 100.0;

                routeScore =
                        (TIME_WEIGHT * timeScore)
                                + (CONGESTION_WEIGHT
                                * congestionImpact)
                                + (SEVERITY_WEIGHT
                                * severityImpact);

                route.put(
                        "trafficScoringStatus",
                        "AVAILABLE");

            } else {

                /*
                 * No traffic values are fabricated.
                 *
                 * If traffic information is unavailable,
                 * the route is scored using travel time only.
                 */
                routeScore =
                        timeScore;

                route.put(
                        "trafficScoringStatus",
                        "UNAVAILABLE");
            }

            route.put(
                    "timeScore",
                    round(timeScore));

            route.put(
                    "congestionImpactScore",
                    congestionImpact == null
                            ? null
                            : round(congestionImpact));

            route.put(
                    "severityImpactScore",
                    severityImpact == null
                            ? null
                            : round(severityImpact));

            route.put(
                    "routeScore",
                    round(routeScore));

            route.put(
                    "recommended",
                    false);
        }

        /*
         * Find the lowest route score.
         */
        Map<String, Object> recommendedRoute =
                routeResults.stream()
                        .min(
                                Comparator.comparingDouble(
                                        route ->
                                                convertToDouble(
                                                        route.get(
                                                                "routeScore"))))
                        .orElse(null);

        if (recommendedRoute == null) {
            return;
        }

        recommendedRoute.put(
                "recommended",
                true);

        /*
         * Assign an explicit rank after calculating scores.
         */
        List<Map<String, Object>> rankedRoutes =
                new ArrayList<>(
                        routeResults);

        rankedRoutes.sort(
                Comparator.comparingDouble(
                        route ->
                                convertToDouble(
                                        route.get(
                                                "routeScore"))));

        for (int index = 0;
             index < rankedRoutes.size();
             index++) {

            rankedRoutes.get(index).put(
                    "recommendationRank",
                    index + 1);
        }

        /*
         * Add a human-readable explanation for the
         * recommendation decision.
         */
        addRecommendationReasons(
                routeResults);
    }

    /**
     * Adds a human-readable explanation describing why
     * each route was or was not recommended.
     *
     * This method does not modify route scores, traffic
     * calculations, ranking, or recommendation selection.
     */
    private void addRecommendationReasons(
            List<Map<String, Object>> routeResults) {

        if (routeResults.isEmpty()) {
            return;
        }

        Map<String, Object> recommendedRoute =
                routeResults.stream()
                        .filter(route ->
                                Boolean.TRUE.equals(
                                        route.get(
                                                "recommended")))
                        .findFirst()
                        .orElse(null);

        if (recommendedRoute == null) {
            return;
        }

        Double recommendedTime =
                convertToDouble(
                        recommendedRoute.get(
                                "timeMinutes"));

        Double recommendedTrafficInfluence =
                convertToDouble(
                        recommendedRoute.get(
                                "trafficInfluenceScore"));

        /*
         * Determine whether the recommended route is a
         * deliberate traffic-aware trade-off:
         *
         * - recommended route takes longer
         * - recommended route has lower traffic influence
         * - comparison route has traffic data available
         *
         * This compares the recommended route against
         * the other alternatives instead of comparing
         * the route with itself.
         */
        boolean lowerTrafficDespiteLongerTime = false;

        if (recommendedTime != null
                && recommendedTrafficInfluence != null) {

            for (Map<String, Object> otherRoute :
                    routeResults) {

                if (otherRoute == recommendedRoute) {
                    continue;
                }

                boolean otherTrafficAvailable =
                        "AVAILABLE".equals(
                                otherRoute.get(
                                        "trafficDataStatus"));

                if (!otherTrafficAvailable) {
                    continue;
                }

                Double otherTime =
                        convertToDouble(
                                otherRoute.get(
                                        "timeMinutes"));

                Double otherTrafficInfluence =
                        convertToDouble(
                                otherRoute.get(
                                        "trafficInfluenceScore"));

                if (otherTime != null
                        && otherTrafficInfluence != null
                        && recommendedTime > otherTime
                        && recommendedTrafficInfluence
                        < otherTrafficInfluence) {

                    lowerTrafficDespiteLongerTime = true;
                    break;
                }
            }
        }

        for (Map<String, Object> route :
                routeResults) {

            boolean isRecommended =
                    Boolean.TRUE.equals(
                            route.get(
                                    "recommended"));

            boolean trafficAvailable =
                    "AVAILABLE".equals(
                            route.get(
                                    "trafficDataStatus"));

            if (!trafficAvailable) {

                if (isRecommended) {

                    route.put(
                            "recommendationReason",
                            "Traffic data is unavailable for "
                                    + "this route. The recommendation "
                                    + "is based on route travel time.");

                } else {

                    route.put(
                            "recommendationReason",
                            "Traffic data is unavailable for "
                                    + "this route, and another "
                                    + "available route has a lower "
                                    + "overall route score.");
                }

                continue;
            }

            if (isRecommended) {

                if (lowerTrafficDespiteLongerTime) {

                    route.put(
                            "recommendationReason",
                            "Recommended because this route "
                                    + "has lower traffic impact "
                                    + "despite a longer travel time.");

                } else {

                    route.put(
                            "recommendationReason",
                            "Recommended because this route "
                                    + "has the lowest overall score "
                                    + "after considering travel time, "
                                    + "congestion, and traffic severity.");
                }

            } else {

                route.put(
                        "recommendationReason",
                        "Not recommended because another "
                                + "available route has a lower "
                                + "overall score after considering "
                                + "travel time and traffic impact.");
            }
        }
    }

    /**
     * Normalizes route travel time to a 0-100 scale.
     *
     * Shortest route = lower score.
     *
     * Formula:
     *
     * route time
     * -----------
     * maximum time × 100
     */
    private double calculateTimeScore(
            Map<String, Object> route,
            double maximumTimeMinutes) {

        Double timeMinutes =
                convertToDouble(
                        route.get(
                                "timeMinutes"));

        if (timeMinutes == null
                || timeMinutes < 0.0
                || maximumTimeMinutes <= 0.0) {

            return 100.0;
        }

        return Math.min(
                100.0,
                (timeMinutes
                        / maximumTimeMinutes)
                        * 100.0);
    }

    private Map<Long, TrafficRecord> buildLatestTrafficMap(
            List<TrafficRecord> trafficRecords) {

        Map<Long, TrafficRecord> latestByLocation =
                new HashMap<>();

        for (TrafficRecord trafficRecord :
                trafficRecords) {

            if (trafficRecord == null
                    || trafficRecord.getLocation() == null
                    || trafficRecord.getLocation().getId() == null) {

                continue;
            }

            Long locationId =
                    trafficRecord.getLocation().getId();

            /*
             * The repository returns records in descending
             * recordedAt order, so the first record encountered
             * for a location is its latest record.
             */
            latestByLocation.putIfAbsent(
                    locationId,
                    trafficRecord);
        }

        return latestByLocation;
    }

    private Map<Long, Double> findNearbyLocations(
            List<double[]> routeCoordinates,
            List<Location> locations) {

        Map<Long, Double> nearestDistanceByLocation =
                new HashMap<>();

        for (Location location : locations) {

            if (location.getLatitude() == null
                    || location.getLongitude() == null
                    || location.getId() == null) {

                continue;
            }

            double locationLatitude =
                    location.getLatitude().doubleValue();

            double locationLongitude =
                    location.getLongitude().doubleValue();

            double nearestDistanceKm =
                    Double.MAX_VALUE;

            for (double[] coordinate :
                    routeCoordinates) {

                if (coordinate.length < 2) {
                    continue;
                }

                /*
                 * GraphHopper / GeoJSON:
                 *
                 * coordinate[0] = longitude
                 * coordinate[1] = latitude
                 */
                double routeLongitude =
                        coordinate[0];

                double routeLatitude =
                        coordinate[1];

                double distanceKm =
                        calculateDistanceKm(
                                routeLatitude,
                                routeLongitude,
                                locationLatitude,
                                locationLongitude);

                if (distanceKm < nearestDistanceKm) {
                    nearestDistanceKm = distanceKm;
                }
            }

            if (nearestDistanceKm <= MATCHING_RADIUS_KM) {

                nearestDistanceByLocation.put(
                        location.getId(),
                        nearestDistanceKm);
            }
        }

        return nearestDistanceByLocation;
    }

    private List<double[]> extractRouteCoordinates(
            Map<?, ?> path) {

        List<double[]> coordinates =
                new ArrayList<>();

        Object pointsObject =
                path.get("points");

        if (!(pointsObject instanceof Map<?, ?> points)) {
            return coordinates;
        }

        Object coordinatesObject =
                points.get("coordinates");

        if (!(coordinatesObject instanceof List<?> coordinateList)) {
            return coordinates;
        }

        for (Object coordinateObject :
                coordinateList) {

            if (!(coordinateObject instanceof List<?> coordinate)
                    || coordinate.size() < 2) {

                continue;
            }

            Double longitude =
                    convertToDouble(
                            coordinate.get(0));

            Double latitude =
                    convertToDouble(
                            coordinate.get(1));

            if (longitude == null
                    || latitude == null) {

                continue;
            }

            coordinates.add(
                    new double[]{
                            longitude,
                            latitude
                    });
        }

        return coordinates;
    }

    private Location findLocationById(
            List<Location> locations,
            Long locationId) {

        for (Location location : locations) {

            if (location.getId() != null
                    && location.getId().equals(locationId)) {

                return location;
            }
        }

        return null;
    }

    private void addTrafficSummary(
            Map<String, Object> result,
            List<TrafficDataPoint> trafficDataPoints) {

        if (trafficDataPoints.isEmpty()) {

            result.put(
                    "trafficDataStatus",
                    "UNAVAILABLE");

            result.put(
                    "trafficLevel",
                    null);

            result.put(
                    "averageSpeed",
                    null);

            result.put(
                    "averageCongestionScore",
                    null);

            result.put(
                    "weightedAverageSpeed",
                    null);

            result.put(
                    "weightedCongestionScore",
                    null);

            result.put(
                    "trafficInfluenceScore",
                    null);

            return;
        }

        double totalSpeed = 0.0;
        double totalCongestion = 0.0;

        double totalWeightedSpeed = 0.0;
        double totalWeightedCongestion = 0.0;

        double totalSpeedWeight = 0.0;
        double totalCongestionWeight = 0.0;
        double totalInfluenceWeight = 0.0;

        int speedCount = 0;
        int congestionCount = 0;

        String worstTrafficLevel = null;
        int worstTrafficScore = -1;

        for (TrafficDataPoint dataPoint :
                trafficDataPoints) {

            TrafficRecord trafficRecord =
                    dataPoint.trafficRecord();

            double weight =
                    dataPoint.distanceWeight();

            totalInfluenceWeight += weight;

            if (trafficRecord.getAverageSpeed() != null) {

                double speed =
                        trafficRecord
                                .getAverageSpeed()
                                .doubleValue();

                totalSpeed += speed;

                totalWeightedSpeed +=
                        speed * weight;

                totalSpeedWeight += weight;

                speedCount++;
            }

            if (trafficRecord.getCongestionScore() != null) {

                double congestion =
                        trafficRecord
                                .getCongestionScore()
                                .doubleValue();

                totalCongestion += congestion;

                totalWeightedCongestion +=
                        congestion * weight;

                totalCongestionWeight += weight;

                congestionCount++;
            }

            String trafficLevel =
                    trafficRecord.getTrafficLevel();

            int trafficScore =
                    getTrafficLevelScore(
                            trafficLevel);

            if (trafficScore > worstTrafficScore) {

                worstTrafficScore =
                        trafficScore;

                worstTrafficLevel =
                        trafficLevel;
            }
        }

        result.put(
                "trafficDataStatus",
                "AVAILABLE");

        result.put(
                "trafficLevel",
                worstTrafficLevel);

        if (speedCount > 0) {

            result.put(
                    "averageSpeed",
                    round(
                            totalSpeed / speedCount));

        } else {

            result.put(
                    "averageSpeed",
                    null);
        }

        if (congestionCount > 0) {

            result.put(
                    "averageCongestionScore",
                    round(
                            totalCongestion
                                    / congestionCount));

        } else {

            result.put(
                    "averageCongestionScore",
                    null);
        }

        if (totalSpeedWeight > 0.0) {

            result.put(
                    "weightedAverageSpeed",
                    round(
                            totalWeightedSpeed
                                    / totalSpeedWeight));

        } else {

            result.put(
                    "weightedAverageSpeed",
                    null);
        }

        if (totalCongestionWeight > 0.0) {

            result.put(
                    "weightedCongestionScore",
                    round(
                            totalWeightedCongestion
                                    / totalCongestionWeight));

        } else {

            result.put(
                    "weightedCongestionScore",
                    null);
        }

        /*
         * Represents the strength of available CityPulse
         * traffic influence on this route.
         *
         * weight = 1 / (1 + distanceFromRouteKm)
         *
         * This is an influence indicator, not a traffic
         * severity score.
         */
        if (totalInfluenceWeight > 0.0) {

            double averageInfluence =
                    totalInfluenceWeight
                            / trafficDataPoints.size();

            double influenceScore =
                    Math.min(
                            100.0,
                            averageInfluence * 100.0);

            result.put(
                    "trafficInfluenceScore",
                    round(influenceScore));

        } else {

            result.put(
                    "trafficInfluenceScore",
                    null);
        }
    }

    private double calculateDistanceWeight(
            double distanceKm) {

        if (distanceKm < 0.0) {
            return 0.0;
        }

        return 1.0
                / (DISTANCE_WEIGHT_BASE + distanceKm);
    }

    private int getTrafficLevelScore(
            String trafficLevel) {

        if (trafficLevel == null) {
            return 0;
        }

        return switch (
                trafficLevel.trim().toUpperCase()) {

            case "LOW" -> 20;
            case "MODERATE" -> 45;
            case "HIGH" -> 70;
            case "SEVERE" -> 95;
            default -> 0;
        };
    }

    private double calculateDistanceKm(
            double latitude1,
            double longitude1,
            double latitude2,
            double longitude2) {

        double latitude1Radians =
                Math.toRadians(latitude1);

        double latitude2Radians =
                Math.toRadians(latitude2);

        double deltaLatitudeRadians =
                Math.toRadians(
                        latitude2 - latitude1);

        double deltaLongitudeRadians =
                Math.toRadians(
                        longitude2 - longitude1);

        double a =
                Math.sin(
                        deltaLatitudeRadians / 2)
                        * Math.sin(
                                deltaLatitudeRadians / 2)
                        +
                        Math.cos(
                                latitude1Radians)
                                * Math.cos(
                                        latitude2Radians)
                                * Math.sin(
                                        deltaLongitudeRadians / 2)
                                * Math.sin(
                                        deltaLongitudeRadians / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }

    private Double convertToDouble(
            Object value) {

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        if (value instanceof String string) {

            try {
                return Double.parseDouble(
                        string);

            } catch (NumberFormatException exception) {

                return null;
            }
        }

        return null;
    }

    private Double convertMetersToKilometers(
            Object value) {

        Double meters =
                convertToDouble(value);

        if (meters == null) {
            return null;
        }

        return round(
                meters / 1000.0);
    }

    private Double convertMillisecondsToMinutes(
            Object value) {

        Double milliseconds =
                convertToDouble(value);

        if (milliseconds == null) {
            return null;
        }

        return round(
                milliseconds / 60000.0);
    }

    private double round(
            double value) {

        return BigDecimal
                .valueOf(value)
                .setScale(
                        2,
                        RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * Holds an actual CityPulse TrafficRecord together
     * with its geographic relationship to a GraphHopper route.
     */
    private record TrafficDataPoint(
            TrafficRecord trafficRecord,
            double distanceKm,
            double distanceWeight) {
    }
}