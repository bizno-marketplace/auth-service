package com.biznopay.authservice.infra.gateway;


import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ActivationTokenGatewayImplTests {
    @Mock
    private ActivationTokenJpaRepository activationTokenJpaRepository;

    @Test
    @DisplayName("should save activation token with correct values")
    public void shouldSaveActivationTokenWithCorrectValues() {
        UserId userId = new UserId(UUID.randomUUID());
        ActivationToken token = ActivationToken.generate(userId);
        ActivationTokenGateway activationTokenGateway = new ActivationTokenGatewayImpl(activationTokenJpaRepository);
        activationTokenGateway.save(token);
        Mockito.verify(activationTokenJpaRepository).save(Mockito.any(ActivationTokenJpaEntity.class));
    }
}
