package com.biznopay.authservice.domain.vo;

import java.time.LocalDateTime;

public record UserAccountActivationPayload(
        String eventId,
        String userId,
        String email,
        String firstName,
        String activationTokenId,
        LocalDateTime occurredAt,
        String eventType,
        String sourceService
) {
}
