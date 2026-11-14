package com.citypulse.service;

import com.citypulse.dto.WeatherImpactResponse;
import com.citypulse.model.Location;
import com.citypulse.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * ============================================================
 * CITYPULSE — ROUTE WEATHER ENRICHER (Feature #4)
 * ============================================================
 *
 * Enriches each route map with weather data for the DESTINATION.
 *
 * Flow:
 *   1. Take destination lat/lon from the controller
 *   2. Find nearest CityPulse Location to that coordinate
 *   3. Look up weather impact for that location
 *   4. Compute a multiplier (rain/storm = higher ETA)
 *   5. Attach weather keys to every route map
 *
 * If weather lookup fails, routes are returned unchanged.
 */
@Service
public class RouteWeatherEnricher {

    private static final double EARTH_RADIUS_KM = 6371.0;

    private final WeatherImpactService weatherImpactService;
    private final LocationRepository locationRepository;

    public RouteWeatherEnricher(
            WeatherImpactService weatherImpactService,
            LocationRepository locationRepository) {
        this.weatherImpactService = weatherImpactService;
        this.locationRepository = locationRepository;
    }

    /**
     * Enriches routes with weather data based on the
     * destination coordinates.
     *
     * @param routes     list of route maps from RouteTrafficAnalysisService
     * @param destLat    destination latitude
     * @param destLon    destination longitude
     */
    public void enrich(
            List<Map<String, Object>> routes,
            Double destLat,
            Double destLon) {

        if (routes == null || routes.isEmpty()) return;
        if (destLat == null || destLon == null) {
            attachNoWeather(routes);
            return;
        }

        WeatherImpactResponse weather = findWeatherForCoordinate(destLat, destLon);

        if (weather == null) {
            attachNoWeather(routes);
            return;
        }

        double score = weather.getWeatherImpactScore() != null
                ? weather.getWeatherImpactScore()
                : 0.0;

        String level = weather.getWeatherImpactLevel() != null
                ? weather.getWeatherImpactLevel()
                : "LOW";

        String condition = weather.getWeatherCondition() != null
                ? weather.getWeatherCondition()
                : "Unknown";

        double multiplier = computeMultiplier(level, score);

        String emoji = pickEmoji(condition);
        int pct = (int) Math.round((multiplier - 1.0) * 100);

        for (Map<String, Object> route : routes) {
            if (route == null) continue;

            Double baseMinutes = extractDouble(route, "timeMinutes", "estimatedTimeMinutes");
            Double adjustedMinutes = baseMinutes != null
                    ? Math.round(baseMinutes * multiplier * 10.0) / 10.0
                    : null;

            String badge = null;
            if (multiplier > 1.0) {
                badge = emoji + " Weather-adjusted: +" + pct + "% ETA"
                        + (adjustedMinutes != null
                            ? String.format(" (%.0f min effective)", adjustedMinutes)
                            : "");
            }

            route.put("weatherImpactScore", score);
            route.put("weatherImpactLevel", level);
            route.put("weatherCondition", condition);
            route.put("weatherMultiplier", multiplier);
            route.put("weatherAdjustedMinutes", adjustedMinutes);
            route.put("weatherBadge", badge);
        }
    }

    private WeatherImpactResponse findWeatherForCoordinate(double lat, double lon) {

        try {
            List<Location> all = locationRepository.findAll();
            if (all == null || all.isEmpty()) return null;

            Location nearest = null;
            double nearestDistance = Double.MAX_VALUE;

            for (Location loc : all) {
                if (loc == null
                        || loc.getLatitude() == null
                        || loc.getLongitude() == null) continue;

                double d = haversineKm(
                        lat, lon,
                        loc.getLatitude().doubleValue(),
                        loc.getLongitude().doubleValue());

                if (d < nearestDistance) {
                    nearestDistance = d;
                    nearest = loc;
                }
            }

            if (nearest == null || nearest.getId() == null) return null;

            var result = weatherImpactService.analyzeLocation(nearest.getId());
            return result != null ? result.orElse(null) : null;

        } catch (Exception e) {
            System.err.println(
                    "[Weather Enricher] Lookup failed: " + e.getMessage());
            return null;
        }
    }

    private void attachNoWeather(List<Map<String, Object>> routes) {
        for (Map<String, Object> route : routes) {
            if (route == null) continue;
            route.put("weatherImpactScore", 0.0);
            route.put("weatherImpactLevel", "UNAVAILABLE");
            route.put("weatherCondition", "Unknown");
            route.put("weatherMultiplier", 1.0);
            route.put("weatherBadge", null);
        }
    }

    private double computeMultiplier(String level, double score) {
        String norm = level == null ? "" : level.toUpperCase();
        switch (norm) {
            case "SEVERE":   return 1.40;
            case "HIGH":     return 1.25;
            case "MODERATE": return 1.15;
            case "LOW":
                if (score >= 10.0) return 1.08;
                return 1.00;
            default:         return 1.00;
        }
    }

    private String pickEmoji(String condition) {
        if (condition == null) return "⛅";
        String c = condition.toLowerCase();
        if (c.contains("storm") || c.contains("thunder")) return "⛈";
        if (c.contains("snow"))                          return "❄";
        if (c.contains("rain") || c.contains("drizzle")) return "🌧";
        if (c.contains("fog") || c.contains("mist"))     return "🌫";
        if (c.contains("cloud"))                         return "☁";
        if (c.contains("clear") || c.contains("sun"))    return "☀";
        return "🌦";
    }

    private Double extractDouble(Map<String, Object> map, String... keys) {
        for (String key : keys) {
            Object v = map.get(key);
            if (v == null) continue;
            if (v instanceof Number n) return n.doubleValue();
            try { return Double.valueOf(String.valueOf(v)); }
            catch (NumberFormatException ignored) {}
        }
        return null;
    }

    private double haversineKm(
            double lat1, double lon1,
            double lat2, double lon2) {

        double lat1r = Math.toRadians(lat1);
        double lat2r = Math.toRadians(lat2);
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1r) * Math.cos(lat2r)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
}