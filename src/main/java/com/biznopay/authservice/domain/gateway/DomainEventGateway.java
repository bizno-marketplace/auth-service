package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.event.UserAccountActivation;

public interface DomainEventGateway {
    void publish(UserAccountActivation event);
}
