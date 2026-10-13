package com.citypulse.controller;

import com.citypulse.dto.CitizenReportRequest;
import com.citypulse.dto.CitizenReportResponse;
import com.citypulse.model.CitizenReport;
import com.citypulse.model.Location;
import com.citypulse.model.Road;
import com.citypulse.model.User;
import com.citypulse.repository.LocationRepository;
import com.citypulse.repository.RoadRepository;
import com.citypulse.repository.UserRepository;
import com.citypulse.service.CitizenReportService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
public class CitizenReportController {

    private final CitizenReportService citizenReportService;
    private final UserRepository userRepository;
    private final RoadRepository roadRepository;
    private final LocationRepository locationRepository;

    public CitizenReportController(
            CitizenReportService citizenReportService,
            UserRepository userRepository,
            RoadRepository roadRepository,
            LocationRepository locationRepository) {

        this.citizenReportService = citizenReportService;
        this.userRepository = userRepository;
        this.roadRepository = roadRepository;
        this.locationRepository = locationRepository;
    }

    @GetMapping
    public ResponseEntity<List<CitizenReportResponse>> getAllReports() {

        List<CitizenReportResponse> responses =
                citizenReportService.getAllReports()
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CitizenReportResponse> getReportById(
            @PathVariable Long id) {

        return citizenReportService.getReportById(id)
                .map(this::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CitizenReportResponse>> getReportsByUser(
            @PathVariable Long userId) {

        List<CitizenReportResponse> responses =
                citizenReportService.getReportsByUser(userId)
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/road/{roadId}")
    public ResponseEntity<List<CitizenReportResponse>> getReportsByRoad(
            @PathVariable Long roadId) {

        List<CitizenReportResponse> responses =
                citizenReportService.getReportsByRoad(roadId)
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<CitizenReportResponse>> getReportsByLocation(
            @PathVariable Long locationId) {

        List<CitizenReportResponse> responses =
                citizenReportService.getReportsByLocation(locationId)
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/type/{reportType}")
    public ResponseEntity<List<CitizenReportResponse>> getReportsByType(
            @PathVariable String reportType) {

        List<CitizenReportResponse> responses =
                citizenReportService.getReportsByType(reportType)
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<CitizenReportResponse>> getReportsByStatus(
            @PathVariable String status) {

        List<CitizenReportResponse> responses =
                citizenReportService.getReportsByStatus(status)
                        .stream()
                        .map(this::toResponse)
                        .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    @PostMapping
    public ResponseEntity<CitizenReportResponse> createReport(
            @Valid @RequestBody CitizenReportRequest request) {

        CitizenReport report = fromRequest(request);

        CitizenReport savedReport =
                citizenReportService.createReport(report);

        return ResponseEntity.ok(toResponse(savedReport));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CitizenReportResponse> updateReport(
            @PathVariable Long id,
            @Valid @RequestBody CitizenReportRequest request) {

        CitizenReport updatedReport = fromRequest(request);

        CitizenReport savedReport =
                citizenReportService.updateReport(
                        id,
                        updatedReport);

        return ResponseEntity.ok(toResponse(savedReport));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReport(
            @PathVariable Long id) {

        citizenReportService.deleteReport(id);

        return ResponseEntity.noContent().build();
    }

    private CitizenReport fromRequest(
            CitizenReportRequest request) {

        CitizenReport report = new CitizenReport();

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found with id: "
                                        + request.getUserId()));

        report.setUser(user);

        if (request.getRoadId() != null) {

            Road road = roadRepository.findById(
                    request.getRoadId()
            ).orElseThrow(() ->
                    new RuntimeException(
                            "Road not found with id: "
                                    + request.getRoadId()));

            report.setRoad(road);
        }

        if (request.getLocationId() != null) {

            Location location = locationRepository.findById(
                    request.getLocationId()
            ).orElseThrow(() ->
                    new RuntimeException(
                            "Location not found with id: "
                                    + request.getLocationId()));

            report.setLocation(location);
        }

        report.setReportType(request.getReportType());
        report.setDescription(request.getDescription());

        if (request.getLatitude() != null) {
            report.setLatitude(
                    BigDecimal.valueOf(request.getLatitude()));
        }

        if (request.getLongitude() != null) {
            report.setLongitude(
                    BigDecimal.valueOf(request.getLongitude()));
        }

        report.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : "PENDING"
        );

        return report;
    }

    private CitizenReportResponse toResponse(
            CitizenReport report) {

        return new CitizenReportResponse(
                report.getId(),

                report.getUser() != null
                        ? report.getUser().getId()
                        : null,

                report.getRoad() != null
                        ? report.getRoad().getId()
                        : null,

                report.getLocation() != null
                        ? report.getLocation().getId()
                        : null,

                report.getReportType(),
                report.getDescription(),

                report.getLatitude() != null
                        ? report.getLatitude().doubleValue()
                        : null,

                report.getLongitude() != null
                        ? report.getLongitude().doubleValue()
                        : null,

                report.getStatus(),
                report.getReportedAt(),
                report.getReviewedAt()
        );
    }
}