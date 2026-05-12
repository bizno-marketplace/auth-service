package com.biznopay.authservice.domain.entity.activation;

import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.InvalidEntityIdException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class ActivationTokenTests {
    @Test
    @DisplayName("Should throw InvalidEntityIdException if id is invalid on build")
    public void shouldThrowInvalidEntityIdExceptionIfIdIsInvalidOnBuild() {
        Assertions.assertThrows(InvalidEntityIdException.class, () -> new ActivationTokenId(null));
    }

    @Test
    @DisplayName("Should throw RequiredFieldException if user id is null on generate")
    public void shouldThrowRequiredFieldExceptionIfUserIdIsNullOnGenerate() {
        Assertions.assertThrows(RequiredFieldException.class, () -> ActivationToken.generate(null));
    }

    @Test
    @DisplayName("Should generate ActivationToken with correct values ")
    public void shouldGenerateActivationTokenWithCorrectValues() {
        UserId userId = new UserId(UUID.randomUUID());
        ActivationToken activationToken = ActivationToken.generate(userId);

        Assertions.assertNotNull(activationToken.getId());
        Assertions.assertEquals(userId, activationToken.getUserId());
        long minutes = ChronoUnit.MINUTES.between(activationToken.getCreatedAt(), activationToken.getExpiresAt());
        Assertions.assertEquals(ActivationToken.EXPIRATION_MINUTES, minutes);
        Assertions.assertFalse(activationToken.isExpired());
        Assertions.assertFalse(activationToken.isUsed());
        Assertions.assertTrue(activationToken.isValid());
        Assertions.assertNotNull(activationToken.getCreatedAt());
        Assertions.assertNotNull(activationToken.getExpiresAt());
    }
}
