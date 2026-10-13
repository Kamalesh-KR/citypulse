package com.citypulse.controller;

import com.citypulse.dto.CongestionResponse;
import com.citypulse.dto.TrafficAnalysisResponse;
import com.citypulse.dto.TrafficAnomalyResponse;
import com.citypulse.dto.TrafficHistoricalAnalyticsResponse;
import com.citypulse.dto.TrafficRecordRequest;
import com.citypulse.dto.TrafficRecordResponse;
import com.citypulse.model.Location;
import com.citypulse.model.Road;
import com.citypulse.model.TrafficRecord;
import com.citypulse.service.LocationService;
import com.citypulse.service.RoadService;
import com.citypulse.service.TrafficRecordService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/traffic")
public class TrafficRecordController {

    private final TrafficRecordService trafficRecordService;
    private final RoadService roadService;
    private final LocationService locationService;

    public TrafficRecordController(
            TrafficRecordService trafficRecordService,
            RoadService roadService,
            LocationService locationService) {

        this.trafficRecordService = trafficRecordService;
        this.roadService = roadService;
        this.locationService = locationService;
    }

    // =========================================================
    // TRAFFIC RECORDS
    // =========================================================

    @GetMapping
    public ResponseEntity<List<TrafficRecordResponse>> getAllTrafficRecords() {

        List<TrafficRecordResponse> records = trafficRecordService
                .getAllTrafficRecords()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(records);
    }

    // =========================================================
    // LIVE TRAFFIC
    // =========================================================

    @GetMapping("/live")
    public ResponseEntity<List<TrafficRecordResponse>> getLiveTrafficRecords() {

        List<TrafficRecordResponse> records = trafficRecordService
                .getLiveTrafficRecords()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(records);
    }

    // =========================================================
    // TRAFFIC ANALYSIS
    // =========================================================

    @GetMapping("/analysis/road/{roadId}")
    public ResponseEntity<TrafficAnalysisResponse> analyzeTrafficByRoad(
            @PathVariable Long roadId) {

        return trafficRecordService
                .analyzeTrafficByRoad(roadId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // CONGESTION DETECTION
    // =========================================================

    @GetMapping("/congestion/road/{roadId}")
    public ResponseEntity<CongestionResponse> detectCongestionByRoad(
            @PathVariable Long roadId) {

        return trafficRecordService
                .detectCongestionByRoad(roadId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // TRAFFIC ANOMALY DETECTION
    // =========================================================

    @GetMapping("/anomaly/road/{roadId}")
    public ResponseEntity<TrafficAnomalyResponse> detectTrafficAnomalyByRoad(
            @PathVariable Long roadId) {

        return trafficRecordService
                .detectTrafficAnomalyByRoad(roadId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // HISTORICAL TRAFFIC ANALYTICS
    // =========================================================

    /**
     * Returns historical analytics calculated from
     * all traffic records currently stored in CityPulse.
     *
     * GET /api/traffic/analytics/historical
     */
    @GetMapping("/analytics/historical")
    public ResponseEntity<TrafficHistoricalAnalyticsResponse>
    getHistoricalAnalytics() {

        return trafficRecordService
                .getHistoricalAnalytics()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns historical analytics for a specific road.
     *
     * GET /api/traffic/analytics/historical/road/{roadId}
     */
    @GetMapping("/analytics/historical/road/{roadId}")
    public ResponseEntity<TrafficHistoricalAnalyticsResponse>
    getHistoricalAnalyticsByRoad(
            @PathVariable Long roadId) {

        return trafficRecordService
                .getHistoricalAnalyticsByRoad(roadId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns historical analytics for a specific location.
     *
     * GET /api/traffic/analytics/historical/location/{locationId}
     */
    @GetMapping("/analytics/historical/location/{locationId}")
    public ResponseEntity<TrafficHistoricalAnalyticsResponse>
    getHistoricalAnalyticsByLocation(
            @PathVariable Long locationId) {

        return trafficRecordService
                .getHistoricalAnalyticsByLocation(locationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Returns historical analytics for a specific time range.
     *
     * GET /api/traffic/analytics/historical/between
     * ?start=2026-09-23T19:00:00
     * &end=2026-09-24T20:00:00
     */
    @GetMapping("/analytics/historical/between")
    public ResponseEntity<TrafficHistoricalAnalyticsResponse>
    getHistoricalAnalyticsBetween(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        return trafficRecordService
                .getHistoricalAnalyticsBetween(start, end)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // GET TRAFFIC RECORD BY ID
    // =========================================================

    @GetMapping("/{id}")
    public ResponseEntity<TrafficRecordResponse> getTrafficRecordById(
            @PathVariable Long id) {

        return trafficRecordService
                .getTrafficRecordById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // LATEST TRAFFIC BY ROAD
    // =========================================================

    @GetMapping("/road/{roadId}/latest")
    public ResponseEntity<TrafficRecordResponse> getLatestTrafficRecordByRoad(
            @PathVariable Long roadId) {

        return trafficRecordService
                .getLatestTrafficRecordByRoad(roadId)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // LATEST TRAFFIC BY LOCATION
    // =========================================================

    @GetMapping("/location/{locationId}/latest")
    public ResponseEntity<TrafficRecordResponse> getLatestTrafficRecordByLocation(
            @PathVariable Long locationId) {

        return trafficRecordService
                .getLatestTrafficRecordByLocation(locationId)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // =========================================================
    // TRAFFIC BY ROAD
    // =========================================================

    @GetMapping("/road/{roadId}")
    public ResponseEntity<List<TrafficRecordResponse>> getTrafficRecordsByRoad(
            @PathVariable Long roadId) {

        List<TrafficRecordResponse> records = trafficRecordService
                .getTrafficRecordsByRoad(roadId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(records);
    }

    // =========================================================
    // TRAFFIC BY LOCATION
    // =========================================================

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<TrafficRecordResponse>> getTrafficRecordsByLocation(
            @PathVariable Long locationId) {

        List<TrafficRecordResponse> records = trafficRecordService
                .getTrafficRecordsByLocation(locationId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(records);
    }

    // =========================================================
    // TRAFFIC BY LEVEL
    // =========================================================

    @GetMapping("/level/{trafficLevel}")
    public ResponseEntity<List<TrafficRecordResponse>> getTrafficRecordsByTrafficLevel(
            @PathVariable String trafficLevel) {

        List<TrafficRecordResponse> records = trafficRecordService
                .getTrafficRecordsByTrafficLevel(trafficLevel)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(records);
    }

    // =========================================================
    // TRAFFIC BETWEEN TWO TIMES
    // =========================================================

    @GetMapping("/between")
    public ResponseEntity<List<TrafficRecordResponse>> getTrafficRecordsBetween(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        List<TrafficRecordResponse> records = trafficRecordService
                .getTrafficRecordsBetween(start, end)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(records);
    }

    // =========================================================
    // CREATE TRAFFIC RECORD
    // =========================================================

    @PostMapping
    public ResponseEntity<?> createTrafficRecord(
            @Valid @RequestBody TrafficRecordRequest request) {

        Road road = roadService
                .getRoadById(request.getRoadId())
                .orElse(null);

        if (road == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Road not found with id: " + request.getRoadId());
        }

        Location location = locationService
                .getLocationById(request.getLocationId())
                .orElse(null);

        if (location == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Location not found with id: " + request.getLocationId());
        }

        TrafficRecord trafficRecord =
                convertToEntity(request, road, location);

        TrafficRecord savedRecord =
                trafficRecordService.createTrafficRecord(
                        trafficRecord
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertToResponse(savedRecord));
    }

    // =========================================================
    // UPDATE TRAFFIC RECORD
    // =========================================================

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTrafficRecord(
            @PathVariable Long id,
            @Valid @RequestBody TrafficRecordRequest request) {

        Road road = roadService
                .getRoadById(request.getRoadId())
                .orElse(null);

        if (road == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Road not found with id: " + request.getRoadId());
        }

        Location location = locationService
                .getLocationById(request.getLocationId())
                .orElse(null);

        if (location == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Location not found with id: " + request.getLocationId());
        }

        TrafficRecord trafficRecord =
                convertToEntity(request, road, location);

        TrafficRecord updatedRecord =
                trafficRecordService.updateTrafficRecord(
                        id,
                        trafficRecord
                );

        return ResponseEntity.ok(
                convertToResponse(updatedRecord)
        );
    }

    // =========================================================
    // DELETE TRAFFIC RECORD
    // =========================================================

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrafficRecord(
            @PathVariable Long id) {

        trafficRecordService.deleteTrafficRecord(id);

        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // REQUEST → ENTITY
    // =========================================================

    private TrafficRecord convertToEntity(
            TrafficRecordRequest request,
            Road road,
            Location location) {

        TrafficRecord trafficRecord = new TrafficRecord();

        trafficRecord.setRoad(road);
        trafficRecord.setLocation(location);

        trafficRecord.setVehicleCount(
                request.getVehicleCount()
        );

        if (request.getAverageSpeed() != null) {
            trafficRecord.setAverageSpeed(
                    BigDecimal.valueOf(
                            request.getAverageSpeed()
                    )
            );
        }

        if (request.getTrafficDensity() != null) {
            trafficRecord.setTrafficDensity(
                    BigDecimal.valueOf(
                            request.getTrafficDensity()
                    )
            );
        }

        trafficRecord.setTrafficLevel(
                request.getTrafficLevel()
        );

        if (request.getCongestionScore() != null) {
            trafficRecord.setCongestionScore(
                    BigDecimal.valueOf(
                            request.getCongestionScore()
                    )
            );
        }

        trafficRecord.setRecordedAt(
                request.getRecordedAt()
        );

        trafficRecord.setDataSource(
                request.getDataSource()
        );

        return trafficRecord;
    }

    // =========================================================
    // ENTITY → RESPONSE
    // =========================================================

    private TrafficRecordResponse convertToResponse(
            TrafficRecord trafficRecord) {

        return new TrafficRecordResponse(
                trafficRecord.getId(),
                trafficRecord.getRoad().getId(),
                trafficRecord.getLocation().getId(),
                trafficRecord.getVehicleCount(),
                trafficRecord.getAverageSpeed() != null
                        ? trafficRecord.getAverageSpeed().doubleValue()
                        : null,
                trafficRecord.getTrafficDensity() != null
                        ? trafficRecord.getTrafficDensity().doubleValue()
                        : null,
                trafficRecord.getTrafficLevel(),
                trafficRecord.getCongestionScore() != null
                        ? trafficRecord.getCongestionScore().doubleValue()
                        : null,
                trafficRecord.getRecordedAt(),
                trafficRecord.getDataSource()
        );
    }
}