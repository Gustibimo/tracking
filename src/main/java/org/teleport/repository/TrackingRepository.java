package org.teleport.repository;

import org.teleport.domain.Tracking;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TrackingRepository extends ReactiveCrudRepository<Tracking, Long> {

    @Query("SELECT EXISTS(SELECT 1 FROM tracking_numbers WHERE tracking_number = :trackingNumber)")
    Mono<Boolean> existsByTrackingNumber(String trackingNumber);
}