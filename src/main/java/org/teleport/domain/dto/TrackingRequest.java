package org.teleport.domain.dto;

import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;
import java.util.UUID;

public record TrackingRequest(
        @NotBlank @Size(min = 2, max = 2) String originCountryId,
        @NotBlank @Size(min = 2, max = 2) String destinationCountryId,
        @NotNull @DecimalMin("0.001") @DecimalMax("999.999") Double weight,
        @NotNull OffsetDateTime createdAt,
        @NotNull UUID customerId,
        @NotBlank String customerName,
        @NotBlank String customerSlug
) {}