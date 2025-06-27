package org.teleport.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.OffsetDateTime;

public record TrackingResponse(
        @JsonProperty("tracking_number") String trackingNumber,
        @JsonProperty("created_at") OffsetDateTime createdAt,
        @JsonProperty("status") String status
) {}