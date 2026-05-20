package com.biznopay.authservice.domain.entity.event;

import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.biznopay.authservice.testcases.UserRegisteredTestCases.*;

@Tag("unit")
public class UserRegisteredTests {

    @ParameterizedTest(name = "{0}")
    @DisplayName("Should throw exception if invalid data is passed")
    @MethodSource("com.biznopay.authservice.testcases.UserRegisteredTestCases#invalidDomainRegisteredCases")
    public void shouldThrowExceptionIfInvalidDataIsPassed(String testName,
                                                          UserId userId, String email,
                                                          String firstName, ActivationTokenId activationTokenId,
                                                          Class<? extends Exception> expectedException, String expectedMessage) {
        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                        UserRegistered.of(userId, email, firstName, activationTokenId))
                .isInstanceOf(expectedException)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Should return UserRegistered with correct values")
    public void shouldReturnUserRegisteredWithCorrectValues() {
        UserRegistered userRegistered = UserRegistered.of(VALID_USER_ID, VALID_USER_EMAIL,
                VALID_USER_FIRST_NAME, VALID_ACTIVATION_TOKEN_ID);

        Assertions.assertNotNull(userRegistered.getEventId());
        Assertions.assertEquals(VALID_USER_ID, userRegistered.getUserId());
        Assertions.assertEquals(VALID_USER_EMAIL, userRegistered.getEmail());
        Assertions.assertEquals(VALID_USER_FIRST_NAME, userRegistered.getFirstName());
        Assertions.assertEquals(VALID_ACTIVATION_TOKEN_ID, userRegistered.getActivationTokenId());
        Assertions.assertNotNull(userRegistered.getOccurredAt());
    }
}
