package com.citypulse.controller;

import com.citypulse.dto.AddressLocationResponse;
import com.citypulse.dto.WorldwideRouteRequest;
import com.citypulse.routing.GeocodingService;
import com.citypulse.routing.GraphHopperRoutingService;
import com.citypulse.service.RouteTrafficAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/worldwide-routes")
public class WorldwideRoutingController {

    private final GraphHopperRoutingService graphHopperRoutingService;
    private final GeocodingService geocodingService;
    private final RouteTrafficAnalysisService routeTrafficAnalysisService;

    public WorldwideRoutingController(
            GraphHopperRoutingService graphHopperRoutingService,
            GeocodingService geocodingService,
            RouteTrafficAnalysisService routeTrafficAnalysisService) {

        this.graphHopperRoutingService =
                graphHopperRoutingService;

        this.geocodingService =
                geocodingService;

        this.routeTrafficAnalysisService =
                routeTrafficAnalysisService;
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getRoutingStatus() {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "service",
                "CityPulse Address-Based Routing");

        response.put(
                "status",
                "READY");

        response.put(
                "provider",
                "Local GraphHopper");

        response.put(
                "geocoder",
                "Local Photon");

        response.put(
                "apiKeyRequired",
                false);

        response.put(
                "message",
                "CityPulse can convert addresses to coordinates "
                        + "using Photon and calculate road routes "
                        + "using the local GraphHopper engine.");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/route")
    public ResponseEntity<Map<String, Object>> calculateRoute(
            @Valid @RequestBody WorldwideRouteRequest request) {

        AddressLocationResponse start =
                geocodingService.geocode(
                        request.getFromAddress());

        AddressLocationResponse destination =
                geocodingService.geocode(
                        request.getToAddress());

        Map<?, ?> routeResponse =
                graphHopperRoutingService.calculateAlternativeRoutes(
                        start.getLatitude(),
                        start.getLongitude(),
                        destination.getLatitude(),
                        destination.getLongitude());

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "from",
                start);

        response.put(
                "to",
                destination);

        response.put(
                "routingProvider",
                "Local GraphHopper");

        response.put(
                "geocodingProvider",
                "Local Photon");

        response.put(
                "route",
                routeResponse);

        Object pathsObject =
                routeResponse.get("paths");

        if (pathsObject instanceof List<?> paths) {

            response.put(
                    "routeCount",
                    paths.size());

        } else {

            response.put(
                    "routeCount",
                    0);
        }

        return ResponseEntity.ok(response);
    }

    @PostMapping("/traffic-route")
    public ResponseEntity<Map<String, Object>> calculateTrafficAwareRoute(
            @Valid @RequestBody WorldwideRouteRequest request) {

        AddressLocationResponse start =
                geocodingService.geocode(
                        request.getFromAddress());

        AddressLocationResponse destination =
                geocodingService.geocode(
                        request.getToAddress());

        Map<?, ?> routeResponse =
                graphHopperRoutingService.calculateAlternativeRoutes(
                        start.getLatitude(),
                        start.getLongitude(),
                        destination.getLatitude(),
                        destination.getLongitude());

        List<Map<String, Object>> trafficAnalysis =
                routeTrafficAnalysisService.analyzeRoutes(
                        routeResponse);

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "from",
                start);

        response.put(
                "to",
                destination);

        response.put(
                "routingProvider",
                "Local GraphHopper");

        response.put(
                "geocodingProvider",
                "Local Photon");

        response.put(
                "routeCount",
                trafficAnalysis.size());

        response.put(
                "trafficAnalysis",
                trafficAnalysis);

        return ResponseEntity.ok(response);
    }
}