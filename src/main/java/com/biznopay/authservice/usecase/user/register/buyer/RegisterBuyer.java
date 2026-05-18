package com.biznopay.authservice.usecase.user.register.buyer;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.Optional;

import static com.biznopay.authservice.domain.util.DomainFuncUtils.validatePassword;

public class RegisterBuyer {
    private final UserGateway userGateway;

    public RegisterBuyer(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public RegisterBuyerOutput execute(RegisterBuyerInput input) {
        Optional<User> optUser = userGateway.findByEmail(input.email());
        if (optUser.isPresent()) {
            throw new EmailAlreadyInUseException("REGISTER_BUYER-001");
        }
        String rawPassword = validatePassword(input.password());
        return new RegisterBuyerOutput("Buyer registered successfully");
    }
}
