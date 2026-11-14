package com.citypulse.service;

import com.citypulse.model.Location;
import com.citypulse.model.Road;
import com.citypulse.model.TrafficRecord;
import com.citypulse.repository.LocationRepository;
import com.citypulse.repository.RoadRepository;
import com.citypulse.repository.TrafficRecordRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * ============================================================
 * CITYPULSE — REALISTIC TRAFFIC SIMULATOR (Feature #5)
 * ============================================================
 *
 * Generates realistic traffic records every 5 minutes.
 *
 * Unlike random simulation, this models:
 *   - Morning rush hour peak  (08:00–09:30)
 *   - Evening rush hour peak  (17:30–19:30)
 *   - Overnight quiet         (00:00–05:00)
 *   - Weekend lighter traffic (Saturday + Sunday)
 *
 * Each record is assigned to the road's actual start location
 * (matched by name) so that weather lookups, map placement,
 * and route analysis all line up with the correct geography.
 *
 * Records are stored in TrafficRecord with dataSource=SIMULATOR
 * and broadcast via WebSocket to all connected clients
 * (existing /topic/traffic).
 */
@Service
public class RealisticTrafficSimulator {

    private final RoadRepository roadRepository;
    private final LocationRepository locationRepository;
    private final TrafficRecordRepository trafficRecordRepository;
    private final WebSocketBroadcastService broadcastService;

    private final Random random = new Random();

    public RealisticTrafficSimulator(
            RoadRepository roadRepository,
            LocationRepository locationRepository,
            TrafficRecordRepository trafficRecordRepository,
            WebSocketBroadcastService broadcastService) {

        this.roadRepository = roadRepository;
        this.locationRepository = locationRepository;
        this.trafficRecordRepository = trafficRecordRepository;
        this.broadcastService = broadcastService;
    }

    /**
     * Runs every 5 minutes.
     * Creates one realistic traffic record per road.
     */
    @Scheduled(fixedRate = 5 * 60 * 1000, initialDelay = 60 * 1000)
    public void generateTrafficSnapshot() {

        try {
            List<Road> roads = roadRepository.findAll();
            List<Location> locations = locationRepository.findAll();

            if (roads.isEmpty() || locations.isEmpty()) {
                return;
            }

            LocalDateTime now = LocalDateTime.now();
            int hourFactor = computeHourFactor(now);
            int dayFactor = computeDayFactor(now.getDayOfWeek());
            int weatherFactor = 0;

            int baseIntensity = hourFactor + dayFactor + weatherFactor;

            for (Road road : roads) {

                Location location = pickNearbyLocation(road, locations);

                TrafficRecord record = buildRecord(
                        road, location, baseIntensity, now);

                TrafficRecord saved = trafficRecordRepository.save(record);

                try {
                    broadcastService.broadcastTrafficUpdate(saved);
                } catch (Exception ignored) {}

                System.out.println(
                        "[Simulator] " + road.getName()
                        + " → location=" + (location != null ? location.getName() : "null")
                        + " vehicles=" + saved.getVehicleCount()
                        + " speed=" + saved.getAverageSpeed()
                        + " level=" + saved.getTrafficLevel());
            }

        } catch (Exception e) {
            System.err.println(
                    "[Simulator] Error: " + e.getMessage());
        }
    }

    /**
     * Prefers to match the road's start_location name to a
     * location in the same city, so MG Road's traffic lands on
     * MG Road's location — not a random one. Falls back to
     * random only if no matching location is found.
     */
    private Location pickNearbyLocation(Road road, List<Location> locations) {

        if (road != null && road.getStartLocation() != null) {

            String startName = road.getStartLocation().trim();

            // Try exact match first
            for (Location loc : locations) {
                if (loc.getName() != null &&
                        loc.getName().equalsIgnoreCase(startName)) {
                    return loc;
                }
            }

            // Try partial match (e.g. "MG Road Junction" contains "MG Road")
            for (Location loc : locations) {
                if (loc.getName() != null &&
                        loc.getName().toLowerCase().contains(startName.toLowerCase())) {
                    return loc;
                }
            }

            // Try reverse (start_location contains location name)
            for (Location loc : locations) {
                if (loc.getName() != null &&
                        startName.toLowerCase().contains(loc.getName().toLowerCase())) {
                    return loc;
                }
            }
        }

        // Fallback: random
        return locations.get(random.nextInt(locations.size()));
    }

    /**
     * Returns 0-100 intensity based on hour of day.
     * Models real rush-hour patterns.
     */
    private int computeHourFactor(LocalDateTime now) {

        int hour = now.getHour();
        int minute = now.getMinute();
        int minutesInDay = hour * 60 + minute;

        // Morning rush: 08:00–09:30 = 480–570 min
        if (minutesInDay >= 480 && minutesInDay <= 570) {
            return 75 + random.nextInt(20);
        }

        // Evening rush: 17:30–19:30 = 1050–1170 min
        if (minutesInDay >= 1050 && minutesInDay <= 1170) {
            return 70 + random.nextInt(25);
        }

        // Overnight: 00:00–05:00
        if (hour <= 5) {
            return 5 + random.nextInt(15);
        }

        // Late evening: 22:00–23:59
        if (hour >= 22) {
            return 15 + random.nextInt(15);
        }

        // Daytime (10:00–17:00)
        if (hour >= 10 && hour <= 16) {
            return 40 + random.nextInt(25);
        }

        return 30 + random.nextInt(25);
    }

    /**
     * Weekend lighter traffic.
     */
    private int computeDayFactor(DayOfWeek day) {
        if (day == DayOfWeek.SATURDAY) return -15;
        if (day == DayOfWeek.SUNDAY) return -25;
        return 0;
    }

    /**
     * Builds one TrafficRecord with realistic correlated fields.
     */
    private TrafficRecord buildRecord(
            Road road,
            Location location,
            int baseIntensity,
            LocalDateTime now) {

        int vehicleCount = 30 + (baseIntensity * 3) + random.nextInt(40);

        double baseSpeed = 75.0;
        double speedReduction = baseIntensity * 0.55;
        double averageSpeed = Math.max(8.0,
                baseSpeed - speedReduction + (random.nextDouble() * 10 - 5));

        double trafficDensity = Math.min(95.0,
                (vehicleCount * 0.15) + (100 - averageSpeed) * 0.4
                + random.nextDouble() * 8);

        double speedPressure = 100.0 - averageSpeed;
        double congestionScore = Math.min(100.0,
                Math.sqrt(trafficDensity * speedPressure) * 1.35);

        String trafficLevel;
        if (congestionScore < 25) {
            trafficLevel = "LOW";
        } else if (congestionScore < 50) {
            trafficLevel = "MODERATE";
        } else if (congestionScore < 75) {
            trafficLevel = "HIGH";
        } else {
            trafficLevel = "SEVERE";
        }

        TrafficRecord record = new TrafficRecord();
        record.setRoad(road);
        record.setLocation(location);
        record.setVehicleCount(vehicleCount);
        record.setAverageSpeed(toBigDecimal(averageSpeed));
        record.setTrafficDensity(toBigDecimal(trafficDensity));
        record.setCongestionScore(toBigDecimal(congestionScore));
        record.setTrafficLevel(trafficLevel);
        record.setDataSource("SIMULATOR");
        record.setRecordedAt(now);

        return record;
    }

    private BigDecimal toBigDecimal(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP);
    }
}