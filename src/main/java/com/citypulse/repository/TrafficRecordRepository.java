package com.citypulse.repository;

import com.citypulse.model.TrafficRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrafficRecordRepository extends JpaRepository<TrafficRecord, Long> {

    List<TrafficRecord> findByRoadId(Long roadId);

    List<TrafficRecord> findByLocationId(Long locationId);

    List<TrafficRecord> findByTrafficLevelIgnoreCase(String trafficLevel);

    List<TrafficRecord> findByRecordedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<TrafficRecord> findFirstByRoadIdOrderByRecordedAtDesc(
            Long roadId
    );

    Optional<TrafficRecord> findFirstByLocationIdOrderByRecordedAtDesc(
            Long locationId
    );

    List<TrafficRecord> findAllByOrderByRecordedAtDesc();
}
