package com.biznopay.authservice.usecase.user.confirmAccount;

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

    public void execute(UUID rawTokenId) {
        throw new ResourceNotFoundException("Activation token", "ACTIVATION_TOKEN-001");
    }
}
