package com.biznopay.authservice.infra.config;

import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.usecase.user.account.confirmAccount.ConfirmAccount;
import com.biznopay.authservice.usecase.user.account.resendConfirmation.ResendConformation;
import com.biznopay.authservice.usecase.user.register.buyer.RegisterBuyer;
import com.biznopay.authservice.usecase.user.register.sa.RegisterSA;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSeller;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserConfig {
    private final TransactionGateway transactionGateway;
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final DomainEventGateway domainEventGateway;
    private final ResendCooldownGateway resendCooldownGateway;
    private final ActivationTokenGateway activationTokenGateway;
    private final StorageGateway storageGateway;

    @Bean
    public ConfirmAccount confirmAccount() {
        return new ConfirmAccount(transactionGateway,activationTokenGateway, userGateway);
    }

    @Bean
    public ResendConformation resendConformation() {
        return new ResendConformation(transactionGateway,userGateway, domainEventGateway, resendCooldownGateway, activationTokenGateway);
    }

    @Bean
    public RegisterSA registerSA() {
        return new RegisterSA(transactionGateway,userGateway, encoderGateway, domainEventGateway, activationTokenGateway);
    }

    @Bean
    public RegisterBuyer registerBuyer() {
        return new RegisterBuyer(transactionGateway,userGateway, encoderGateway, domainEventGateway, activationTokenGateway);
    }

    @Bean
    public RegisterSeller registerSeller() {
        return new RegisterSeller(transactionGateway,userGateway, encoderGateway, storageGateway, domainEventGateway, activationTokenGateway);
    }
}
