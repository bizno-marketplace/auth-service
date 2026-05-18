package com.biznopay.authservice.usecase.user.register.buyer;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.exception.InvalidPasswordException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
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

    @Test
    @DisplayName("Should throw email EmailAlreadyInUseException when email is already in use")
    public void shouldThrowEmailAlreadyInUseExceptionWhenEmailIsAlreadyInUse() {
        User user = Mocks.buyerMock();
        Address address = Mocks.addressMock();
        RegisterBuyerInput input = new RegisterBuyerInput(user.getFirstName(), user.getLastName(),
                user.getEmail(), user.getPassword(), user.getPhone(), address);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        RegisterBuyer registerBuyer = new RegisterBuyer(userGateway);
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
        RegisterBuyer registerBuyer = new RegisterBuyer(userGateway);
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
        RegisterBuyer registerBuyer = new RegisterBuyer(userGateway);
        Assertions.assertThrows(InvalidPasswordException.class, () -> registerBuyer.execute(input));
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }
}
