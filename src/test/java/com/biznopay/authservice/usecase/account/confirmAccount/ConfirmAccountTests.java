package com.biznopay.authservice.usecase.account.confirmAccount;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.activation.ActivationTokenId;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.exception.ExpiredConfirmationTokenException;
import com.biznopay.authservice.domain.exception.InvalidConfirmationTokenException;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.TransactionGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static com.biznopay.authservice.testcases.BuyerTestCases.VALID_BUYER;
import static com.biznopay.authservice.testcases.SellerTestCases.VALID_SELLER;
import static com.biznopay.authservice.testcases.SuperAdminTestCases.VALID_SUPER_ADMIN;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ConfirmAccountTests {
    @Mock
    private ActivationTokenGateway tokenGateway;
    @Mock
    private UserGateway userGateway;

    public ConfirmAccount setUp() {
        TransactionGateway transactionGateway = new TransactionGatewayImpl();
        return new ConfirmAccount(transactionGateway, tokenGateway, userGateway);
    }

    @Test
    @DisplayName("Should throw InvalidConfirmationTokenException when token is not found")
    public void shouldThrowInvalidConfirmationTokenExceptionWhenTokenIsNotFound() {
        ConfirmAccount confirmAccount = setUp();
        Assertions.assertThrows(InvalidConfirmationTokenException.class, () -> confirmAccount.execute("any_token_id"));
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
        ConfirmAccount confirmAccount = setUp();
        Assertions.assertThrows(ExpiredConfirmationTokenException.class, () -> confirmAccount.execute(rawTokenId.toString()));
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
        Mockito.when(userGateway.findById(userId.value())).thenReturn(Optional.empty());
        Mockito.when(tokenGateway.findById(rawTokenId)).thenReturn(Optional.of(activationToken));

        ConfirmAccount confirmAccount = setUp();
        Assertions.assertThrows(ResourceNotFoundException.class, () -> confirmAccount.execute(rawTokenId.toString()));
        Assertions.assertTrue(activationToken.isValid());
        Mockito.verify(tokenGateway, Mockito.times(1)).findById(rawTokenId);
        Mockito.verify(userGateway, Mockito.times(1)).findById(userId.value());
    }

    @Test
    @DisplayName("Should throw AccountAlreadyConfirmedException if account is already confirmed")
    public void shouldThrowAccountAlreadyConfirmedExceptionIfAccountIsAlreadyConfirmed() {
        UUID rawTokenId = UUID.randomUUID();
        UserId userId = new UserId(UUID.randomUUID());
        ActivationTokenId activationTokenId = new ActivationTokenId(rawTokenId);
        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(15);
        ActivationToken activationToken = ActivationToken.reconstitute(activationTokenId, userId, true, expiredAt, expiredAt);
        User user = VALID_BUYER;
        user.activate();
        ;
        Mockito.when(userGateway.findById(userId.value())).thenReturn(Optional.of(user));
        Mockito.when(tokenGateway.findById(rawTokenId)).thenReturn(Optional.of(activationToken));
        ConfirmAccount confirmAccount = setUp();
        Assertions.assertThrows(AccountAlreadyConfirmedException.class, () -> confirmAccount.execute(rawTokenId.toString()));
        Assertions.assertFalse(activationToken.isValid());
    }

    @Test
    @DisplayName("Should active user and mark activation token as used")
    public void shouldActiveUserAndMarkActivationTokenAsUsed() {
        User user = VALID_SUPER_ADMIN;
        ActivationToken activationToken = ActivationToken.generate(user.getId());
        UUID rawTokenId = activationToken.getId().value();

        Mockito.when(tokenGateway.findById(rawTokenId)).thenReturn(Optional.of(activationToken));
        Mockito.when(userGateway.findById(user.getId().value())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userGateway).save(user);
        Mockito.doNothing().when(tokenGateway).delete(activationToken);

        ConfirmAccount confirmAccount = setUp();
        confirmAccount.execute(rawTokenId.toString());

        Mockito.verify(tokenGateway, Mockito.times(1)).findById(rawTokenId);
        Mockito.verify(userGateway, Mockito.times(1)).findById(user.getId().value());
        Mockito.verify(userGateway, Mockito.times(1)).save(user);
        Mockito.verify(tokenGateway, Mockito.times(1)).delete(Mockito.any(ActivationToken.class));
    }

    @Test
    @DisplayName("Should set status as waiting for approval if role is seller")
    public void shouldSetStatusAsWaitingForApprovalIfRoleIsSeller() {
        User user = VALID_SELLER;
        ActivationToken activationToken = ActivationToken.generate(user.getId());
        UUID rawTokenId = activationToken.getId().value();

        Mockito.when(tokenGateway.findById(rawTokenId)).thenReturn(Optional.of(activationToken));
        Mockito.when(userGateway.findById(user.getId().value())).thenReturn(Optional.of(user));
        Mockito.doNothing().when(userGateway).save(user);
        Mockito.doNothing().when(tokenGateway).delete(activationToken);

        ConfirmAccount confirmAccount = setUp();
        confirmAccount.execute(rawTokenId.toString());

        Mockito.verify(tokenGateway, Mockito.times(1)).findById(rawTokenId);
        Mockito.verify(userGateway, Mockito.times(1)).findById(user.getId().value());
        Mockito.verify(userGateway, Mockito.times(1)).save(user);
        Mockito.verify(tokenGateway, Mockito.times(1)).delete(Mockito.any(ActivationToken.class));
    }
}
