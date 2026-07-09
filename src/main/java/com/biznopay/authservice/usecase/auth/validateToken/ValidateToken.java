package com.biznopay.authservice.usecase.auth.validateToken;

import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.MetricsGateway;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ValidateToken {
    private final AuthenticationGateway authenticationGateway;
    private final MetricsGateway metricsGateway;

    public ValidateTokenOutput execute(ValidateTokenInput input) {
        boolean isValid = authenticationGateway.isTokenSignatureValid(input.token());
        metricsGateway.recordValidateTokenDuration(5000);
        return new ValidateTokenOutput(isValid);
    }
}
