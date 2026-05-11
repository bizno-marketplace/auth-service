package com.biznopay.authservice.domain.entity.activation;

import com.biznopay.authservice.domain.exception.InvalidEntityIdException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

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
}
