package com.biznopay.authservice.domain.entity.event;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class UserRegisteredTests {
    @Test
    @DisplayName("Should throw RequiredFieldException if userId is null")
    public void shouldThrowRequiredFieldExceptionIfUserIdIsNull() {
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        Assertions.assertThrows(RequiredFieldException.class, ()
                -> UserRegistered.of(null, "email", "firstName", tokenId));
    }
}
