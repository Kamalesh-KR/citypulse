package com.citypulse.repository;

import com.citypulse.model.PushSubscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PushSubscriptionRepository extends JpaRepository<PushSubscription, Long> {

    Optional<PushSubscription> findByEndpoint(String endpoint);

    List<PushSubscription> findByActiveTrue();

    List<PushSubscription> findByUserIdAndActiveTrue(Long userId);

    long countByActiveTrue();
}