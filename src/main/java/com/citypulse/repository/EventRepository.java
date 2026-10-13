package com.citypulse.repository;

import com.citypulse.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findByLocationId(Long locationId);

    List<Event> findByEventTypeIgnoreCase(String eventType);

    List<Event> findByImpactLevelIgnoreCase(String impactLevel);

    List<Event> findByActiveTrue();

    List<Event> findByStartTimeBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}