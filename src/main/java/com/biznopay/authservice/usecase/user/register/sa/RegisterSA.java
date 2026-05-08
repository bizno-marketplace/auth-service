package com.biznopay.authservice.usecase.user.register.sa;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.ConflictException;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.Optional;

public class RegisterSA {
    private final UserGateway userGateway;

    public RegisterSA(UserGateway userGateway) {
        this.userGateway = userGateway;
    }

    public RegisterSAOutput execute(RegisterSAInput input) {
        long contSAs = userGateway.countSAs();
        if (contSAs > 0) throw new ConflictException("Super admin", "SUPER_ADMIN-003");
        Optional<User> user = userGateway.findByEmail(input.email());
        if (user.isPresent()) throw new ConflictException("Email already in use", "SUPER_ADMIN-004");
        SuperAdmin superAdmin = SuperAdmin.register(input.firstName(), input.lastName(), input.email(), input.password());
        userGateway.save(superAdmin);
        return new RegisterSAOutput("We've sent an activation link to provided email: " + input.email());
    }
}
