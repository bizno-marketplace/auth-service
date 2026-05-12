package com.biznopay.authservice.domain.entity.event;

import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.InvalidEmailException;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.UUID;

@Tag("unit")
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
    public void shouldThrowsRequiredFieldExceptionIfEmailIsEmptyOrNull(String email) {
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        Assertions.assertThrows(RequiredFieldException.class, ()
                -> UserRegistered.of(userId, email, "firstName", tokenId));
    }

    @Test
    @DisplayName("Should throw InvalidEmailException if email is invalid")
    public void shouldThrowInvalidEmailExceptionIfEmailIsInvalid() {
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        Assertions.assertThrows(InvalidEmailException.class, ()
                -> UserRegistered.of(userId, "email", "firstName", tokenId));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if firstName is null or empty")
    public void shouldThrowRequiredFieldExceptionIfFirstNameIsNullOrEmpty(String firstName) {
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        Assertions.assertThrows(RequiredFieldException.class,
                () -> UserRegistered.of(userId, "test@email.com", firstName, tokenId));
    }

    @ParameterizedTest
    @ValueSource(strings = {"ad"})
    @DisplayName("Should throw InvalidStringFieldLengException if firstName has less than min characters allowed")
    public void shouldThrowInvalidStringFieldLengExceptionIfFirstNameIsNullOrEmpty(String firstName) {
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        Assertions.assertThrows(InvalidStringFieldLengException.class,
                () -> UserRegistered.of(userId, "test@email.com", firstName, tokenId));
    }

    @NullSource
    @ParameterizedTest
    @DisplayName("Should throw RequiredFieldException if ActivationTokenId is null")
    public void shouldThrowRequiredFieldExceptionIfFirstNameIsNull(ActivationTokenId activationTokenId) {
        UserId userId = new UserId(UUID.randomUUID());
        Assertions.assertThrows(RequiredFieldException.class,
                () -> UserRegistered.of(userId, "test@email.com", "John", activationTokenId));
    }

    @Test
    @DisplayName("Should return UserRegistered with correct values")
    public void shouldReturnUserRegisteredWithCorrectValues() {
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId tokenId = new ActivationTokenId(UUID.randomUUID());
        UserRegistered userRegistered = UserRegistered.of(userId, "test@email.com", "John", tokenId);

        Assertions.assertNotNull(userRegistered.getEventId());
        Assertions.assertEquals(userId, userRegistered.getUserId());
        Assertions.assertEquals("test@email.com", userRegistered.getEmail());
        Assertions.assertEquals("John", userRegistered.getFirstName());
        Assertions.assertEquals(tokenId, userRegistered.getActivationTokenId());
        Assertions.assertNotNull(userRegistered.getOccurredAt());
    }
}
