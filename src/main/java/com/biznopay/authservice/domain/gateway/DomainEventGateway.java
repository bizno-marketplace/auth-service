package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.event.UserRegistered;

public interface DomainEventGateway {
    void publish(UserRegistered event);
}
