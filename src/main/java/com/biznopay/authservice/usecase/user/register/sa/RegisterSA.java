package com.biznopay.authservice.usecase.user.register.sa;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.ConflictException;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.EncoderGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.Optional;

import static com.biznopay.authservice.domain.util.DomainFuncUtils.validatePassword;


public class RegisterSA {
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final DomainEventGateway domainEventGateway;
    private final ActivationTokenGateway activationTokenGateway;

    public RegisterSA(UserGateway userGateway, EncoderGateway encoderGateway, DomainEventGateway domainEventGateway,
                      ActivationTokenGateway activationTokenGateway) {
        this.userGateway = userGateway;
        this.encoderGateway = encoderGateway;
        this.domainEventGateway = domainEventGateway;
        this.activationTokenGateway = activationTokenGateway;
    }

    public RegisterSAOutput execute(RegisterSAInput input) {
        long contSAs = userGateway.countSAs();
        if (contSAs > 0) throw new ConflictException("Super admin", "REGISTER_SA-001");
        Optional<User> user = userGateway.findByEmail(input.email());
        if (user.isPresent()) throw new EmailAlreadyInUseException("REGISTER_SA-002");
        String rawPassword = validatePassword(input.password());
        String encodedPassword = encoderGateway.encode(rawPassword);
        SuperAdmin superAdmin = SuperAdmin.register(input.firstName(), input.lastName(), input.email(), encodedPassword);
        userGateway.save(superAdmin);
        ActivationToken token = ActivationToken.generate(superAdmin.getId());
        activationTokenGateway.save(token);
        UserRegistered event = UserRegistered.of(superAdmin.getId(), superAdmin.getEmail(), superAdmin.getFirstName(), token.getId());
        domainEventGateway.publish(event);
        return new RegisterSAOutput("We've sent an activation link to provided email: " + input.email());
    }
}
