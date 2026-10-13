package com.citypulse.service;

import com.citypulse.model.CitizenReport;
import com.citypulse.repository.CitizenReportRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitizenReportService {

    private final CitizenReportRepository citizenReportRepository;

    public CitizenReportService(
            CitizenReportRepository citizenReportRepository) {

        this.citizenReportRepository = citizenReportRepository;
    }

    public List<CitizenReport> getAllReports() {
        return citizenReportRepository.findAll();
    }

    public Optional<CitizenReport> getReportById(Long id) {
        return citizenReportRepository.findById(id);
    }

    public List<CitizenReport> getReportsByUser(Long userId) {
        return citizenReportRepository.findByUserId(userId);
    }

    public List<CitizenReport> getReportsByRoad(Long roadId) {
        return citizenReportRepository.findByRoadId(roadId);
    }

    public List<CitizenReport> getReportsByLocation(Long locationId) {
        return citizenReportRepository.findByLocationId(locationId);
    }

    public List<CitizenReport> getReportsByType(String reportType) {
        return citizenReportRepository.findByReportTypeIgnoreCase(reportType);
    }

    public List<CitizenReport> getReportsByStatus(String status) {
        return citizenReportRepository.findByStatusIgnoreCase(status);
    }

    public CitizenReport createReport(CitizenReport report) {
        return citizenReportRepository.save(report);
    }

    public CitizenReport updateReport(
            Long id,
            CitizenReport updatedReport) {

        return citizenReportRepository.findById(id)
                .map(existingReport -> {

                    existingReport.setUser(updatedReport.getUser());
                    existingReport.setRoad(updatedReport.getRoad());
                    existingReport.setLocation(updatedReport.getLocation());
                    existingReport.setReportType(updatedReport.getReportType());
                    existingReport.setDescription(updatedReport.getDescription());
                    existingReport.setLatitude(updatedReport.getLatitude());
                    existingReport.setLongitude(updatedReport.getLongitude());
                    existingReport.setStatus(updatedReport.getStatus());

                    /*
                     * Preserve the original reportedAt timestamp when
                     * the update request does not provide one.
                     */
                    if (updatedReport.getReportedAt() != null) {
                        existingReport.setReportedAt(
                                updatedReport.getReportedAt());
                    }

                    /*
                     * Only replace reviewedAt when the update request
                     * explicitly provides a value.
                     */
                    if (updatedReport.getReviewedAt() != null) {
                        existingReport.setReviewedAt(
                                updatedReport.getReviewedAt());
                    }

                    return citizenReportRepository.save(existingReport);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Citizen report not found with id: " + id));
    }

    public void deleteReport(Long id) {
        if (!citizenReportRepository.existsById(id)) {
            throw new RuntimeException(
                    "Citizen report not found with id: " + id);
        }

        citizenReportRepository.deleteById(id);
    }
}