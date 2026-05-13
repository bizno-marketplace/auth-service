package com.biznopay.authservice.infra.mapper;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.infra.persistence.jpa.entity.ActivationTokenJpaEntity;

public class ActivationTokenMapper {
    public static ActivationTokenJpaEntity toJpaEntity(ActivationToken token) {
        return new ActivationTokenJpaEntity(token.getId().value(), token.getUserId().value(), token.isUsed(),
                token.getExpiresAt(), token.getCreatedAt());
    }

    public static ActivationToken toDomainEntity(ActivationTokenJpaEntity jpaEntity) {
        ActivationTokenId activationTokenId = ActivationTokenId.of(jpaEntity.getId());
        return ActivationToken.reconstitute(activationTokenId, UserId.of(jpaEntity.getUserId()), jpaEntity.isUsed(),
                jpaEntity.getExpiresAt(), jpaEntity.getCreatedAt());
    }
}
