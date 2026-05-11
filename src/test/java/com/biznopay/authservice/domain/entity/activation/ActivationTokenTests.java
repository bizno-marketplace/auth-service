package com.biznopay.authservice.domain.entity.activation;

import com.biznopay.authservice.domain.exception.InvalidEntityIdException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ActivationTokenTests {
    @Test
    @DisplayName("Should throw InvalidEntityIdException if id is invalid on build")
    public void shouldThrowInvalidEntityIdExceptionIfIdIsInvalidOnBuild() {
        Assertions.assertThrows(InvalidEntityIdException.class, () -> new ActivationTokenId(null));
    }

}
