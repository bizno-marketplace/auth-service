package com.biznopay.authservice.usecase.user.account.resendConfirmation;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.Optional;

public class ResendConformation {
    private final UserGateway userGateway;

    public ResendConformation(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public  String execute(String email) {
        Optional<User> existingUser = userGateway.findByEmail(email);
        if (existingUser.isEmpty()) return "Successfully requested a new confirmation email.";
        User user = existingUser.get();
        if (user.getStatus() == UserStatus.ACTIVE) throw new AccountAlreadyConfirmedException("RESNED_CONFIRMATION-001");
        return null;
    }
}
