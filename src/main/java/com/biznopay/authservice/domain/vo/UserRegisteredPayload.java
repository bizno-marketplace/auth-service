package com.biznopay.authservice.domain.vo;

public record UserRegisteredPayload(
        String eventId,
        String userId,
        String email,
        String firstName,
        String activationTokenId,
        String occurredAt
) {
}
