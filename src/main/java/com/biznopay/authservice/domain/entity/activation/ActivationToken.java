package com.biznopay.authservice.domain.entity.activation;

import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

import java.time.LocalDateTime;

public class ActivationToken {
    public static final int EXPIRATION_MINUTES = 15;

    private final ActivationTokenId id;
    private final UserId userId;
    private final boolean used;
    private final LocalDateTime expiresAt;
    private final LocalDateTime createdAt;

    private ActivationToken(ActivationTokenId id, UserId userId, boolean used, LocalDateTime expiresAt, LocalDateTime createdAt) {
        this.id = id;
        this.userId = this.validateUserId(userId);
        this.used = used;
        this.expiresAt = expiresAt;
        this.createdAt = createdAt;
    }

    public static ActivationToken generate(UserId userId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusMinutes(EXPIRATION_MINUTES);
        return new ActivationToken(ActivationTokenId.generate(), userId, false, expiresAt, now);
    }

    public static ActivationToken reconstitute(ActivationTokenId id, UserId userId, boolean used,
                                               LocalDateTime expiresAt, LocalDateTime createdAt) {
        return new ActivationToken(id, userId, used, expiresAt, createdAt);
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    public boolean isUsed() {
        return used;
    }

    public boolean isValid() {
        return !isExpired() && !isUsed();
    }

    private UserId validateUserId(UserId userId) {
        if (userId == null)
            throw new RequiredFieldException("UserId", ActivationToken.class.getName(), "ACTIVATION_TOKEN-002");
        return userId;
    }

    public ActivationTokenId getId() {
        return id;
    }

    public UserId getUserId() {
        return userId;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

