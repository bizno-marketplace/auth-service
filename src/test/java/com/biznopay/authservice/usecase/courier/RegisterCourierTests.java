package com.biznopay.authservice.usecase.courier;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.Courier;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.AccessDeniedException;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.RegisterCourierPolicy;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import com.biznopay.authservice.usecase.courier.register.RegisterCourier;
import com.biznopay.authservice.usecase.courier.register.RegisterCourierInput;
import com.biznopay.authservice.usecase.courier.register.RegisterCourierOutput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.CourierTestCases.*;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class RegisterCourierTests {
    private TransactionGateway transactionGateway = new TransactionGatewayImpl();
    @Mock
    private AuthenticationGateway authenticationGateway;
    private RegisterCourierPolicy policy = new RegisterCourierPolicy();
    @Mock
    private UserGateway userGateway;
    @Mock
    private EncoderGateway encoderGateway;
    @Mock
    private ActivationTokenGateway activationTokenGateway;
    @Mock
    private DomainEventGateway domainEventGateway;
    @Mock
    private MetricsGateway metricsGateway;

    private RegisterCourier setUp() {
        return new RegisterCourier(transactionGateway, authenticationGateway, policy, userGateway, encoderGateway,
                activationTokenGateway, domainEventGateway, metricsGateway);
    }

    @Test
    @DisplayName("Should throw AccessDeniedException when no user is logged")
    public void shouldThrowAccessDeniedExceptionWhenNoUserIsLogged() {
        RegisterCourierInput input = new RegisterCourierInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE,
                VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);
        Mockito.when(authenticationGateway.loggedUser()).thenReturn(null);
        RegisterCourier usecase = setUp();
        Assertions.assertThatThrownBy(() -> usecase.execute(input))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Access denied");
    }

    @Test
    @DisplayName("Should throw EmailAlreadyInUseException when email is already in use")
    public void shouldThrowEmailAlreadyInUseExceptionWhenEmailIsAlreadyInUse() {
        RegisterCourierInput input = new RegisterCourierInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE,
                VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        Courier courier = validCourier();

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(VALID_SUPER_ADMIN);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(courier));


        RegisterCourier usecase = setUp();
        Assertions.assertThatThrownBy(() -> usecase.execute(input))
                .isInstanceOf(EmailAlreadyInUseException.class)
                .hasMessage("E-mail already in use");
    }

    @Test
    @DisplayName("Should save courier and send activation code")
    public void shouldSaveCourierAndSendActivationCode() {
        RegisterCourierInput input = new RegisterCourierInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE,
                VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE);

        String encodePassword = UUID.randomUUID().toString();

        Mockito.when(authenticationGateway.loggedUser()).thenReturn(VALID_SUPER_ADMIN);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(encoderGateway.encode(VALID_PASSWORD)).thenReturn(encodePassword);
        Mockito.doNothing().when(userGateway).save(Mockito.any(User.class));
        Mockito.doNothing().when(activationTokenGateway).save(Mockito.any(ActivationToken.class));
        Mockito.doNothing().when(domainEventGateway).publish(Mockito.any(UserRegistered.class));
        Mockito.doNothing().when(metricsGateway).incrementCourierRegistered();

        RegisterCourier usecase = setUp();
        RegisterCourierOutput output = usecase.execute(input);

        Assertions.assertThat(output).isNotNull();
        Assertions.assertThat("We've sent an activation link to provided email: " + input.email()).isEqualTo(output.message());

        Mockito.verify(authenticationGateway, Mockito.times(1)).loggedUser();
        Mockito.verify(userGateway, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(activationTokenGateway).save(Mockito.any(ActivationToken.class));
        Mockito.verify(domainEventGateway).publish(Mockito.any(UserRegistered.class));
        Mockito.verify(metricsGateway).incrementCourierRegistered();
    }
}
