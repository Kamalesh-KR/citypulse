package com.citypulse.repository;

import com.citypulse.model.EmergencyRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmergencyRouteRepository extends JpaRepository<EmergencyRoute, Long> {

    List<EmergencyRoute> findByEmergencyTypeIgnoreCase(String emergencyType);

    List<EmergencyRoute> findByStartLocationId(Long startLocationId);

    List<EmergencyRoute> findByDestinationLocationId(Long destinationLocationId);

    List<EmergencyRoute> findByPriorityStatusIgnoreCase(String priorityStatus);

    List<EmergencyRoute> findBySimulationStatusIgnoreCase(String simulationStatus);
}