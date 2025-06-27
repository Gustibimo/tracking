package org.teleport.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class TrackingNumberGenerator {

    private static final String CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final SecureRandom random = new SecureRandom();

    public String generateTrackingNumber(String originCountryId,
                                         String destinationCountryId,
                                         Double weight,
                                         OffsetDateTime createdAt,
                                         UUID customerId,
                                         String customerName,
                                         String customerSlug) {

        StringBuilder trackingNumber = new StringBuilder();

        // Add origin country prefix (2 chars)
        trackingNumber.append(originCountryId);

        // Add destination country code (2 chars)
        trackingNumber.append(destinationCountryId);

        // Add weight-based component (2 chars)
        int weightCode = (int) (weight * 100) % 1296; // 36^2 = 1296
        trackingNumber.append(encodeBase36(weightCode, 2));

        // Add time-based component (4 chars)
        long timeCode = createdAt.toEpochSecond() % 1679616; // 36^4 = 1679616
        trackingNumber.append(encodeBase36(timeCode, 4));

        // Add customer-based component (2 chars)
        int customerCode = Math.abs(customerId.hashCode()) % 1296;
        trackingNumber.append(encodeBase36(customerCode, 2));

        // Add random component (4 chars) for uniqueness
        for (int i = 0; i < 4; i++) {
            trackingNumber.append(CHARS.charAt(random.nextInt(CHARS.length())));
        }

        return trackingNumber.toString();
    }

    private String encodeBase36(long value, int length) {
        String encoded = Long.toString(value, 36).toUpperCase();
        return String.format("%" + length + "s", encoded).replace(' ', '0');
    }
}