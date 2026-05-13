package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;

import java.util.Optional;
import java.util.UUID;

public interface ActivationTokenGateway {
    void save(ActivationToken token);

    Optional<ActivationToken> findById(UUID tokenId);

}
