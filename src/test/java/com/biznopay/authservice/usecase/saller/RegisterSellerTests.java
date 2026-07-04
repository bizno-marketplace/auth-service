package com.biznopay.authservice.usecase.saller;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import com.biznopay.authservice.usecase.seller.register.RegisterSeller;
import com.biznopay.authservice.usecase.seller.register.RegisterSellerInput;
import com.biznopay.authservice.usecase.seller.register.RegisterSellerOutput;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.SellerTestCases.*;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class RegisterSellerTests {
    @Mock
    private UserGateway userGateway;
    @Mock
    private EncoderGateway encoderGateway;
    @Mock
    private StorageGateway storageGateway;
    @Mock
    private DomainEventGateway domainEventGateway;
    @Mock
    private ActivationTokenGateway activationTokenGateway;

    private RegisterSeller setUp() {
        TransactionGateway transactionGateway = new TransactionGatewayImpl();
        return new RegisterSeller(transactionGateway, userGateway, encoderGateway, storageGateway, domainEventGateway, activationTokenGateway);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.SellerTestCases#invalidUseCaseRegisterSellerCases")
    @DisplayName("Should throw exception when seller data already exists or password is weak")
    void shouldThrowExceptionWhenSellerDataAlreadyExistsOrPasswordIsWeak(String testName, RegisterSellerInput input,
                                                                         Optional<User> existingByEmail,
                                                                         Optional<User> existingByNuit,
                                                                         Class<? extends Exception> expectedException) {

        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(existingByEmail);
        if (existingByNuit.isPresent())
            Mockito.when(userGateway.findByNuit(input.nuit())).thenReturn(existingByNuit);
        RegisterSeller registerSeller = setUp();
        Assertions.assertThatThrownBy(() -> registerSeller.execute(input)).isInstanceOf(expectedException);
    }

    @Test
    @DisplayName("Should register seller successfully with valid data")
    void shouldRegisterSellerSuccessfully() {
        RegisterSellerInput input = registerSellerInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,
                VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI_REQUEST);

        String encodePassword = UUID.randomUUID().toString();
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(userGateway.findByNuit(input.nuit())).thenReturn(Optional.empty());
        Mockito.when(encoderGateway.encode(VALID_PASSWORD)).thenReturn(encodePassword);
        Mockito.doNothing().when(storageGateway).upload(Mockito.anyList());
        Mockito.doNothing().when(userGateway).save(Mockito.any(User.class));
        Mockito.doNothing().when(activationTokenGateway).save(Mockito.any(ActivationToken.class));
        Mockito.doNothing().when(domainEventGateway).publish(Mockito.any(UserRegistered.class));

        RegisterSeller registerSeller = setUp();
        RegisterSellerOutput output = registerSeller.execute(input);
        Assertions.assertThat(output.message()).isEqualTo("We've sent an activation link to provided email: " + input.email());
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
        Mockito.verify(userGateway, Mockito.times(1)).findByNuit(input.nuit());
        Mockito.verify(encoderGateway, Mockito.times(1)).encode(VALID_PASSWORD);
        Mockito.verify(storageGateway, Mockito.times(1)).upload(Mockito.anyList());
        Mockito.verify(userGateway, Mockito.times(1)).save(Mockito.any(User.class));
        Mockito.verify(activationTokenGateway, Mockito.times(1)).save(Mockito.any(ActivationToken.class));
        Mockito.verify(domainEventGateway, Mockito.times(1)).publish(Mockito.any(UserRegistered.class));
    }
}
