package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.infra.mapper.ActivationTokenMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.ActivationTokenJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivationTokenGatewayImpl implements ActivationTokenGateway {
    private final ActivationTokenJpaRepository activationTokenJpaRepository;

    @Override
    public void save(ActivationToken token) {
        ActivationTokenJpaEntity jpaEntity = ActivationTokenMapper.toJpaEntity(token);
        activationTokenJpaRepository.save(jpaEntity);
    }
}
