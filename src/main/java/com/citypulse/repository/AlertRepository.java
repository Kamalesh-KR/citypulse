package com.citypulse.repository;

import com.citypulse.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByRoadId(Long roadId);

    List<Alert> findByLocationId(Long locationId);

    List<Alert> findByAlertTypeIgnoreCase(String alertType);

    List<Alert> findBySeverityIgnoreCase(String severity);

    List<Alert> findByActiveTrue();
}