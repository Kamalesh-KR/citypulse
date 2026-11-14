package com.citypulse.repository;

import com.citypulse.model.District;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DistrictRepository extends JpaRepository<District, Long> {

    List<District> findByActiveTrueOrderByNameAsc();

    List<District> findByStateIdAndActiveTrueOrderByNameAsc(Long stateId);

    Optional<District> findByStateIdAndName(Long stateId, String name);

    long countByStateIdAndActiveTrue(Long stateId);
}