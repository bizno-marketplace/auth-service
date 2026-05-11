package com.biznopay.authservice.domain.entity.event;

import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.InvalidEmailException;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.UUID;

public class UserRegisteredTests {
    @Test
    @DisplayName("Should throw RequiredFieldException if userId is null")
    public void shouldThrowRequiredFieldExceptionIfUserIdIsNull() {
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        Assertions.assertThrows(RequiredFieldException.class, ()
                -> UserRegistered.of(null, "email", "firstName", tokenId));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if email is empty or null")
    public void shouldThrowsRequiredFieldExceptionIfEmailIsEmptyOrNull(String email){
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        Assertions.assertThrows(RequiredFieldException.class, ()
                -> UserRegistered.of(userId, email, "firstName", tokenId));
    }

    @Test
    @DisplayName("Should throw InvalidEmailException if email is invalid")
    public void shouldThrowInvalidEmailExceptionIfEmailIsInvalid(){
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        Assertions.assertThrows(InvalidEmailException.class, ()
                -> UserRegistered.of(userId, "email", "firstName", tokenId));
    }
}
