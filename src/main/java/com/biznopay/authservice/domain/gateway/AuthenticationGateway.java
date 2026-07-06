package com.biznopay.authservice.domain.gateway;

import com.biznopay.authservice.domain.entity.user.User;

import java.security.Key;

public interface AuthenticationGateway {
    User loggedUser();

    String extractEmail(String token);

    boolean isTokenValid(String token, User user);

    String extractUserId(String token);

    String extractRole(String token);

    String extractStatus(String token);

    boolean isTokenSignatureValid(String token);

    Key getSignKey();
}
