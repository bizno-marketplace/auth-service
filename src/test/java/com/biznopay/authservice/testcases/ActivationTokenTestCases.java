package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.params.provider.Arguments;

import java.util.UUID;
import java.util.stream.Stream;

public class ActivationTokenTestCases {
    public static final UserId VALID_USER_ID = UserId.of(UUID.randomUUID());

    public static Stream<Arguments> invalidDomainGenerateCases() {
        return Stream.of(
                Arguments.of("User id is null", null, RequiredFieldException.class, "UserId is required")
        );
    }
}
