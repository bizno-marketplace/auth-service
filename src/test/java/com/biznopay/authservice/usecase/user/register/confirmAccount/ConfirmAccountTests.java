package com.biznopay.authservice.usecase.user.register.confirmAccount;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.Buyer;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.exception.ExpiredConfirmationTokenException;
import com.biznopay.authservice.domain.exception.InvalidConfirmationTokenException;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
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

    @Test
    @DisplayName("Should throw AccountAlreadyConfirmedException if account is already confirmed")
    public void shouldThrowAccountAlreadyConfirmedExceptionIfAccountIsAlreadyConfirmed() {
        UUID rawTokenId = UUID.randomUUID();
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId activationTokenId = new ActivationTokenId(rawTokenId);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);
        ActivationToken activationToken = ActivationToken.reconstitute(activationTokenId, userId, true, expiredAt, expiredAt);
        Mockito.when(tokenGateway.findById(rawTokenId)).thenReturn(Optional.of(activationToken));
        ConfirmAccount confirmAccount = new ConfirmAccount(tokenGateway, userGateway);
        Assertions.assertThrows(AccountAlreadyConfirmedException.class, () -> confirmAccount.execute(rawTokenId));
        Assertions.assertFalse(activationToken.isValid());
    }


    @Test
    @DisplayName("Should throw ResourceNotFoundException if user does not exist")
    public void shouldThrowResourceNotFoundExceptionIfUserDoesNotExist() {
        UUID rawTokenId = UUID.randomUUID();
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId activationTokenId = new ActivationTokenId(rawTokenId);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);
        ActivationToken activationToken = ActivationToken.reconstitute(activationTokenId, userId, false, expiredAt, expiredAt);
        Mockito.when(tokenGateway.findById(rawTokenId)).thenReturn(Optional.of(activationToken));
        Mockito.when(userGateway.findById(userId.value())).thenReturn(Optional.empty());

        ConfirmAccount confirmAccount = new ConfirmAccount(tokenGateway, userGateway);
        Assertions.assertThrows(ResourceNotFoundException.class, () -> confirmAccount.execute(rawTokenId));
        Assertions.assertTrue(activationToken.isValid());
        Mockito.verify(tokenGateway, Mockito.times(1)).findById(rawTokenId);
        Mockito.verify(userGateway, Mockito.times(1)).findById(userId.value());
    }

    @Test
    @DisplayName("Should active user and mark activation token as used")
    public void shouldActiveUserAndMarkActivationTokenAsUsed() {
        UUID rawTokenId = UUID.randomUUID();
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId activationTokenId = new ActivationTokenId(rawTokenId);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);
        ActivationToken activationToken = ActivationToken.reconstitute(activationTokenId, userId, false, expiredAt, expiredAt);
        Mockito.when(tokenGateway.findById(rawTokenId)).thenReturn(Optional.of(activationToken));

        User user = Buyer.reconstitute(userId, "any_first_name", "any_last_name", "email@test",
                "any_phone", "Password@0199", UserStatus.PENDING, LocalDateTime.now(),
                LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(userGateway.findById(userId.value())).thenReturn(Optional.of(user));
        ConfirmAccount confirmAccount = new ConfirmAccount(tokenGateway, userGateway);
        confirmAccount.execute(rawTokenId);
        Assertions.assertTrue(activationToken.isValid());
        Mockito.verify(tokenGateway, Mockito.times(1)).findById(rawTokenId);
        Mockito.verify(userGateway, Mockito.times(1)).findById(userId.value());
        Mockito.verify(userGateway, Mockito.times(1)).save(user);
        UserJpaEntity userJpaEntity = UserMapper.toUserJpaEntity(user);
        Mockito.verify(tokenGateway, Mockito.times(1)).markAsUsed(rawTokenId);
    }
}
