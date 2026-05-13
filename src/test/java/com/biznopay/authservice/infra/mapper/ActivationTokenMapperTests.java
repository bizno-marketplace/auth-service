package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

public class ActivationTokenMapperTests {
    @Test
    @DisplayName("Should return ActivationTokenJpaEntity on toJpaEntity")
    public void shouldReturnActivationTokenJpaEntityOnToJpaEntity() {
        ActivationTokenMapper mapper = new ActivationTokenMapper();
        UserId userId = new UserId(UUID.randomUUID());
        ActivationToken activationToken = ActivationToken.generate(userId);
        ActivationTokenJpaEntity entity = ActivationTokenMapper.toJpaEntity(activationToken);
        Assertions.assertEquals(entity.getId(), activationToken.getId().value());
        Assertions.assertEquals(entity.getUserId(), activationToken.getUserId().value());
        Assertions.assertEquals(entity.isUsed(), activationToken.isUsed());
        Assertions.assertEquals(entity.getExpiresAt(), activationToken.getExpiresAt());
        Assertions.assertEquals(entity.getCreatedAt(), activationToken.getCreatedAt());
    }
}
