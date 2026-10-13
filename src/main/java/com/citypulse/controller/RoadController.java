package com.citypulse.controller;

import com.citypulse.model.Road;
import com.citypulse.service.RoadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/roads")
public class RoadController {

    private final RoadService roadService;

    public RoadController(RoadService roadService) {
        this.roadService = roadService;
    }

    @GetMapping
    public ResponseEntity<List<Road>> getAllRoads() {
        return ResponseEntity.ok(roadService.getAllRoads());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Road> getRoadById(@PathVariable Long id) {
        return roadService.getRoadById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/code")
    public ResponseEntity<Road> getRoadByCode(
            @RequestParam String roadCode) {

        return roadService.getRoadByCode(roadCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Road> createRoad(@RequestBody Road road) {
        return ResponseEntity.ok(roadService.createRoad(road));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Road> updateRoad(
            @PathVariable Long id,
            @RequestBody Road road) {

        return ResponseEntity.ok(roadService.updateRoad(id, road));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoad(@PathVariable Long id) {
        roadService.deleteRoad(id);
        return ResponseEntity.noContent().build();
    }
}