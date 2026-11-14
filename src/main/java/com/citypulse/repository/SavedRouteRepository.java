package com.citypulse.repository;

import com.citypulse.model.SavedRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedRouteRepository
        extends JpaRepository<SavedRoute, Long> {

    /** All routes saved by a specific user, newest first. */
    List<SavedRoute> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** A specific saved route, but only if it belongs to the user. */
    Optional<SavedRoute> findByIdAndUserId(Long id, Long userId);

    /** Check if a route already exists for this user (exact match). */
    boolean existsByUserIdAndFromLocationIdAndToLocationId(
            Long userId,
            Long fromLocationId,
            Long toLocationId
    );

    /** Delete all of a user's saved routes. */
    void deleteByUserId(Long userId);
}