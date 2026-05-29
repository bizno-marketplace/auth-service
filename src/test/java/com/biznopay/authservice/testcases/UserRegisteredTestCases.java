package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.InvalidEmailException;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class UserRegisteredTestCases {
    public static final UserId VALID_USER_ID = UserId.generate();
    public static final String VALID_USER_EMAIL = "test@example.com";
    public static final String VALID_USER_FIRST_NAME = "John";
    public static final ActivationTokenId VALID_ACTIVATION_TOKEN_ID = ActivationTokenId.generate();


    public static Stream<Arguments> invalidDomainRegisteredCases() {
        return Stream.of(
                Arguments.of("UserId is null", null, VALID_USER_EMAIL, VALID_USER_FIRST_NAME, VALID_ACTIVATION_TOKEN_ID, RequiredFieldException.class, "UserId is required"),
                Arguments.of("E-mail is null", VALID_USER_ID, null, VALID_USER_FIRST_NAME, VALID_ACTIVATION_TOKEN_ID, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("E-mail is empty", VALID_USER_ID, "", VALID_USER_FIRST_NAME, VALID_ACTIVATION_TOKEN_ID, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("E-mail is invalid", VALID_USER_ID, "invalid-email", VALID_USER_FIRST_NAME, VALID_ACTIVATION_TOKEN_ID, InvalidEmailException.class, "Invalid E-mail"),
                Arguments.of("FirstName is null", VALID_USER_ID, VALID_USER_EMAIL, null, VALID_ACTIVATION_TOKEN_ID, RequiredFieldException.class, "FirstName is required"),
                Arguments.of("FirstName is empty", VALID_USER_ID, VALID_USER_EMAIL, "", VALID_ACTIVATION_TOKEN_ID, RequiredFieldException.class, "FirstName is required"),
                Arguments.of("FirstName is too short", VALID_USER_ID, VALID_USER_EMAIL, "Jo", VALID_ACTIVATION_TOKEN_ID, InvalidStringFieldLengException.class, "FirstName must be at least 3 characters long"),
                Arguments.of("ActivationTokenId is null", VALID_USER_ID, VALID_USER_EMAIL, VALID_USER_FIRST_NAME, null, RequiredFieldException.class, "ActivationTokenId is required")
        );
    }
}
