package com.biznopay.authservice.usecase.user.register.seller;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.exception.NuitAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.vo.Nuit;

import java.util.Optional;

public class RegisterSeller {

    private final UserGateway userGateway;

    public RegisterSeller(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public RegisterSellerOutput execute(RegisterSellerInput input) {
        Optional<User> optUser = userGateway.findByEmail(input.email());
        if (optUser.isPresent())
            throw new EmailAlreadyInUseException("REGISTER_SELLER-001");
        optUser =  userGateway.findByNuit(input.nuit());
        if (optUser.isPresent())
            throw new NuitAlreadyInUseException("REGISTER_SELLER-002");
        return null;
    }
}
