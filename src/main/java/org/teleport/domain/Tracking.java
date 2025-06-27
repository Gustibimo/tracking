package org.teleport.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Table("tracking_numbers")
public record Tracking(
        @Id Long id,
        String trackingNumber,
        String originCountryId,
        String destinationCountryId,
        Double weight,
        OffsetDateTime orderCreatedAt,
        UUID customerId,
        String customerName,
        String customerSlug,
        OffsetDateTime generatedAt
) {}
