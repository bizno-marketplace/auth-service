package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import org.springframework.stereotype.Component;

@Component
public class DomainEventGatewayImpl implements DomainEventGateway {
    @Override
    public void publish(UserRegistered event) {

    }
}
