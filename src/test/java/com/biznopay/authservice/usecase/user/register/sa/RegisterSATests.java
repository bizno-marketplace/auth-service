package com.biznopay.authservice.usecase.user.register.sa;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.exception.ConflictException;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class RegisterSATests {
    @Mock
    private UserGateway userGateway;

    @Mock
    private ActivationTokenGateway activationTokenGateway;

    @Mock
    private DomainEventGateway domainEventGateway;

    private RegisterSA setupRegisterSA() {
        return new RegisterSA(userGateway, activationTokenGateway, domainEventGateway);
    }

    @Test
    @DisplayName("Should throw ConflictException when system already has a super admin on register super admin")
    public void shouldThrowConflictExceptionWhenSystemHAlreadyHasASuperAdminOnRegisterSuperAdmin() {
        RegisterSAInput input = new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        Mockito.when(userGateway.countSAs()).thenReturn(1L);
        RegisterSA useCase = setupRegisterSA();
        Assertions.assertThrows(ConflictException.class, () -> useCase.execute(input), "Super admin already exists");
        Mockito.verify(userGateway, Mockito.times(1)).countSAs();
    }

    @Test
    @DisplayName("Should throw EmailAlreadyInUseException when email is already in use on register super admin")
    public void shouldThrowEmailAlreadyInUseExceptionWhenEmailIsAlreadyInUseOnRegisterSuperAdmin() {
        RegisterSAInput input = new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        SuperAdmin superAdmin = SuperAdmin.register(input.firstName(), input.lastName(), input.email(), input.password());
        Mockito.when(userGateway.countSAs()).thenReturn(0L);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.of(superAdmin));
        RegisterSA useCase = setupRegisterSA();
        Assertions.assertThrows(EmailAlreadyInUseException.class, () -> useCase.execute(input), "Email already in use");
        Mockito.verify(userGateway, Mockito.times(1)).countSAs();
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
    }

    @Test
    @DisplayName("Should registry supper admin, send activation link with 15 mins of expiration to provided email and return message to notify user")
    public void shouldRegisterSupperAdminSendActivationLinkWith15minsOfExpirationToProvidedEmailAndReturnMessageToNotifyUser() {
        RegisterSAInput input = new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        Mockito.when(userGateway.countSAs()).thenReturn(0L);
        Mockito.doNothing().when(activationTokenGateway).save(Mockito.any(ActivationToken.class));
        Mockito.doNothing().when(domainEventGateway).publish(Mockito.any(UserRegistered.class));
        RegisterSA useCase = setupRegisterSA();
        RegisterSAOutput output = useCase.execute(input);
        Assertions.assertEquals("We've sent an activation link to provided email: " + input.email(), output.message());
        Mockito.verify(userGateway, Mockito.times(1)).countSAs();
        Mockito.verify(userGateway, Mockito.times(1)).save(Mockito.any(SuperAdmin.class));
        Mockito.verify(activationTokenGateway, Mockito.times(1)).save(Mockito.any(ActivationToken.class));
        Mockito.verify(domainEventGateway, Mockito.times(1)).publish(Mockito.any(UserRegistered.class));
    }
}
