package com.biznopay.authservice.infra.gateway;


import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.infra.mapper.ActivationTokenMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

@Tag("unit")
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

    @Test
    @DisplayName("Should delete activation token if exists")
    public void shouldDeleteActivationTokenIfExists() {
        ActivationToken token = ActivationToken.generate(new UserId(UUID.randomUUID()));
        ActivationTokenGateway activationTokenGateway = new ActivationTokenGatewayImpl(activationTokenJpaRepository);
        activationTokenGateway.delete(token);
        Mockito.verify(activationTokenJpaRepository).delete(Mockito.any(ActivationTokenJpaEntity.class));
    }

    @Test
    @DisplayName("Should return optional empty if activation token is not found on find by id")
    public void shouldReturnOptionalEmptyIfActivationTokenIdIsNotFoundOnFindById() {
        UUID tokenId = UUID.randomUUID();
        Mockito.when(activationTokenJpaRepository.findById(tokenId)).thenReturn(Optional.empty());
        ActivationTokenGateway activationTokenGateway = new ActivationTokenGatewayImpl(activationTokenJpaRepository);
        Optional<ActivationToken> token = activationTokenGateway.findById(tokenId);
        Assertions.assertTrue(token.isEmpty());
    }

    @Test
    @DisplayName("Should return activation token if found on find by id")
    public void shouldReturnActivationTokenIfFoundOnFindById() {
        UUID tokenId = UUID.randomUUID();
        UserId userId = UserId.of(UUID.randomUUID());
        ActivationToken activationToken = ActivationToken.generate(userId);
        ActivationTokenJpaEntity entity = ActivationTokenMapper.toJpaEntity(activationToken);
        Mockito.when(activationTokenJpaRepository.findById(tokenId)).thenReturn(Optional.of(entity));
        ActivationTokenGateway activationTokenGateway = new ActivationTokenGatewayImpl(activationTokenJpaRepository);
        Optional<ActivationToken> token = activationTokenGateway.findById(tokenId);
        Assertions.assertTrue(token.isPresent());
        Assertions.assertEquals(activationToken.getId(), token.get().getId());
        Assertions.assertEquals(activationToken.getUserId(), token.get().getUserId());
        Assertions.assertEquals(activationToken.getExpiresAt(), token.get().getExpiresAt());
        Assertions.assertEquals(activationToken.getCreatedAt(), token.get().getCreatedAt());
        Assertions.assertEquals(activationToken.isUsed(), token.get().isUsed());

    }

    @Test
    @DisplayName("Should return optional empty if activation token is not found on find active by  user id")
    public void shouldReturnOptionalEmptyIfActivationTokenIdIsNotFoundOnFindActiveByUserId() {
        UUID userId = UUID.randomUUID();
        Mockito.when(activationTokenJpaRepository.findByUsedAndUserId(false, userId)).thenReturn(Optional.empty());
        ActivationTokenGateway activationTokenGateway = new ActivationTokenGatewayImpl(activationTokenJpaRepository);
        Optional<ActivationToken> token = activationTokenGateway.findActiveByUserId(userId);
        Assertions.assertTrue(token.isEmpty());
    }
}
