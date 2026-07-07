package com.biznopay.authservice.infra.config;

import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.ApproveSellerPolicy;
import com.biznopay.authservice.domain.policy.RejectSellerPolicy;
import com.biznopay.authservice.domain.policy.ResubmitSellerPolicy;
import com.biznopay.authservice.usecase.account.confirmAccount.ConfirmAccount;
import com.biznopay.authservice.usecase.account.resendConfirmation.ResendConformation;
import com.biznopay.authservice.usecase.auth.getUserProfile.GetUserProfile;
import com.biznopay.authservice.usecase.auth.validateToken.ValidateToken;
import com.biznopay.authservice.usecase.buyer.RegisterBuyer;
import com.biznopay.authservice.usecase.sa.RegisterSA;
import com.biznopay.authservice.usecase.seller.approveSeller.ApproveSeller;
import com.biznopay.authservice.usecase.seller.register.RegisterSeller;
import com.biznopay.authservice.usecase.seller.rejectSeller.RejectSeller;
import com.biznopay.authservice.usecase.seller.resubmitseller.ResubmitSeller;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class UserConfig {
    private final TransactionGateway transactionGateway;
    private final AuthenticationGateway authenticationGateway;
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final DomainEventGateway domainEventGateway;
    private final ResendCooldownGateway resendCooldownGateway;
    private final ActivationTokenGateway activationTokenGateway;
    private final StorageGateway storageGateway;
    private final SellerRejectionGateway sellerRejectionGateway;
    private final MetricsGateway metricsGateway;

    @Bean
    public ConfirmAccount confirmAccount() {
        return new ConfirmAccount(transactionGateway, activationTokenGateway, userGateway);
    }

    @Bean
    public ResendConformation resendConformation() {
        return new ResendConformation(transactionGateway, userGateway, domainEventGateway, resendCooldownGateway, activationTokenGateway);
    }

    @Bean
    public RegisterSA registerSA() {
        return new RegisterSA(transactionGateway, userGateway, encoderGateway, domainEventGateway, activationTokenGateway);
    }

    @Bean
    public RegisterBuyer registerBuyer() {
        return new RegisterBuyer(transactionGateway, userGateway, encoderGateway,
                domainEventGateway, activationTokenGateway, metricsGateway);
    }

    @Bean
    public RegisterSeller registerSeller() {
        return new RegisterSeller(transactionGateway, userGateway, encoderGateway,
                storageGateway, domainEventGateway, activationTokenGateway, metricsGateway);
    }

    @Bean
    public ApproveSeller approveSeller(ApproveSellerPolicy approveSellerPolicy) {
        return new ApproveSeller(approveSellerPolicy, authenticationGateway, userGateway, metricsGateway);
    }

    @Bean
    public RejectSeller rejectSeller(RejectSellerPolicy rejectSellerPolicy) {
        return new RejectSeller(transactionGateway, authenticationGateway, rejectSellerPolicy, userGateway, sellerRejectionGateway, metricsGateway);
    }

    @Bean
    public ResubmitSeller resubmitSeller(ResubmitSellerPolicy resubmitSellerPolicy) {
        return new ResubmitSeller(transactionGateway, resubmitSellerPolicy, authenticationGateway, userGateway,
                storageGateway, activationTokenGateway, domainEventGateway, metricsGateway);
    }

    @Bean
    public ApproveSellerPolicy approveSellerPolicy() {
        return new ApproveSellerPolicy();
    }

    @Bean
    public RejectSellerPolicy rejectSellerPolicy() {
        return new RejectSellerPolicy();
    }

    @Bean
    public ResubmitSellerPolicy resubmitSellerPolicy() {
        return new ResubmitSellerPolicy();
    }

    @Bean
    public ValidateToken validateToken() {
        return new ValidateToken(authenticationGateway, metricsGateway);
    }

    @Bean
    public GetUserProfile getUserProfile() {
        return new GetUserProfile(userGateway);
    }
}

