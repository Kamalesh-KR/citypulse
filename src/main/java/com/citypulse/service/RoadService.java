package com.citypulse.service;

import com.citypulse.model.Road;
import com.citypulse.repository.RoadRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoadService {

    private final RoadRepository roadRepository;

    public RoadService(RoadRepository roadRepository) {
        this.roadRepository = roadRepository;
    }

    public List<Road> getAllRoads() {
        return roadRepository.findAll();
    }

    public Optional<Road> getRoadById(Long id) {
        return roadRepository.findById(id);
    }

    public Optional<Road> getRoadByCode(String roadCode) {
        return roadRepository.findByRoadCode(roadCode);
    }

    public Road createRoad(Road road) {
        return roadRepository.save(road);
    }

    public Road updateRoad(Long id, Road updatedRoad) {
        return roadRepository.findById(id)
                .map(existingRoad -> {
                    existingRoad.setName(updatedRoad.getName());
                    existingRoad.setRoadCode(updatedRoad.getRoadCode());
                    existingRoad.setStartLocation(updatedRoad.getStartLocation());
                    existingRoad.setEndLocation(updatedRoad.getEndLocation());
                    existingRoad.setLengthKm(updatedRoad.getLengthKm());
                    existingRoad.setLanes(updatedRoad.getLanes());
                    existingRoad.setSpeedLimit(updatedRoad.getSpeedLimit());
                    existingRoad.setActive(updatedRoad.getActive());

                    return roadRepository.save(existingRoad);
                })
                .orElseThrow(() ->
                        new RuntimeException("Road not found with id: " + id));
    }

    public void deleteRoad(Long id) {
        if (!roadRepository.existsById(id)) {
            throw new RuntimeException("Road not found with id: " + id);
        }

        roadRepository.deleteById(id);
    }

    public boolean roadCodeExists(String roadCode) {
        return roadRepository.existsByRoadCode(roadCode);
    }
}