package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.infra.mapper.ActivationTokenMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import org.junit.jupiter.params.provider.Arguments;

import java.util.UUID;
import java.util.stream.Stream;

public class ActivationTokenTestCases {
    public static final UserId VALID_USER_ID = UserId.of(UUID.randomUUID());

    public static final ActivationToken VALID_ACTIVATION_TOKEN = ActivationToken.generate(VALID_USER_ID);
    public static final ActivationTokenJpaEntity VALID_ACTIVATION_TOKEN_JPA = ActivationTokenMapper.toJpaEntity(VALID_ACTIVATION_TOKEN);
    public static final ActivationTokenJpaEntity activationTokenJpa(UUID userId){
        return ActivationTokenMapper.toJpaEntity(ActivationToken.generate(UserId.of(userId)));
    }

    public static Stream<Arguments> generateTokenDomainCases() {
        return Stream.of(
                Arguments.of("User id is null", null, RequiredFieldException.class, "UserId is required"),
                Arguments.of("Success", VALID_USER_ID, null, null)
        );
    }
}
