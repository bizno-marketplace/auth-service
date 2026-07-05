package com.biznopay.authservice.usecase.auth.validateToken;

import com.biznopay.authservice.domain.gateway.AuthenticationGateway;

public class ValidateToken {
    private final AuthenticationGateway authenticationGateway;

    public ValidateToken(AuthenticationGateway authenticationGateway) {
        this.authenticationGateway = authenticationGateway;
    }

    public ValidateTokenOutput execute(ValidateTokenInput input) {
        boolean isValid = authenticationGateway.isTokenSignatureValid(input.token());
        return new ValidateTokenOutput(isValid);
    }
}
