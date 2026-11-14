package com.citypulse.controller;

import com.citypulse.model.District;
import com.citypulse.repository.DistrictRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * ============================================================
 * CITYPULSE — DISTRICT ENDPOINTS (Phase 1: All India)
 * ============================================================
 *
 * GET  /api/districts                         — all districts
 * GET  /api/districts/{id}                    — one district
 * GET  /api/districts/search?q=...            — search by name
 */
@RestController
@RequestMapping("/api/districts")
public class DistrictController {

    private final DistrictRepository districtRepo;

    public DistrictController(DistrictRepository districtRepo) {
        this.districtRepo = districtRepo;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDistricts() {

        List<Map<String, Object>> result = new ArrayList<>();

        districtRepo.findByActiveTrueOrderByNameAsc().forEach(d -> {
            result.add(toMap(d));
        });

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDistrict(@PathVariable Long id) {

        return districtRepo.findById(id)
                .map(d -> ResponseEntity.ok(toMap(d)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> search(
            @RequestParam(name = "q", required = false, defaultValue = "") String query) {

        String q = query.trim().toLowerCase();
        List<Map<String, Object>> result = new ArrayList<>();

        if (q.isEmpty()) {
            return ResponseEntity.ok(result);
        }

        for (District d : districtRepo.findByActiveTrueOrderByNameAsc()) {
            if (d.getName() != null &&
                d.getName().toLowerCase().contains(q)) {
                result.add(toMap(d));
            }
            if (result.size() >= 50) break;
        }

        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toMap(District d) {

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", d.getId());
        map.put("name", d.getName());
        map.put("headquartersName", d.getHeadquartersName());
        map.put("latitude", d.getLatitude());
        map.put("longitude", d.getLongitude());
        map.put("locationId", d.getLocationId());

        if (d.getState() != null) {
            Map<String, Object> stateMap = new LinkedHashMap<>();
            stateMap.put("id", d.getState().getId());
            stateMap.put("name", d.getState().getName());
            stateMap.put("code", d.getState().getCode());
            map.put("state", stateMap);
        }

        return map;
    }
}