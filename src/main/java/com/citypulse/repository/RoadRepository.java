package com.citypulse.repository;

import com.citypulse.model.Road;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoadRepository extends JpaRepository<Road, Long> {

    Optional<Road> findByRoadCode(String roadCode);

    boolean existsByRoadCode(String roadCode);
}