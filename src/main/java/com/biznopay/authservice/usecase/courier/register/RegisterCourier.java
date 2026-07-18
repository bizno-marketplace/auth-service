package com.biznopay.authservice.usecase.courier.register;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.Courier;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.RegisterCourierPolicy;

import java.util.Optional;

public class RegisterCourier {
    private final TransactionGateway transactionGateway;
    private final AuthenticationGateway authenticationGateway;
    private final RegisterCourierPolicy policy;
    private final UserGateway userGateway;
    private final EncoderGateway encoderGateway;
    private final ActivationTokenGateway activationTokenGateway;
    private final DomainEventGateway domainEventGateway;
    private final MetricsGateway metricsGateway;

    public RegisterCourier(TransactionGateway transactionGateway, AuthenticationGateway authenticationGateway,
                           RegisterCourierPolicy policy, UserGateway userGateway,
                           EncoderGateway encoderGateway, ActivationTokenGateway activationTokenGateway,
                           DomainEventGateway domainEventGateway, MetricsGateway metricsGateway) {
        this.transactionGateway = transactionGateway;
        this.authenticationGateway = authenticationGateway;
        this.policy = policy;
        this.userGateway = userGateway;
        this.encoderGateway = encoderGateway;
        this.activationTokenGateway = activationTokenGateway;
        this.domainEventGateway = domainEventGateway;
        this.metricsGateway = metricsGateway;
    }

    public RegisterCourierOutput execute(RegisterCourierInput input) {
        return transactionGateway.execute(() -> {
            User requestingUser = authenticationGateway.loggedUser();
            policy.enforce(requestingUser, "REGISTER_COURIER-001");
            Optional<User> courierOpt = userGateway.findByEmail(input.email());
            if (courierOpt.isPresent())
                throw new EmailAlreadyInUseException("REGISTER_COURIER-002");
            String encodedPassword = encoderGateway.encode(input.password());
            Courier courier = buildCourier(input, encodedPassword);
            userGateway.save(courier);
            ActivationToken token = ActivationToken.generate(courier.getId());
            activationTokenGateway.save(token);
            UserRegistered event = UserRegistered.of(courier.getId(), courier.getEmail(), courier.getFirstName(), token.getId());
            domainEventGateway.publish(event);
            metricsGateway.incrementCourierRegistered();
            return new RegisterCourierOutput("We've sent an activation link to provided email: " + input.email());
        });
    }

    private Courier buildCourier(RegisterCourierInput input, String encodedPassword) {
        return Courier.register(input.firstName(), input.lastname(), input.email(),
                input.phone(), encodedPassword, input.vehicleType(), input.licenseNumber(), input.zone());
    }
}
