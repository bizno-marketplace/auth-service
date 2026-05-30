package com.biznopay.authservice.domain.entity.event;

import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("unit")
public class UserRegisteredTests {

    @ParameterizedTest(name = "{0}")
    @DisplayName("Should throw exception if invalid data is passed")
    @MethodSource("com.biznopay.authservice.testcases.UserRegisteredTestCases#invalidDomainRegisteredCases")
    public void shouldThrowExceptionIfInvalidDataIsPassed(String testName,
                                                          UserId userId, String email,
                                                          String firstName, ActivationTokenId activationTokenId,
                                                          Class<? extends Exception> expectedException, String expectedMessage) {
        if (testName.equals("Success")) {
            UserRegistered userRegistered = UserRegistered.of(userId, email,
                    firstName, activationTokenId);
            Assertions.assertNotNull(userRegistered.getEventId());
            Assertions.assertEquals(userId, userRegistered.getUserId());
            Assertions.assertEquals(email, userRegistered.getEmail());
            Assertions.assertEquals(firstName, userRegistered.getFirstName());
            Assertions.assertEquals(activationTokenId, userRegistered.getActivationTokenId());
            Assertions.assertNotNull(userRegistered.getOccurredAt());
        } else {
            org.assertj.core.api.Assertions.assertThatThrownBy(() ->
                            UserRegistered.of(userId, email, firstName, activationTokenId))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedMessage);
        }
    }
}
