package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import org.springframework.stereotype.Component;

@Component
public class ActivationTokenGatewayImpl implements ActivationTokenGateway {
    @Override
    public void save(ActivationToken token) {

    }
}
