package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.event.UserUpdated;

public interface DomainEventGateway {
    void publish(UserRegistered event);

    void publish(UserUpdated event);
}
