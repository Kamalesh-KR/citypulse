package com.citypulse.repository;

import com.citypulse.model.FavoriteRoad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRoadRepository
        extends JpaRepository<FavoriteRoad, Long> {

    /** All roads favorited by a specific user, newest first. */
    List<FavoriteRoad> findByUserIdOrderByCreatedAtDesc(Long userId);

    /** Check if a specific road is already favorited by this user. */
    Optional<FavoriteRoad> findByUserIdAndRoadId(
            Long userId,
            Long roadId
    );

    /** Check if the road is favorited (boolean convenience). */
    boolean existsByUserIdAndRoadId(Long userId, Long roadId);

    /** Remove a favorite. */
    void deleteByUserIdAndRoadId(Long userId, Long roadId);

    /** Delete all of a user's favorites. */
    void deleteByUserId(Long userId);
}