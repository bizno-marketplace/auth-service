package com.biznopay.authservice.infra.config;

import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSA;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserConfig {
    private final UserGateway userGateway;
    private final ActivationTokenGateway activationTokenGateway;
    private final DomainEventGateway domainEventGateway;

    @Bean
    public RegisterSA registerSA() {
        return new RegisterSA(userGateway, activationTokenGateway, domainEventGateway);
    }
}
