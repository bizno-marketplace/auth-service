package com.biznopay.authservice.usecase.user.account.resendConfirmation;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.exception.TokenCooldownException;
import com.biznopay.authservice.domain.gateway.*;

import java.time.Duration;
import java.util.Optional;

public class ResendConformation {
    public static final Duration COOLDOWN = Duration.ofMinutes(2);

    private final TransactionGateway transactionGateway;
    private final UserGateway userGateway;
    private final DomainEventGateway domainEventGateway;
    private final ResendCooldownGateway resendCooldownGateway;
    private final ActivationTokenGateway activationTokenGateway;

    public ResendConformation(TransactionGateway transactionGateway, UserGateway userGateway, DomainEventGateway domainEventGateway,
                              ResendCooldownGateway resendCooldownGateway, ActivationTokenGateway activationTokenGateway) {
        this.transactionGateway = transactionGateway;
        this.userGateway = userGateway;
        this.domainEventGateway = domainEventGateway;
        this.resendCooldownGateway = resendCooldownGateway;
        this.activationTokenGateway = activationTokenGateway;
    }

    public ResendConformationOutput execute(String email) {
      return  transactionGateway.execute(() -> {
            ResendConformationOutput output = new ResendConformationOutput("Successfully requested a new confirmation email.");
            Optional<User> existingUser = userGateway.findByEmail(email);
            if (existingUser.isEmpty()) return output;
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
            return output;
        });
    }
}
