package com.biznopay.authservice.usecase.auth;

import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.MetricsGateway;
import com.biznopay.authservice.usecase.auth.validateToken.ValidateToken;
import com.biznopay.authservice.usecase.auth.validateToken.ValidateTokenInput;
import com.biznopay.authservice.usecase.auth.validateToken.ValidateTokenOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
public class ValidateTokenTests {
    @InjectMocks
    private ValidateToken usecase;
    @Mock
    private AuthenticationGateway authenticationGateway;
    @Mock
    private MetricsGateway metricsGateway;

    @Test
    @DisplayName("Should return false if token is invalid")
    public void shouldReturnFalseIfTokenIsInvalid() {
        ValidateTokenInput input = new ValidateTokenInput("invalid-token");
        ValidateTokenOutput output = usecase.execute(input);
        assertFalse(output.isValid());
    }

    @Test
    @DisplayName("Should return true if token is valid")
    public void shouldReturnTrueIfTokenIsValid() {
        ValidateTokenInput input = new ValidateTokenInput("valid-token");
        Mockito.when(authenticationGateway.isTokenSignatureValid("valid-token")).thenReturn(true);
        ValidateTokenOutput output = usecase.execute(input);
        assertTrue(output.isValid());
    }
}
