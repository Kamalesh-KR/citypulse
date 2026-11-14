package com.citypulse.routing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class GraphHopperRoutingService {

    private static final Logger log =
            LoggerFactory.getLogger(GraphHopperRoutingService.class);

    /**
     * Straight-line distance is multiplied by this factor to approximate
     * real road distance. 1.3 is a common average for urban road networks.
     */
    private static final double ROAD_DISTANCE_FACTOR = 1.3;

    /**
     * Average urban driving speed used for the fallback ETA.
     * 40 km/h is a reasonable assumption for a mixed-speed city trip.
     */
    private static final double FALLBACK_AVERAGE_SPEED_KMH = 40.0;

    private final RestClient restClient;

    public GraphHopperRoutingService(
            @Value("${citypulse.routing.graphhopper-url:http://localhost:8989}")
            String graphHopperUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(graphHopperUrl)
                .build();
    }

    public Map<?, ?> calculateRoute(
            double fromLatitude,
            double fromLongitude,
            double toLatitude,
            double toLongitude) {

        try {

            Map<?, ?> response = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/route")
                            .queryParam(
                                    "point",
                                    fromLatitude + "," + fromLongitude
                            )
                            .queryParam(
                                    "point",
                                    toLatitude + "," + toLongitude
                            )
                            .queryParam("profile", "car")
                            .queryParam("locale", "en")
                            .queryParam("calc_points", "true")
                            .queryParam("instructions", "true")
                            .queryParam("points_encoded", "false")
                            .build())
                    .retrieve()
                    .body(Map.class);

            if (response != null) {
                return response;
            }

            log.warn(
                    "GraphHopper returned an empty response; falling back "
                            + "to Haversine estimate.");

            return buildHaversineFallback(
                    fromLatitude,
                    fromLongitude,
                    toLatitude,
                    toLongitude);

        } catch (RuntimeException routingFailure) {

            log.warn(
                    "GraphHopper routing unavailable ({}); falling back "
                            + "to Haversine estimate.",
                    routingFailure.getMessage());

            return buildHaversineFallback(
                    fromLatitude,
                    fromLongitude,
                    toLatitude,
                    toLongitude);
        }
    }

    public Map<?, ?> calculateAlternativeRoutes(
            double fromLatitude,
            double fromLongitude,
            double toLatitude,
            double toLongitude) {

        try {

            Map<?, ?> response = restClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/route")
                            .queryParam(
                                    "point",
                                    fromLatitude + "," + fromLongitude
                            )
                            .queryParam(
                                    "point",
                                    toLatitude + "," + toLongitude
                            )
                            .queryParam("profile", "car")
                            .queryParam("locale", "en")
                            .queryParam("calc_points", "true")
                            .queryParam("instructions", "true")
                            .queryParam("points_encoded", "false")
                            .queryParam(
                                    "algorithm",
                                    "alternative_route"
                            )
                            .build())
                    .retrieve()
                    .body(Map.class);

            if (response != null) {
                return response;
            }

            log.warn(
                    "GraphHopper returned an empty alternative-route "
                            + "response; falling back to Haversine estimate.");

            return buildHaversineFallback(
                    fromLatitude,
                    fromLongitude,
                    toLatitude,
                    toLongitude);

        } catch (RuntimeException routingFailure) {

            log.warn(
                    "GraphHopper alternative-routing unavailable ({}); "
                            + "falling back to Haversine estimate.",
                    routingFailure.getMessage());

            return buildHaversineFallback(
                    fromLatitude,
                    fromLongitude,
                    toLatitude,
                    toLongitude);
        }
    }

    /* =========================================================
       HAVERSINE FALLBACK — GraphHopper-compatible response shape
       ========================================================= */

    /**
     * Builds a response map in the same shape GraphHopper returns,
     * including a GeoJSON-style {@code points.coordinates} array.
     *
     * The {@code coordinates} array contains two points:
     *   [fromLongitude, fromLatitude]
     *   [toLongitude,   toLatitude]
     *
     * This is enough for RouteTrafficAnalysisService to
     * find nearby traffic records at both ends of the trip
     * — which is what we need for accurate route scoring.
     *
     * GraphHopper field units:
     *   - distance: metres
     *   - time:     milliseconds
     */
    private Map<String, Object> buildHaversineFallback(
            double fromLatitude,
            double fromLongitude,
            double toLatitude,
            double toLongitude) {

        double straightLineKm =
                haversineKm(
                        fromLatitude,
                        fromLongitude,
                        toLatitude,
                        toLongitude);

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

        double distanceMetres = roadDistanceKm * 1000.0;
        long timeMillis = Math.round(travelTimeMinutes * 60000.0);

        /* ---------------------------------------------------------
           Points — GeoJSON style, exactly like GraphHopper
           --------------------------------------------------------- */

        List<List<Double>> coordinates =
                List.of(
                        List.of(fromLongitude, fromLatitude),
                        List.of(toLongitude, toLatitude));

        Map<String, Object> points = new LinkedHashMap<>();
        points.put("type", "LineString");
        points.put("coordinates", coordinates);

        /* ---------------------------------------------------------
           Path entry
           --------------------------------------------------------- */

        Map<String, Object> path = new LinkedHashMap<>();
        path.put("distance", distanceMetres);
        path.put("time", timeMillis);
        path.put("weight", travelTimeMinutes);
        path.put("points", points);
        path.put("instructions", List.of());
        path.put("ascend", 0.0);
        path.put("descend", 0.0);
        path.put(
                "description",
                "Haversine fallback (routing engine unavailable)");

        /* ---------------------------------------------------------
           Top-level response
           --------------------------------------------------------- */

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("paths", List.of(path));

        Map<String, Object> info = new LinkedHashMap<>();
        info.put("took", 0);
        info.put(
                "fallback",
                "GraphHopper unreachable — using straight-line estimate");

        response.put("info", info);

        return response;
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
}