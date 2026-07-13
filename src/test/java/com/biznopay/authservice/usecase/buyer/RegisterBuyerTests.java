package com.biznopay.authservice.usecase.buyer;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.exception.InvalidPasswordException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static com.biznopay.authservice.testcases.BuyerTestCases.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class RegisterBuyerTests {
    TransactionGateway transactionGateway = new TransactionGatewayImpl();
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

    private RegisterBuyer usecase;

    @BeforeEach
    void setUp() {
        usecase = new RegisterBuyer(transactionGateway, userGateway, encoderGateway,
                domainEventGateway, activationTokenGateway, metricsGateway);
    }

    @Test
    @DisplayName("Should throw email EmailAlreadyInUseException when email is already in use")
    public void shouldThrowEmailAlreadyInUseExceptionWhenEmailIsAlreadyInUse() {
        Buyer user = VALID_BUYER_DEFINED_ADDRESS();
        ;
        RegisterBuyerInput input = VALID_REGISTER_BUYER_INPUT;
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> usecase.execute(input));
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException when password is null or empty")
    public void shouldThrowRequiredFieldExceptionWhenPasswordIsNulOrEmpty(String password) {
        RegisterBuyerInput input = registerBuyerInputWithInvalidPassword(password);
        ;
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        Assertions.assertThrows(RequiredFieldException.class, () -> usecase.execute(input));
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Senha@Senha", "senha@1234", "Senha123"})
    @DisplayName("Should throw InvalidPasswordException when password does not match with established rules")
    public void shouldThrowInvalidPasswordExceptionWhenPasswordDoesNotMatchWithEstablishedRules(String password) {
        RegisterBuyerInput input = registerBuyerInputWithInvalidPassword(password);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        Assertions.assertThrows(InvalidPasswordException.class, () -> usecase.execute(input));
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }

    @Test
    @DisplayName("Should registry buyer, send activation link with 15 min of expiration to provided email and return message to notify user")
    public void shouldRegistryBuyerSedActivationLinkWith15MinOfExpirationToProvidedEmailAndReturnMessageToNotifyUser() {
        RegisterBuyerInput input = VALID_REGISTER_BUYER_INPUT;
        String encodedPassword = "Any@EncodePassword019";
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(encoderGateway.encode(input.password())).thenReturn(encodedPassword);
        Mockito.doNothing().when(userGateway).save(Mockito.any(Buyer.class));
        Mockito.doNothing().when(activationTokenGateway).save(Mockito.any(ActivationToken.class));
        Mockito.doNothing().when(domainEventGateway).publish(Mockito.any(UserRegistered.class));
        Mockito.doNothing().when(metricsGateway).incrementBuyerRegistered();

        RegisterBuyerOutput output = usecase.execute(input);
        Assertions.assertEquals("We've sent an activation link to provided email: " + input.email(), output.message());
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
        Mockito.verify(encoderGateway, Mockito.times(1)).encode(input.password());
        Mockito.verify(userGateway, Mockito.times(1)).save(Mockito.any(Buyer.class));
        Mockito.verify(activationTokenGateway, Mockito.times(1)).save(Mockito.any(ActivationToken.class));
        Mockito.verify(domainEventGateway, Mockito.times(1)).publish(Mockito.any(UserRegistered.class));
    }
}
