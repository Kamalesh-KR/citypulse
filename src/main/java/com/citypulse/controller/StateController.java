package com.citypulse.controller;

import com.citypulse.repository.DistrictRepository;
import com.citypulse.repository.StateRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ============================================================
 * CITYPULSE — STATE ENDPOINTS (Phase 1: All India)
 * ============================================================
 *
 * GET  /api/states                      — all states
 * GET  /api/states/{id}                 — one state
 * GET  /api/states/{id}/districts       — districts within a state
 */
@RestController
@RequestMapping("/api/states")
public class StateController {

    private final StateRepository stateRepo;
    private final DistrictRepository districtRepo;

    public StateController(StateRepository stateRepo,
                          DistrictRepository districtRepo) {
        this.stateRepo = stateRepo;
        this.districtRepo = districtRepo;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllStates() {

        List<Map<String, Object>> result = new ArrayList<>();

        stateRepo.findByActiveTrueOrderByNameAsc().forEach(state -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", state.getId());
            map.put("name", state.getName());
            map.put("code", state.getCode());
            map.put("type", state.getType());
            map.put("districtCount",
                    districtRepo.countByStateIdAndActiveTrue(state.getId()));
            result.add(map);
        });

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getState(@PathVariable Long id) {

        return stateRepo.findById(id)
                .map(state -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", state.getId());
                    map.put("name", state.getName());
                    map.put("code", state.getCode());
                    map.put("type", state.getType());
                    map.put("districtCount",
                            districtRepo.countByStateIdAndActiveTrue(state.getId()));
                    return ResponseEntity.ok(map);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/districts")
    public ResponseEntity<List<Map<String, Object>>> getDistrictsInState(
            @PathVariable Long id) {

        List<Map<String, Object>> result = new ArrayList<>();

        districtRepo.findByStateIdAndActiveTrueOrderByNameAsc(id).forEach(d -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", d.getId());
            map.put("name", d.getName());
            map.put("headquartersName", d.getHeadquartersName());
            map.put("latitude", d.getLatitude());
            map.put("longitude", d.getLongitude());
            map.put("locationId", d.getLocationId());
            result.add(map);
        });

        return ResponseEntity.ok(result);
    }
}