package com.biznopay.authservice.infra.config;

import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.ResendCooldownGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.usecase.user.account.confirmAccount.ConfirmAccount;
import com.biznopay.authservice.usecase.user.account.resendConfirmation.ResendConformation;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSA;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserConfig {
    private final UserGateway userGateway;
    private final DomainEventGateway domainEventGateway;
    private final ResendCooldownGateway resendCooldownGateway;
    private final ActivationTokenGateway activationTokenGateway;

    @Bean
    public ConfirmAccount confirmAccount() {
        return new ConfirmAccount(activationTokenGateway, userGateway);
    }

    @Bean
    public ResendConformation resendConformation() {
        return new ResendConformation(userGateway, domainEventGateway, resendCooldownGateway, activationTokenGateway);
    }

    @Bean
    public RegisterSA registerSA() {
        return new RegisterSA(userGateway, activationTokenGateway, domainEventGateway);
    }
}
