package com.citypulse.repository;

import com.citypulse.model.RouteAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface RouteAnalysisRepository extends JpaRepository<RouteAnalysis, Long> {

    List<RouteAnalysis> findByStartLocationId(Long startLocationId);

    List<RouteAnalysis> findByDestinationLocationId(Long destinationLocationId);

    List<RouteAnalysis> findByAnalysisTypeIgnoreCase(String analysisType);

    List<RouteAnalysis> findByAnalyzedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
