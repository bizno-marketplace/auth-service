package com.biznopay.authservice.usecase.auth.logout;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.exception.AccessDeniedException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.gateway.TransactionGateway;
import com.biznopay.authservice.domain.vo.RefreshTokenClaims;

public class Logout {
    private final AuthenticationGateway authenticationGateway;
    private final TransactionGateway transactionGateway;

    public Logout(AuthenticationGateway authenticationGateway, TransactionGateway transactionGateway) {
        this.authenticationGateway = authenticationGateway;
        this.transactionGateway = transactionGateway;
    }

    public LogoutOutput execute(LogoutInput input) {
        return transactionGateway.execute(() -> {
            validateRequired(input);
            User loggedUser = authenticationGateway.loggedUser();
            tryRevoke(input.refreshToken(), loggedUser);
            return new LogoutOutput("Logged out successfully");
        });
    }

    private void validateRequired(LogoutInput input) {
        if (input.refreshToken() == null || input.refreshToken().isEmpty())
            throw new RequiredFieldException("Refresh accessToken", "Logout", "LOGOUT-001");
    }

    private void tryRevoke(String refreshToken, User loggedUser) {
        RefreshTokenClaims claims = authenticationGateway.extractRefreshClaims(refreshToken);
        if (!claims.userId().equals(loggedUser.getId().value())) throw new AccessDeniedException("LOGOUT-002");
        authenticationGateway.revokeRefreshToken(claims.userId(), claims.tokenId());
    }
}