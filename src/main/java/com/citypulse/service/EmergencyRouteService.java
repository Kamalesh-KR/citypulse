package com.citypulse.service;

import com.citypulse.model.EmergencyRoute;
import com.citypulse.repository.EmergencyRouteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EmergencyRouteService {

    private final EmergencyRouteRepository emergencyRouteRepository;

    public EmergencyRouteService(
            EmergencyRouteRepository emergencyRouteRepository) {

        this.emergencyRouteRepository = emergencyRouteRepository;
    }

    public List<EmergencyRoute> getAllEmergencyRoutes() {
        return emergencyRouteRepository.findAll();
    }

    public Optional<EmergencyRoute> getEmergencyRouteById(Long id) {
        return emergencyRouteRepository.findById(id);
    }

    public List<EmergencyRoute> getRoutesByEmergencyType(
            String emergencyType) {

        return emergencyRouteRepository
                .findByEmergencyTypeIgnoreCase(emergencyType);
    }

    public List<EmergencyRoute> getRoutesByStartLocation(
            Long startLocationId) {

        return emergencyRouteRepository
                .findByStartLocationId(startLocationId);
    }

    public List<EmergencyRoute> getRoutesByDestinationLocation(
            Long destinationLocationId) {

        return emergencyRouteRepository
                .findByDestinationLocationId(destinationLocationId);
    }

    public List<EmergencyRoute> getRoutesByPriorityStatus(
            String priorityStatus) {

        return emergencyRouteRepository
                .findByPriorityStatusIgnoreCase(priorityStatus);
    }

    public List<EmergencyRoute> getRoutesBySimulationStatus(
            String simulationStatus) {

        return emergencyRouteRepository
                .findBySimulationStatusIgnoreCase(simulationStatus);
    }

    public EmergencyRoute createEmergencyRoute(
            EmergencyRoute emergencyRoute) {

        return emergencyRouteRepository.save(emergencyRoute);
    }

    public EmergencyRoute updateEmergencyRoute(
            Long id,
            EmergencyRoute updatedRoute) {

        return emergencyRouteRepository.findById(id)
                .map(existingRoute -> {
                    existingRoute.setRouteName(updatedRoute.getRouteName());
                    existingRoute.setEmergencyType(
                            updatedRoute.getEmergencyType());
                    existingRoute.setStartLocation(
                            updatedRoute.getStartLocation());
                    existingRoute.setDestinationLocation(
                            updatedRoute.getDestinationLocation());
                    existingRoute.setEstimatedDistanceKm(
                            updatedRoute.getEstimatedDistanceKm());
                    existingRoute.setEstimatedTimeMinutes(
                            updatedRoute.getEstimatedTimeMinutes());
                    existingRoute.setTrafficLevel(
                            updatedRoute.getTrafficLevel());
                    existingRoute.setPriorityStatus(
                            updatedRoute.getPriorityStatus());
                    existingRoute.setSimulationStatus(
                            updatedRoute.getSimulationStatus());

                    return emergencyRouteRepository.save(existingRoute);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Emergency route not found with id: " + id));
    }

    public void deleteEmergencyRoute(Long id) {
        if (!emergencyRouteRepository.existsById(id)) {
            throw new RuntimeException(
                    "Emergency route not found with id: " + id);
        }

        emergencyRouteRepository.deleteById(id);
    }
}