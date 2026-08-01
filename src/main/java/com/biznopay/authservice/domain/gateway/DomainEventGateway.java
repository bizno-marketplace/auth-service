package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.event.UserAccountActivationEvent;

public interface DomainEventGateway {
    void publish(UserAccountActivationEvent event);
}
