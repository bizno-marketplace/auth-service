package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;

public class ActivationTokenMapper {
    public static ActivationTokenJpaEntity toJpaEntity(ActivationToken token) {
        return new ActivationTokenJpaEntity(token.getId().value(), token.getUserId().value(), token.isUsed(),
                token.getExpiresAt(), token.getCreatedAt());
    }
}
