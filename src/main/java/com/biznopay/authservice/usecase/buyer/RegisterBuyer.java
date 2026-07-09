package com.biznopay.authservice.usecase.buyer;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.*;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.biznopay.authservice.domain.util.DomainFuncUtils.validatePassword;

@RequiredArgsConstructor
public class RegisterBuyer {
    private final TransactionGateway transactionGateway;
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final DomainEventGateway domainEventGateway;
    private final ActivationTokenGateway activationTokenGateway;
    private final MetricsGateway metricsGateway;

    public RegisterBuyerOutput execute(RegisterBuyerInput input) {
        return transactionGateway.execute(() -> {
            Optional<User> optUser = userGateway.findByEmail(input.email());
            if (optUser.isPresent()) throw new EmailAlreadyInUseException("REGISTER_BUYER-001");
            String rawPassword = validatePassword(input.password(), "User", "REGISTER_BUYER-002");
            String encodedPassword = encoderGateway.encode(rawPassword);
            User buyer = Buyer.register(input.firstName(), input.lastName(), input.email(), input.phoneNumber(), encodedPassword, input.address());
            userGateway.save(buyer);
            ActivationToken token = ActivationToken.generate(buyer.getId());
            activationTokenGateway.save(token);
            UserRegistered event = UserRegistered.of(buyer.getId(), buyer.getEmail(), buyer.getFirstName(), token.getId());
            domainEventGateway.publish(event);
            metricsGateway.incrementBuyerRegistered();
            return new RegisterBuyerOutput("We've sent an activation link to provided email: " + input.email());
        });
    }
}
