package com.citypulse.repository;

import com.citypulse.model.CitizenReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitizenReportRepository extends JpaRepository<CitizenReport, Long> {

    List<CitizenReport> findByUserId(Long userId);

    List<CitizenReport> findByRoadId(Long roadId);

    List<CitizenReport> findByLocationId(Long locationId);

    List<CitizenReport> findByReportTypeIgnoreCase(String reportType);

    List<CitizenReport> findByStatusIgnoreCase(String status);
}