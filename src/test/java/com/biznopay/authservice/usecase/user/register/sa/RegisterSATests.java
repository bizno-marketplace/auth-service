package com.biznopay.authservice.usecase.user.register.sa;

import com.biznopay.authservice.domain.entity.user.SuperAdmin;
import com.biznopay.authservice.domain.exception.ConflictException;
import com.biznopay.authservice.domain.gateway.UserGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class RegisterSATests {
    @Mock
    private UserGateway userGateway;


    @Test
    @DisplayName("Should throw ConflictException when super admin already exists on register super admin")
    public void shouldThrowConflictExceptionWhenSuperAdminAlreadyExistsOnRegisterSuperAdmin() {
        RegisterSA useCase = new RegisterSA(userGateway);
        RegisterSAInput input =  new RegisterSAInput("any_first_name", "any_last_name", "admin@bizno.co.mz", "Password@123");
        SuperAdmin superAdmin =  SuperAdmin.register(input.firstName(), input.lastName(), input.email(), input.password());
        Mockito.when(userGateway.findSuperByEmail(input.email())).thenReturn(Optional.of(superAdmin));

        Assertions.assertThrows(ConflictException.class, () -> useCase.execute(input), "Super admin already exists");

        Mockito.verify(userGateway, Mockito.times(1)).findSuperByEmail(input.email());
    }
}
