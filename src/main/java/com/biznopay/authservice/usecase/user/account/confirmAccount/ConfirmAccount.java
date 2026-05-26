package com.biznopay.authservice.usecase.user.account.confirmAccount;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.exception.ExpiredConfirmationTokenException;
import com.biznopay.authservice.domain.exception.InvalidConfirmationTokenException;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.UUID;

public class ConfirmAccount {
    private final ActivationTokenGateway tokenGateway;
    private final UserGateway userGateway;

    public ConfirmAccount(ActivationTokenGateway tokenGateway, UserGateway userGateway) {
        this.tokenGateway = tokenGateway;
        this.userGateway = userGateway;
    }

    public void execute(String rawTokenId) {
        UUID tokenId = validateActivationTokenId(rawTokenId);
        ActivationToken activationToken = tokenGateway.findById(tokenId).
                orElseThrow(() -> new InvalidConfirmationTokenException("ACTIVATION_TOKEN-002"));

        if (activationToken.isExpired())
            throw new ExpiredConfirmationTokenException("ACTIVATION_TOKEN-003");

        User user = userGateway.findById(activationToken.getUserId().value()).
                orElseThrow(() -> new ResourceNotFoundException("User", "ACTIVATION_TOKEN-004"));

        if (activationToken.isUsed() && UserStatus.ACTIVE == user.getStatus())
            throw new AccountAlreadyConfirmedException("ACTIVATION_TOKEN-005");

        user.activate();
        userGateway.save(user);
        tokenGateway.delete(activationToken);
    }

    private UUID validateActivationTokenId(String rawTokenId) {
        UUID tokenId = null;
        try {
            tokenId = UUID.fromString(rawTokenId);
        } catch (IllegalArgumentException ex) {
            throw new InvalidConfirmationTokenException("ACTIVATION_TOKEN-001");
        }
        return tokenId;
    }
}
