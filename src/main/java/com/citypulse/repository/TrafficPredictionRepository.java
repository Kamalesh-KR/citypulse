package com.citypulse.repository;

import com.citypulse.model.TrafficPrediction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TrafficPredictionRepository extends JpaRepository<TrafficPrediction, Long> {

    List<TrafficPrediction> findByRoadId(Long roadId);

    List<TrafficPrediction> findByLocationId(Long locationId);

    List<TrafficPrediction> findByPredictionTimeBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    List<TrafficPrediction> findByPredictionHorizonMinutes(
            Integer predictionHorizonMinutes
    );
}