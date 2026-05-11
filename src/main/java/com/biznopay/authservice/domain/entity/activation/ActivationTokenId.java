package com.biznopay.authservice.domain.entity.activation;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.InvalidEntityIdException;

import java.util.UUID;

public record ActivationTokenId (UUID value) {
    public ActivationTokenId {
        if (value == null) {
            throw new InvalidEntityIdException(User.class.getName(), "USER-001");
        }
    }

    public static ActivationTokenId of(UUID value) {
        return new ActivationTokenId(value);
    }

    public static ActivationTokenId generate() {
        return new ActivationTokenId(UUID.randomUUID());
    }
}
