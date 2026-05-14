package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.infra.mapper.ActivationTokenMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ActivationTokenGatewayImpl implements ActivationTokenGateway {
    private final ActivationTokenJpaRepository activationTokenJpaRepository;

    @Override
    public void save(ActivationToken token) {
        ActivationTokenJpaEntity jpaEntity = ActivationTokenMapper.toJpaEntity(token);
        activationTokenJpaRepository.save(jpaEntity);
    }

    @Override
    public void delete(ActivationToken token) {
        ActivationTokenJpaEntity jpaEntity = ActivationTokenMapper.toJpaEntity(token);
        activationTokenJpaRepository.delete(jpaEntity);
    }

    @Override
    public Optional<ActivationToken> findById(UUID tokenId) {
        Optional<ActivationTokenJpaEntity> jpaEntity = activationTokenJpaRepository.findById(tokenId);
        return jpaEntity.map(ActivationTokenMapper::toDomainEntity);
    }

    @Override
    public Optional<ActivationToken> findActiveByUserId(UUID userId) {
        Optional<ActivationTokenJpaEntity> entity = activationTokenJpaRepository.findByUsedAndUserId(false, userId);
        return entity.map(ActivationTokenMapper::toDomainEntity);
    }
}
