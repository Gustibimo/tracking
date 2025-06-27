package org.teleport.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.teleport.domain.Tracking;
import org.teleport.domain.dto.TrackingRequest;
import org.teleport.domain.dto.TrackingResponse;
import org.teleport.repository.TrackingRepository;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.security.SecureRandom;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class TrackingNumberService {

    private final TrackingNumberGenerator generator;
    private final TrackingRepository repository;
    private final ReactiveRedisTemplate<String, String> redisTemplate;

    public TrackingNumberService(TrackingNumberGenerator generator,
                                 TrackingRepository repository,
                                 ReactiveRedisTemplate<String, String> redisTemplate) {
        this.generator = generator;
        this.repository = repository;
        this.redisTemplate = redisTemplate;
    }

    public Mono<TrackingResponse> generateTrackingNumber(TrackingRequest request) {
        return generateUniqueTrackingNumber(request)
                .flatMap(trackingNumber -> saveTrackingRecord(request, trackingNumber)
                        .map(record -> new TrackingResponse(
                                trackingNumber,
                                record.generatedAt(),
                                "GENERATED"
                        )))
                .retryWhen(Retry.backoff(3, Duration.ofMillis(100)));
    }

    private Mono<String> generateUniqueTrackingNumber(TrackingRequest request) {
        return Mono.defer(() -> Mono.fromCallable(() -> generator.generateTrackingNumber(
                        request.originCountryId(),
                        request.destinationCountryId(),
                        request.weight(),
                        request.createdAt(),
                        request.customerId(),
                        request.customerName(),
                        request.customerSlug()
                ))
                .flatMap(this::ensureUniqueness)
        )
        .retryWhen(Retry.backoff(1000, Duration.ofMillis(100)))
        .onErrorResume(e -> Mono.error(new RuntimeException("Failed to generate unique tracking number after maximum attempts")));
    }

    private Mono<String> ensureUniqueness(String trackingNumber) {
        String redisKey = "tracking:" + trackingNumber;

        return redisTemplate.opsForValue()
                .setIfAbsent(redisKey, "1", Duration.ofMinutes(5))
                .flatMap(success -> {
                    if (success) {
                        return repository.existsByTrackingNumber(trackingNumber)
                                .flatMap(exists -> {
                                    if (exists) {
                                        return redisTemplate.delete(redisKey).then(Mono.empty());
                                    }
                                    return Mono.just(trackingNumber);
                                });
                    }
                    return Mono.empty();
                });
    }

    private Mono<Tracking> saveTrackingRecord(TrackingRequest request, String trackingNumber) {
        Tracking record = new Tracking(
                null,
                trackingNumber,
                request.originCountryId(),
                request.destinationCountryId(),
                request.weight(),
                request.createdAt(),
                request.customerId(),
                request.customerName(),
                request.customerSlug(),
                OffsetDateTime.now()
        );

        return repository.save(record);
    }
}