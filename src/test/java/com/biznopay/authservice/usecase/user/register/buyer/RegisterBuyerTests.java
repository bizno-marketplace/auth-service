package com.biznopay.authservice.usecase.user.register.buyer;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.mocks.Mocks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
                user.getEmail(), user.getPassword(), user.getPhone(),address );
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(user));
        RegisterBuyer registerBuyer = new RegisterBuyer(userGateway);
        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> registerBuyer.execute(input));
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }
}
