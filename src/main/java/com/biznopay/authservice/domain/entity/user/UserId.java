package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.exception.InvalidEntityIdException;

import java.util.UUID;

public record UserId(UUID value) {
    public UserId {
        if (value == null) {
            throw new InvalidEntityIdException(User.class.getName(), "USER-001");
        }
    }

    public static UserId of(UUID value){
        return new UserId(value);
    }

    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }
}
