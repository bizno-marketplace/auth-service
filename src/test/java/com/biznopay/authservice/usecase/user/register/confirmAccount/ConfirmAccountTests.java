package com.biznopay.authservice.usecase.user.register.confirmAccount;

import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.usecase.user.confirmAccount.ConfirmAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ConfirmAccountTests {
    @Mock
    private ActivationTokenGateway tokenGateway;
    @Mock
    private UserGateway userGateway;


    @Test
    @DisplayName("Should throw ResourceNotFoundException when token is not found")
    public void shouldThrowResourceNotFoundExceptionWhenTokenIsNotFound() {
        UUID rawTokenId = UUID.randomUUID();
        ConfirmAccount confirmAccount =  new ConfirmAccount(tokenGateway, userGateway);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> confirmAccount.execute(rawTokenId));
    }

}
