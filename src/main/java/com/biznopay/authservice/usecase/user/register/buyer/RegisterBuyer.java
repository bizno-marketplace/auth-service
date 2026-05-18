package com.biznopay.authservice.usecase.user.register.buyer;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.exception.InvalidPasswordException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.Optional;

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

    private String validatePassword(String password) {
        if (password == null || password.isEmpty())
            throw new RequiredFieldException("Password", User.class.getName(), "REGISTER_SA-003");
        return password;
    }
}
