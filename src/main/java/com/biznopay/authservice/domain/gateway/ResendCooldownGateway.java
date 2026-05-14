package com.biznopay.authservice.domain.gateway;

import java.time.Duration;

public interface ResendCooldownGateway {
    boolean isInCooldown(String email);
    void startCooldown(String email, Duration duration);
}
