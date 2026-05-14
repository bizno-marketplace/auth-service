package com.biznopay.authservice.usecase.user.account.resendConfirmation;

import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.gateway.UserGateway;

public class ResendConformation {
    private final UserGateway userGateway;

    public ResendConformation(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public  void execute(String email) {
        userGateway.findByEmail(email);
        throw new AccountAlreadyConfirmedException("RESNED_CONFIRMATION-001");
    }
}
