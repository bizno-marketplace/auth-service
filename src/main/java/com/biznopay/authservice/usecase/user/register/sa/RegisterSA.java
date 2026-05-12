package com.biznopay.authservice.usecase.user.register.sa;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.ConflictException;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.Optional;

public class RegisterSA {
    private final UserGateway userGateway;
    private final ActivationTokenGateway activationTokenGateway;
    private final DomainEventGateway domainEventGateway;

    public RegisterSA(UserGateway userGateway, ActivationTokenGateway activationTokenGateway, DomainEventGateway domainEventGateway) {
        this.userGateway = userGateway;
        this.activationTokenGateway = activationTokenGateway;
        this.domainEventGateway = domainEventGateway;
    }

    public RegisterSAOutput execute(RegisterSAInput input) {
        long contSAs = userGateway.countSAs();
        if (contSAs > 0) throw new ConflictException("Super admin", "SUPER_ADMIN-003");
        Optional<User> user = userGateway.findByEmail(input.email());
        if (user.isPresent()) throw new EmailAlreadyInUseException("SUPER_ADMIN-004");
        SuperAdmin superAdmin = SuperAdmin.register(input.firstName(), input.lastName(), input.email(), input.password());
        userGateway.save(superAdmin);
        ActivationToken token = ActivationToken.generate(superAdmin.getId());
        activationTokenGateway.save(token);
        UserRegistered event = UserRegistered.of(superAdmin.getId(), superAdmin.getEmail(), superAdmin.getFirstName(), token.getId());
        domainEventGateway.publish(event);
        return new RegisterSAOutput("We've sent an activation link to provided email: " + input.email());
    }
}
