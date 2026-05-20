package com.biznopay.authservice.usecase.user.register.seller;

import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.UserGateway;

public class RegisterSeller {

    private final UserGateway userGateway;

    public RegisterSeller(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public RegisterSellerOutput execute(RegisterSellerInput input) {
        userGateway.findByEmail(input.email());
        throw new EmailAlreadyInUseException("REGISTER_SELLER-001");
    }
}
