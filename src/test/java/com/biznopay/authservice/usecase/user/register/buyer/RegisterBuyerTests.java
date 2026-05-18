package com.biznopay.authservice.usecase.user.register.buyer;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.exception.InvalidPasswordException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.EncoderGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.mocks.Mocks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RegisterBuyerTests {
    @Mock
    private UserGateway userGateway;
    @Mock
    private EncoderGateway encoderGateway;
    @Mock
    private DomainEventGateway domainEventGateway;
    @Mock
    private ActivationTokenGateway activationTokenGateway;

    private RegisterBuyer setUp() {
        return new RegisterBuyer(userGateway, encoderGateway, domainEventGateway, activationTokenGateway);
    }

    @Test
    @DisplayName("Should throw email EmailAlreadyInUseException when email is already in use")
    public void shouldThrowEmailAlreadyInUseExceptionWhenEmailIsAlreadyInUse() {
        User user = Mocks.buyerMock();
        Address address = Mocks.addressMock();
        RegisterBuyerInput input = new RegisterBuyerInput(user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPassword(), user.getPhone(), address);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        RegisterBuyer registerBuyer = setUp();
        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> registerBuyer.execute(input));
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException when password is null or empty")
    public void shouldThrowRequiredFieldExceptionWhenPasswordIsNulOrEmpty(String password) {
        User user = Mocks.buyerMock();
        Address address = Mocks.addressMock();
        RegisterBuyerInput input = new RegisterBuyerInput(user.getFirstName(), user.getLastName(),
                user.getEmail(), password, user.getPhone(), address);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        RegisterBuyer registerBuyer = setUp();
        Assertions.assertThrows(RequiredFieldException.class, () -> registerBuyer.execute(input));
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }

    @ParameterizedTest
    @ValueSource(strings = {"Senha@Senha", "senha@1234", "Senha123"})
    @DisplayName("Should throw InvalidPasswordException when password does not match with established rules")
    public void shouldThrowInvalidPasswordExceptionWhenPasswordDoesNotMatchWithEstablishedRules(String password) {
        User user = Mocks.buyerMock();
        Address address = Mocks.addressMock();
        RegisterBuyerInput input = new RegisterBuyerInput(user.getFirstName(), user.getLastName(),
                user.getEmail(), password, user.getPhone(), address);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        RegisterBuyer registerBuyer = setUp();
        Assertions.assertThrows(InvalidPasswordException.class, () -> registerBuyer.execute(input));
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }

    @Test
    @DisplayName("Should registry buyer, send activation link with 15 min of expiration to provided email and return message to notify user")
    public void shouldRegistryBuyerSedActivationLinkWith15MinOfExpirationToProvidedEmailAndReturnMessageToNotifyUser() {
        Address address = Mocks.addressMock();
        RegisterBuyerInput input = new RegisterBuyerInput("John", "Doe", "john.doe@example.com", "Senha@1234", "848484848", address);
        String encodedPassword = "Any@EncodePassword019";
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        Mockito.when(encoderGateway.encode(input.password())).thenReturn(encodedPassword);
        Mockito.doNothing().when(userGateway).save(Mockito.any(Buyer.class));
        Mockito.doNothing().when(activationTokenGateway).save(Mockito.any(ActivationToken.class));
        Mockito.doNothing().when(domainEventGateway).publish(Mockito.any(UserRegistered.class));
        RegisterBuyer registerBuyer = setUp();
        RegisterBuyerOutput output = registerBuyer.execute(input);
        Assertions.assertEquals("We've sent an activation link to provided email: " + input.email(), output.message());
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
        Mockito.verify(encoderGateway, Mockito.times(1)).encode(input.password());
        Mockito.verify(userGateway, Mockito.times(1)).save(Mockito.any(Buyer.class));
        Mockito.verify(activationTokenGateway, Mockito.times(1)).save(Mockito.any(ActivationToken.class));
        Mockito.verify(domainEventGateway, Mockito.times(1)).publish(Mockito.any(UserRegistered.class));
    }
}
