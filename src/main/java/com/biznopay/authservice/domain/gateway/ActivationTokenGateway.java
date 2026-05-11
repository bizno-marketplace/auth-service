package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;

public interface ActivationTokenGateway {
    void save(ActivationToken token);
}
