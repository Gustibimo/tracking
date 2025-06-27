package org.teleport.controller;

import org.teleport.domain.*;
import org.teleport.domain.dto.TrackingRequest;
import org.teleport.domain.dto.TrackingResponse;
import org.teleport.service.TrackingNumberService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
public class TrackingController {

    private final TrackingNumberService trackingService;

    public TrackingController(TrackingNumberService trackingService) {
        this.trackingService = trackingService;
    }

    @GetMapping("/next-tracking-number")
    public Mono<ResponseEntity<TrackingResponse>> getNextTrackingNumber(
            @Valid TrackingRequest request) {

        return trackingService.generateTrackingNumber(request)
                .map(ResponseEntity::ok)
                .onErrorReturn(ResponseEntity.internalServerError().build());
    }

    @GetMapping("/health")
    public Mono<ResponseEntity<String>> health() {
        return Mono.just(ResponseEntity.ok("OK"));
    }
}