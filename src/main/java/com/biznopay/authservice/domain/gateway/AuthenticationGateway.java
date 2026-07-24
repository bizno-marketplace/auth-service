package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.vo.AuthenticateOutput;
import com.biznopay.authservice.domain.vo.RefreshTokenClaims;

import java.util.UUID;

public interface AuthenticationGateway {
    User loggedUser();

    boolean isTokenSignatureValid(String token);

    AuthenticateOutput authenticate(User user);

    boolean isRefreshTokenValid(UUID userId, String tokenId);

    void markRefreshTokenUsed(UUID userId, String tokenId);

    void revokeRefreshToken(UUID userId, String tokenId);

    void revokeAllRefreshTokens(UUID userId);

    RefreshTokenClaims extractRefreshClaims(String refreshToken);
}
