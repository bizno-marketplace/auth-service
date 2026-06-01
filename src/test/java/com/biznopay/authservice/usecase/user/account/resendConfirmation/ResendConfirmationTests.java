package com.biznopay.authservice.usecase.user.account.resendConfirmation;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.AccountAlreadyConfirmedException;
import com.biznopay.authservice.domain.exception.TokenCooldownException;
import com.biznopay.authservice.domain.gateway.*;
import com.biznopay.authservice.infra.gateway.TransactionGatewayImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.biznopay.authservice.testcases.BuyerTestCases.validBuyer;


@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ResendConfirmationTests {

    @Mock
    private UserGateway userGateway;
    @Mock
    private DomainEventGateway domainEventGateway;
    @Mock
    private ResendCooldownGateway resendCooldownGateway;
    @Mock
    private ActivationTokenGateway activationTokenGateway;

    private ResendConformation setUp() {
        TransactionGateway transactionGateway = new TransactionGatewayImpl();
        return new ResendConformation(transactionGateway,userGateway, domainEventGateway, resendCooldownGateway, activationTokenGateway);
    }

    @Test
    @Tag("unit")
    @DisplayName("Should return successfully message when account does not exist")
    public void shouldReturnSuccessfullyMessageWhenAccountDoesNotExist() {
        String email = "user@example.com";
        Mockito.when(userGateway.findByEmail(email)).thenReturn(Optional.empty());
        ResendConformation resendConformation = setUp();
        ResendConformationOutput result = resendConformation.execute(email);
        Assertions.assertEquals("Successfully requested a new confirmation email.", result.message());
    }

    @Test
    @DisplayName("Should throw AccountAlreadyConfirmedException when account has confirmed ")
    public void shouldThrowAccountAlreadyConfirmedExceptionWhenAccountHasConfirmed() {
        User user = validBuyer();
        String email = user.getEmail();
        user.activate();
        Mockito.when(userGateway.findByEmail(email)).thenReturn(Optional.of(user));
        ResendConformation resendConformation = setUp();
        Assertions.assertThrows(AccountAlreadyConfirmedException.class, () -> resendConformation.execute(email));
    }

    @Test
    @DisplayName("Should throw TokenCooldownException when account is on cooldown")
    public void shouldThrowTokenCooldownExceptionWhenAccountIsOnCoolDown() {
        User user = validBuyer();
        String email = user.getEmail();
        Mockito.when(userGateway.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(resendCooldownGateway.isInCooldown(email)).thenReturn(true);
        ResendConformation resendConformation = setUp();
        Assertions.assertThrows(TokenCooldownException.class, () -> resendConformation.execute(email));
    }

    @Test
    @DisplayName("Should publish email event, delete older token if exists, start cooldown and return successfully message")
    public void shouldPublishEmailEventDeleteOlderTokenIfExistsStartCooldownAndReturnSuccessfullyMessage() {
        User user = validBuyer();
        String email = user.getEmail();
        ActivationToken token = ActivationToken.generate(user.getId());

        Mockito.when(userGateway.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(resendCooldownGateway.isInCooldown(email)).thenReturn(false);
        Mockito.when(activationTokenGateway.findActiveByUserId(user.getId().value())).thenReturn(Optional.of(token));
        ResendConformation resendConformation = setUp();
        ResendConformationOutput output = resendConformation.execute(email);

        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(email);
        Mockito.verify(resendCooldownGateway, Mockito.times(1)).isInCooldown(email);
        Mockito.verify(activationTokenGateway, Mockito.times(1)).findActiveByUserId(user.getId().value());
        Mockito.verify(activationTokenGateway, Mockito.times(1)).delete(Mockito.any(ActivationToken.class));
        Mockito.verify(activationTokenGateway, Mockito.times(1)).save(Mockito.any(ActivationToken.class));
        Mockito.verify(resendCooldownGateway, Mockito.times(1)).startCooldown(email, ResendConformation.COOLDOWN);
        Mockito.verify(domainEventGateway, Mockito.times(1)).publish(Mockito.any(UserRegistered.class));
        Assertions.assertEquals("Successfully requested a new confirmation email.", output.message());
    }

    @Test
    @DisplayName("Should publish email event, not delete older token when not exists, start cooldown and return successfully message")
    public void shouldPublishEmailEventNotDeleteOlderTokenWhenNotExistsStartCooldownAndReturnSuccessfullyMessage() {
        User user = validBuyer();
        String email = user.getEmail();

        Mockito.when(userGateway.findByEmail(email)).thenReturn(Optional.of(user));
        Mockito.when(resendCooldownGateway.isInCooldown(email)).thenReturn(false);
        Mockito.when(activationTokenGateway.findActiveByUserId(user.getId().value())).thenReturn(Optional.empty());
        ResendConformation resendConformation = setUp();
        ResendConformationOutput output = resendConformation.execute(email);

        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(email);
        Mockito.verify(resendCooldownGateway, Mockito.times(1)).isInCooldown(email);
        Mockito.verify(activationTokenGateway, Mockito.times(1)).findActiveByUserId(user.getId().value());
        Mockito.verify(activationTokenGateway, Mockito.times(1)).save(Mockito.any(ActivationToken.class));
        Mockito.verify(resendCooldownGateway, Mockito.times(1)).startCooldown(email, ResendConformation.COOLDOWN);
        Mockito.verify(domainEventGateway, Mockito.times(1)).publish(Mockito.any(UserRegistered.class));
        Assertions.assertEquals("Successfully requested a new confirmation email.", output.message());
    }
}
