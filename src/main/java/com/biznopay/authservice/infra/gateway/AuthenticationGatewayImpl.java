package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@RequiredArgsConstructor
public class AuthenticationGatewayImpl implements AuthenticationGateway {
    private final UserJpaRepository userJpaRepository;
    private final JwtHelper jwtHelper;

    @Override
    public User loggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        UserJpaEntity userJpaEntity = this.userJpaRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new SecurityException("Authenticated user not found in database: AUTHENTICATION-GATEWAY-011"));
        return UserMapper.toUserDomain(userJpaEntity);
    }

    @Override
    public String extractEmail(String token) {
        return jwtHelper.getUsername(token);
    }

    @Override
    public boolean isTokenValid(String token, User user) {
        return jwtHelper.isValid(token, user);
    }

    @Override
    public String extractUserId(String token) {
        return jwtHelper.getUserId(token);
    }

    @Override
    public String extractRole(String token) {
        return jwtHelper.getRole(token);
    }

    @Override
    public String extractStatus(String token) {
        return jwtHelper.getStatus(token);
    }

    @Override
    public Key getSignKey() {
        return jwtHelper.getSignKey();
    }

    @Override
    public boolean isTokenSignatureValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
