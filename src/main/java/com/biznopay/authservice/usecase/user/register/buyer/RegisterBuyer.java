package com.biznopay.authservice.usecase.user.register.buyer;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.EncoderGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;

import java.util.Optional;

import static com.biznopay.authservice.domain.util.DomainFuncUtils.validatePassword;

public class RegisterBuyer {
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final DomainEventGateway domainEventGateway;
    private final ActivationTokenGateway activationTokenGateway;

    public RegisterBuyer(UserGateway userGateway, EncoderGateway encoderGateway, DomainEventGateway domainEventGateway, 
                         ActivationTokenGateway activationTokenGateway) {
        this.userGateway = userGateway;
        this.encoderGateway = encoderGateway;
        this.domainEventGateway = domainEventGateway;
        this.activationTokenGateway = activationTokenGateway;
    }

    public RegisterBuyerOutput execute(RegisterBuyerInput input) {
        Optional<User> optUser = userGateway.findByEmail(input.email());
        if (optUser.isPresent()) throw new EmailAlreadyInUseException("REGISTER_BUYER-001");
        String rawPassword = validatePassword(input.password(), "User", "REGISTER_BUYER-002");
        String encodedPassword = encoderGateway.encode(rawPassword);
        User buyer = Buyer.register(input.firstName(),input.lastName(),input.email(),input.phoneNumber(),encodedPassword,input.address());
        userGateway.save(buyer);
        ActivationToken token = ActivationToken.generate(buyer.getId());
        activationTokenGateway.save(token);
        UserRegistered event = UserRegistered.of(buyer.getId(), buyer.getEmail(), buyer.getFirstName(), token.getId());
        domainEventGateway.publish(event);
        return new RegisterBuyerOutput("We've sent an activation link to provided email: " + input.email());
    }
}
