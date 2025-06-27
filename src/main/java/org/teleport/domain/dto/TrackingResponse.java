package org.teleport.domain.dto;

import java.time.OffsetDateTime;

public record TrackingResponse(
        String trackingNumber,
        OffsetDateTime generatedAt,
        String status
) {}