package com.biznopay.authservice.infra.config;

import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.EncoderGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.usecase.user.confirmAccount.ConfirmAccount;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSA;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserConfig {
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final DomainEventGateway domainEventGateway;
    private final ActivationTokenGateway activationTokenGateway;

    @Bean
    public ConfirmAccount confirmAccount() {
        return new ConfirmAccount(activationTokenGateway, userGateway);
    }

    @Bean
    public RegisterSA registerSA() {
        return new RegisterSA(userGateway, encoderGateway, domainEventGateway, activationTokenGateway);
    }
}
