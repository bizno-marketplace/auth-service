package com.biznopay.authservice.domain.entity.activation;

import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.InvalidEntityIdException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.temporal.ChronoUnit;

import static com.biznopay.authservice.testcases.ActivationTokenTestCases.VALID_USER_ID;

@Tag("unit")
public class ActivationTokenTests {
    @Test
    @DisplayName("Should throw InvalidEntityIdException if id is invalid on build")
    public void shouldThrowInvalidEntityIdExceptionIfIdIsInvalidOnBuild() {
        Assertions.assertThrows(InvalidEntityIdException.class, () -> new ActivationTokenId(null));
    }

    @ParameterizedTest(name = "{0}")
    @DisplayName("Should throw exception when data is invalid on generate")
    @MethodSource("com.biznopay.authservice.testcases.ActivationTokenTestCases#invalidDomainGenerateCases")
    public void shouldThrowRequiredFieldExceptionIfUserIdIsNullOnGenerate(String message, UserId userId, Class<? extends RuntimeException> expectedException, String expectedMessage) {
        org.assertj.core.api.Assertions.assertThatThrownBy(() -> ActivationToken.generate(userId))
                .isInstanceOf(expectedException)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Should generate ActivationToken with correct values ")
    public void shouldGenerateActivationTokenWithCorrectValues() {
        ActivationToken activationToken = ActivationToken.generate(VALID_USER_ID);

        Assertions.assertNotNull(activationToken.getId());
        Assertions.assertEquals(VALID_USER_ID, activationToken.getUserId());
        long minutes = ChronoUnit.MINUTES.between(activationToken.getCreatedAt(), activationToken.getExpiresAt());
        Assertions.assertEquals(ActivationToken.EXPIRATION_MINUTES, minutes);
        Assertions.assertFalse(activationToken.isExpired());
        Assertions.assertFalse(activationToken.isUsed());
        Assertions.assertTrue(activationToken.isValid());
        Assertions.assertNotNull(activationToken.getCreatedAt());
        Assertions.assertNotNull(activationToken.getExpiresAt());
    }


    @Test
    @DisplayName("Should mark token as used on markAsUsed")
    public void shouldMarkTokenAsUsedOnMarlAsUsed() {
        ActivationToken activationToken = ActivationToken.generate(VALID_USER_ID);
        activationToken.markAsUsed();

        Assertions.assertNotNull(activationToken.getId());
        Assertions.assertEquals(VALID_USER_ID, activationToken.getUserId());
        long minutes = ChronoUnit.MINUTES.between(activationToken.getCreatedAt(), activationToken.getExpiresAt());
        Assertions.assertEquals(ActivationToken.EXPIRATION_MINUTES, minutes);
        Assertions.assertFalse(activationToken.isExpired());
        Assertions.assertTrue(activationToken.isUsed());
        Assertions.assertFalse(activationToken.isValid());
        Assertions.assertNotNull(activationToken.getCreatedAt());
        Assertions.assertNotNull(activationToken.getExpiresAt());
    }

}
