package com.biznopay.authservice.usecase.user.register.confirmAccount;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.ExpiredConfirmationTokenException;
import com.biznopay.authservice.domain.exception.InvalidConfirmationTokenException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.usecase.user.confirmAccount.ConfirmAccount;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class ConfirmAccountTests {
    @Mock
    private ActivationTokenGateway tokenGateway;
    @Mock
    private UserGateway userGateway;


    @Test
    @DisplayName("Should throw InvalidConfirmationTokenException when token is not found")
    public void shouldThrowInvalidConfirmationTokenExceptionWhenTokenIsNotFound() {
        UUID rawTokenId = UUID.randomUUID();
        ConfirmAccount confirmAccount = new ConfirmAccount(tokenGateway, userGateway);
        Assertions.assertThrows(InvalidConfirmationTokenException.class, () -> confirmAccount.execute(rawTokenId));
    }

    @Test
    @DisplayName("Should throw ExpiredConfirmationTokenException if token has expired")
    public void shouldThrowExpiredConfirmationTokenExceptionIfTokenHasExpired() {
        UUID rawTokenId = UUID.randomUUID();
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId activationTokenId = new ActivationTokenId(rawTokenId);
        LocalDateTime expiredAt = LocalDateTime.now().minusMinutes(15);
        ActivationToken activationToken = ActivationToken.reconstitute(activationTokenId, userId, false, expiredAt, expiredAt);
        Mockito.when(tokenGateway.findById(rawTokenId)).thenReturn(Optional.of(activationToken));
        ConfirmAccount confirmAccount = new ConfirmAccount(tokenGateway, userGateway);
        Assertions.assertThrows(ExpiredConfirmationTokenException.class, () -> confirmAccount.execute(rawTokenId));
        Assertions.assertFalse(activationToken.isValid());
    }
}
