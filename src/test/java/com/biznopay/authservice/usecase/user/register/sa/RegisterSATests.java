package com.biznopay.authservice.usecase.user.register.sa;

import com.biznopay.authservice.domain.entity.activation.ActivationToken;
import com.biznopay.authservice.domain.entity.event.UserRegistered;
import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.exception.ConflictException;
import com.biznopay.authservice.domain.exception.EmailAlreadyInUseException;
import com.biznopay.authservice.domain.exception.InvalidPasswordException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.gateway.ActivationTokenGateway;
import com.biznopay.authservice.domain.gateway.DomainEventGateway;
import com.biznopay.authservice.domain.gateway.EncoderGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
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
    private  EncoderGateway encoderGateway;
    @Mock
    private  DomainEventGateway domainEventGateway;
    @Mock
    private  ActivationTokenGateway activationTokenGateway;

    private RegisterSA setupRegisterSA() {
        return new RegisterSA(userGateway, encoderGateway, domainEventGateway,activationTokenGateway);
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

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException if password is null or empty on register SA")
    public void shouldThrowRequiredFieldExceptionIfPasswordIsNullOrEmptyOnRegisterSA(String password) {
        RegisterSAInput input = new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", password);
        Mockito.when(userGateway.countSAs()).thenReturn(0L);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        RegisterSA useCase = setupRegisterSA();
        Assertions.assertThrows(RequiredFieldException.class, () -> useCase.execute(input), "Password is required");
        Mockito.verify(userGateway, Mockito.times(1)).countSAs();
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
        Mockito.verify(encoderGateway, Mockito.times(0)).encode(input.password());
    }

    @ParameterizedTest
    @ValueSource(strings = {"any_pass", "PassWord!", "Password123"})
    @DisplayName("Should throw InvalidPasswordException if password does not match with established rules")
    public void shouldThrowInvalidPasswordExceptionIfPasswordDoesNotMatchWithEstablishedRules(String password) {
        String msgError = "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character";
        RegisterSAInput input = new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", password);
        Mockito.when(userGateway.countSAs()).thenReturn(0L);
        Mockito.when(userGateway.findByEmail(input.email())).thenReturn(Optional.empty());
        RegisterSA useCase = setupRegisterSA();
        Assertions.assertThrows(InvalidPasswordException.class, () -> useCase.execute(input), msgError);
        Mockito.verify(userGateway, Mockito.times(1)).countSAs();
        Mockito.verify(userGateway, Mockito.times(1)).findByEmail(input.email());
        Mockito.verify(encoderGateway, Mockito.times(0)).encode(input.password());
    }

    @Test
    @DisplayName("Should registry supper admin, send activation link with 15 mins of expiration to provided email and return message to notify user")
    public void shouldRegisterSupperAdminSendActivationLinkWith15minsOfExpirationToProvidedEmailAndReturnMessageToNotifyUser() {
        RegisterSAInput input = new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        Mockito.when(userGateway.countSAs()).thenReturn(0L);
        Mockito.doNothing().when(activationTokenGateway).save(Mockito.any(ActivationToken.class));
        Mockito.when(encoderGateway.encode(input.password())).thenReturn("GGSGGnxhgajhasfsklm)0199");
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
