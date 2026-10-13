package com.citypulse.repository;

import com.citypulse.model.WeatherRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface WeatherRecordRepository extends JpaRepository<WeatherRecord, Long> {

    List<WeatherRecord> findByLocationId(Long locationId);

    List<WeatherRecord> findByWeatherConditionIgnoreCase(String weatherCondition);

    List<WeatherRecord> findByRecordedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
