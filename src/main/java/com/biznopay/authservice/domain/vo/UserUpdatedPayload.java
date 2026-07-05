package com.biznopay.authservice.domain.vo;

public record UserUpdatedPayload(
        String eventId,
        String userId,
        String email,
        String firstName,
        String activationTokenId,
        String occurredAt
) {
}
