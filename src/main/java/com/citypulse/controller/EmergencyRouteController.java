package com.citypulse.controller;

import com.citypulse.dto.EmergencySimulationRequest;
import com.citypulse.dto.EmergencySimulationResponse;
import com.citypulse.model.EmergencyRoute;
import com.citypulse.service.EmergencyRouteService;
import com.citypulse.service.EmergencySimulationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emergency-routes")
public class EmergencyRouteController {

    private final EmergencyRouteService emergencyRouteService;
    private final EmergencySimulationService emergencySimulationService;

    public EmergencyRouteController(
            EmergencyRouteService emergencyRouteService,
            EmergencySimulationService emergencySimulationService) {

        this.emergencyRouteService =
                emergencyRouteService;

        this.emergencySimulationService =
                emergencySimulationService;
    }

    @GetMapping
    public ResponseEntity<List<EmergencyRoute>> getAllEmergencyRoutes() {

        return ResponseEntity.ok(
                emergencyRouteService
                        .getAllEmergencyRoutes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmergencyRoute> getEmergencyRouteById(
            @PathVariable Long id) {

        return emergencyRouteService
                .getEmergencyRouteById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/type/{emergencyType}")
    public ResponseEntity<List<EmergencyRoute>>
    getRoutesByEmergencyType(
            @PathVariable String emergencyType) {

        return ResponseEntity.ok(
                emergencyRouteService
                        .getRoutesByEmergencyType(
                                emergencyType));
    }

    @GetMapping("/start/{startLocationId}")
    public ResponseEntity<List<EmergencyRoute>>
    getRoutesByStartLocation(
            @PathVariable Long startLocationId) {

        return ResponseEntity.ok(
                emergencyRouteService
                        .getRoutesByStartLocation(
                                startLocationId));
    }

    @GetMapping("/destination/{destinationLocationId}")
    public ResponseEntity<List<EmergencyRoute>>
    getRoutesByDestinationLocation(
            @PathVariable Long destinationLocationId) {

        return ResponseEntity.ok(
                emergencyRouteService
                        .getRoutesByDestinationLocation(
                                destinationLocationId));
    }

    @GetMapping("/priority/{priorityStatus}")
    public ResponseEntity<List<EmergencyRoute>>
    getRoutesByPriorityStatus(
            @PathVariable String priorityStatus) {

        return ResponseEntity.ok(
                emergencyRouteService
                        .getRoutesByPriorityStatus(
                                priorityStatus));
    }

    @GetMapping("/simulation/{simulationStatus}")
    public ResponseEntity<List<EmergencyRoute>>
    getRoutesBySimulationStatus(
            @PathVariable String simulationStatus) {

        return ResponseEntity.ok(
                emergencyRouteService
                        .getRoutesBySimulationStatus(
                                simulationStatus));
    }

    @PostMapping
    public ResponseEntity<EmergencyRoute>
    createEmergencyRoute(
            @RequestBody EmergencyRoute emergencyRoute) {

        return ResponseEntity.ok(
                emergencyRouteService
                        .createEmergencyRoute(
                                emergencyRoute));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmergencyRoute>
    updateEmergencyRoute(
            @PathVariable Long id,
            @RequestBody EmergencyRoute emergencyRoute) {

        return ResponseEntity.ok(
                emergencyRouteService.updateEmergencyRoute(
                        id,
                        emergencyRoute));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmergencyRoute(
            @PathVariable Long id) {

        emergencyRouteService.deleteEmergencyRoute(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/simulate")
    public ResponseEntity<EmergencySimulationResponse>
    simulateEmergencyRoute(
            @Valid @RequestBody EmergencySimulationRequest request) {

        EmergencySimulationResponse response =
                emergencySimulationService.simulate(request);

        return ResponseEntity.ok(response);
    }
}