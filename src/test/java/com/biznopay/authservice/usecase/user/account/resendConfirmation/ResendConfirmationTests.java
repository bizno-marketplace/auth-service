package com.biznopay.authservice.usecase.user.account.resendConfirmation;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.exception.TokenCooldownException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.ResendCooldownGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
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
public class ResendConfirmationTests {
    @Mock
    private UserGateway userGateway;
    @Mock
    private  DomainEventGateway domainEventGateway;
    @Mock
    private ResendCooldownGateway resendCooldownGateway;
    @Mock
    private ActivationTokenGateway activationTokenGateway;

    private ResendConformation setUp(){
        return new ResendConformation(userGateway,domainEventGateway,resendCooldownGateway,activationTokenGateway);
    }

    @Test
    @DisplayName("Should return successfully message when account does not exist")
    public void shouldReturnSuccessfullyMessageWhenAccountDoesNotExist(){
        String email = "user@example.com";
        Mockito.when(userGateway.findByEmail(email)).thenReturn(Optional.empty());
        ResendConformation resendConformation =  setUp();
        String result = resendConformation.execute(email);
        Assertions.assertEquals("Successfully requested a new confirmation email.", result);
    }

    @Test
    @DisplayName("Should throw AccountAlreadyConfirmedException when account has confirmed ")
    public void shouldThrowAccountAlreadyConfirmedExceptionWhenAccountHasConfirmed() {
        String email = "user@example.com";
        User user = Mocks.buyerMock();
        user.activate();
        Mockito.when(userGateway.findByEmail(email)).thenReturn(Optional.of(user));
        ResendConformation resendConformation =  setUp();
        Assertions.assertThrows(AccountAlreadyConfirmedException.class, () -> resendConformation.execute(email));
    }

    @Test
    @DisplayName("Should throw TokenCooldownException when account is on cooldown")
    public void shouldThrowTokenCooldownExceptionWhenAccountIsOnCoolDown(){
        String email = "user@example.com";
        User user = Mocks.buyerMock();
        Mockito.when(userGateway.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(resendCooldownGateway.isInCooldown(email)).thenReturn(true);
        ResendConformation resendConformation =  setUp();
        Assertions.assertThrows(TokenCooldownException.class, () -> resendConformation.execute(email));
    }
}
