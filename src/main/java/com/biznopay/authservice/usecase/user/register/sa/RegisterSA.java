package com.biznopay.authservice.usecase.user.register.sa;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.exception.ConflictException;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.Optional;

public class RegisterSA {
    private final UserGateway userGateway;

    public RegisterSA(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public void execute(RegisterSAInput input) {
        Optional<SuperAdmin> superAdmin = userGateway.findSuperByEmail(input.email());
        if (superAdmin.isPresent()) throw new ConflictException("Super admin", "SUPER_ADMIN-003");
    }
}
