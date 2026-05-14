package com.biznopay.authservice.usecase.user.account.resendConfirmation;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.exception.TokenCooldownException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.ResendCooldownGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.time.Duration;
import java.util.Optional;

public class ResendConformation {
    public static final Duration COOLDOWN = Duration.ofMinutes(2);

    private final UserGateway userGateway;
    private final DomainEventGateway domainEventGateway;
    private final ResendCooldownGateway resendCooldownGateway;
    private final ActivationTokenGateway activationTokenGateway;

    public ResendConformation(UserGateway userGateway, DomainEventGateway domainEventGateway,
                              ResendCooldownGateway resendCooldownGateway, ActivationTokenGateway activationTokenGateway) {
        this.userGateway = userGateway;
        this.domainEventGateway = domainEventGateway;
        this.resendCooldownGateway = resendCooldownGateway;
        this.activationTokenGateway = activationTokenGateway;
    }

    public  String execute(String email) {
        Optional<User> existingUser = userGateway.findByEmail(email);
        if (existingUser.isEmpty()) return "Successfully requested a new confirmation email.";
        User user = existingUser.get();
        if (user.getStatus() == UserStatus.ACTIVE)
            throw new AccountAlreadyConfirmedException("RESEND_CONFIRMATION-001");

        boolean isOnCooldown = resendCooldownGateway.isInCooldown(email);
        if (isOnCooldown)
            throw new TokenCooldownException("RESEND_CONFIRMATION-002");

        Optional<ActivationToken> optActivationToken = activationTokenGateway.findActiveByUserId(user.getId().value());
        optActivationToken.ifPresent(activationTokenGateway::delete);
        ActivationToken token = ActivationToken.generate(user.getId());
        activationTokenGateway.save(token);
        resendCooldownGateway.startCooldown(email, COOLDOWN);
        UserRegistered event = UserRegistered.of(user.getId(), user.getEmail(), user.getFirstName(), token.getId());
        domainEventGateway.publish(event);
        return "Successfully requested a new confirmation email.";
    }
}
