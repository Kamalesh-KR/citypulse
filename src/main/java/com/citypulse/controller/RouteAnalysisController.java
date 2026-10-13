package com.citypulse.controller;

import com.citypulse.dto.SmartRouteRequest;
import com.citypulse.dto.SmartRouteResponse;
import com.citypulse.model.RouteAnalysis;
import com.citypulse.service.RouteAnalysisService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/route-analysis")
public class RouteAnalysisController {

private final RouteAnalysisService routeAnalysisService;

public RouteAnalysisController(
        RouteAnalysisService routeAnalysisService) {

    this.routeAnalysisService = routeAnalysisService;
}

@GetMapping
public ResponseEntity<List<RouteAnalysis>> getAllRouteAnalyses() {
    return ResponseEntity.ok(
            routeAnalysisService.getAllRouteAnalyses());
}

@GetMapping("/{id}")
public ResponseEntity<RouteAnalysis> getRouteAnalysisById(
        @PathVariable Long id) {

    return routeAnalysisService.getRouteAnalysisById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@GetMapping("/start/{startLocationId}")
public ResponseEntity<List<RouteAnalysis>>
getAnalysesByStartLocation(
        @PathVariable Long startLocationId) {

    return ResponseEntity.ok(
            routeAnalysisService
                    .getAnalysesByStartLocation(startLocationId));
}

@GetMapping("/destination/{destinationLocationId}")
public ResponseEntity<List<RouteAnalysis>>
getAnalysesByDestinationLocation(
        @PathVariable Long destinationLocationId) {

    return ResponseEntity.ok(
            routeAnalysisService
                    .getAnalysesByDestinationLocation(
                            destinationLocationId));
}

@GetMapping("/type/{analysisType}")
public ResponseEntity<List<RouteAnalysis>> getAnalysesByType(
        @PathVariable String analysisType) {

    return ResponseEntity.ok(
            routeAnalysisService.getAnalysesByType(analysisType));
}

@GetMapping("/between")
public ResponseEntity<List<RouteAnalysis>> getAnalysesBetween(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime start,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime end) {

    return ResponseEntity.ok(
            routeAnalysisService.getAnalysesBetween(start, end));
}

@PostMapping
public ResponseEntity<RouteAnalysis> createRouteAnalysis(
        @RequestBody RouteAnalysis routeAnalysis) {

    return ResponseEntity.ok(
            routeAnalysisService
                    .createRouteAnalysis(routeAnalysis));
}

@PutMapping("/{id}")
public ResponseEntity<RouteAnalysis> updateRouteAnalysis(
        @PathVariable Long id,
        @RequestBody RouteAnalysis routeAnalysis) {

    return ResponseEntity.ok(
            routeAnalysisService.updateRouteAnalysis(
                    id,
                    routeAnalysis));
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteRouteAnalysis(
        @PathVariable Long id) {

    routeAnalysisService.deleteRouteAnalysis(id);
    return ResponseEntity.noContent().build();
}

@PostMapping("/recommend")
public ResponseEntity<List<SmartRouteResponse>> recommendRoutes(
        @Valid @RequestBody SmartRouteRequest request) {

    return ResponseEntity.ok(
            routeAnalysisService.recommendRoutes(
                    request.getStartLocationId(),
                    request.getDestinationLocationId()));
}

}
