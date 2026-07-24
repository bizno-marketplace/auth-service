package com.biznopay.authservice.usecase.auth.refreshToken;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.RefreshTokenExpiredException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.exception.ResourceNotFoundException;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.TransactionGateway;
import com.biznopay.authservice.domain.gateway.UserGateway;
import com.biznopay.authservice.domain.vo.AuthenticateOutput;
import com.biznopay.authservice.domain.vo.RefreshTokenClaims;

public class RefreshToken {
    private final TransactionGateway transactionGateway;
    private final AuthenticationGateway authenticationGateway;
    private final UserGateway userGateway;

    public RefreshToken(TransactionGateway transactionGateway, AuthenticationGateway authenticationGateway, UserGateway userGateway) {
        this.transactionGateway = transactionGateway;
        this.authenticationGateway = authenticationGateway;
        this.userGateway = userGateway;
    }

    public RefreshTokenOutput execute(RefreshTokenInput input) {
        return transactionGateway.execute(() -> {
            validateRefreshToken(input);
            RefreshTokenClaims claims = authenticationGateway.extractRefreshClaims(input.refreshToken());
            if (!authenticationGateway.isRefreshTokenValid(claims.userId(), claims.tokenId())) {
                authenticationGateway.revokeAllRefreshTokens(claims.userId());
                throw new RefreshTokenExpiredException();
            }
            authenticationGateway.markRefreshTokenUsed(claims.userId(), claims.tokenId());
            User user = userGateway.findById(claims.userId())
                    .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "REFRESH-TOKEN-002"));
            AuthenticateOutput authenticateOutput = authenticationGateway.authenticate(user);
            return new RefreshTokenOutput(authenticateOutput.token(), authenticateOutput.refreshToken());
        });
    }

    private void validateRefreshToken(RefreshTokenInput input) {
        if (input.refreshToken() == null || input.refreshToken().isEmpty())
            throw new RequiredFieldException("Refresh accessToken", "RefreshToken", "REFRESH-TOKEN-001");
    }

}
