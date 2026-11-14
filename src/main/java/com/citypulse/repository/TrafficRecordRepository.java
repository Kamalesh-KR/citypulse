package com.citypulse.repository;

import com.citypulse.model.TrafficRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TrafficRecordRepository
        extends JpaRepository<TrafficRecord, Long> {

    /* =========================================================
       Existing methods (kept)
       ========================================================= */

    Optional<TrafficRecord> findFirstByRoadIdOrderByRecordedAtDesc(
            Long roadId);

    Optional<TrafficRecord> findFirstByLocationIdOrderByRecordedAtDesc(
            Long locationId);

    @Query("SELECT DISTINCT t.road.id FROM TrafficRecord t WHERE t.road IS NOT NULL")
    List<Long> findDistinctRoadIdsWithTraffic();

    /* =========================================================
       Restored methods (were in original file, deleted by
       the earlier replacement)
       ========================================================= */

    List<TrafficRecord> findAllByOrderByRecordedAtDesc();

    List<TrafficRecord> findByRoadId(Long roadId);

    List<TrafficRecord> findByLocationId(Long locationId);

    List<TrafficRecord> findByTrafficLevelIgnoreCase(String trafficLevel);

    List<TrafficRecord> findByRecordedAtBetween(
            LocalDateTime start,
            LocalDateTime end);
}