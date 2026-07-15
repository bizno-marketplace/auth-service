package com.biznopay.authservice.usecase.courier;

import com.biznopay.authservice.domain.exception.AccessDeniedException;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.domain.policy.RegisterCourierPolicy;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import com.biznopay.authservice.usecase.courier.register.RegisterCourier;
import com.biznopay.authservice.usecase.courier.register.RegisterCourierInput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.biznopay.authservice.testcases.CourierTestCases.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class RegisterCourierTests {
    private TransactionGateway transactionGateway =  new TransactionGatewayImpl();
    @Mock
    private AuthenticationGateway authenticationGateway;
    private RegisterCourierPolicy policy =  new RegisterCourierPolicy();
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
        return new RegisterCourier(transactionGateway, authenticationGateway,policy,userGateway,encoderGateway,
                activationTokenGateway,domainEventGateway,metricsGateway);
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
}
