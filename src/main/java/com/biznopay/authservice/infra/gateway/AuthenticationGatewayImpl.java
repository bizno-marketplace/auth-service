package com.biznopay.authservice.infra.gateway;

import com.biznopay.authservice.domain.entity.user.User;
import com.biznopay.authservice.domain.enums.RefreshTokenStatus;
import com.biznopay.authservice.domain.exception.InvalidRefreshTokenException;
import com.biznopay.authservice.domain.gateway.AuthenticationGateway;
import com.biznopay.authservice.domain.vo.AuthenticateOutput;
import com.biznopay.authservice.domain.vo.RefreshTokenClaims;
import com.biznopay.authservice.infra.helper.JwtHelper;
import com.biznopay.authservice.infra.mapper.UserMapper;
import com.biznopay.authservice.infra.persistence.jpa.entity.UserJpaEntity;
import com.biznopay.authservice.infra.persistence.jpa.repository.UserJpaRepository;
import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AuthenticationGatewayImpl implements AuthenticationGateway {
    private static final long REUSE_DETECTION_WINDOW_SECONDS = 60;
    private static final String REDIS_KEY_PREFIX = "auth:refresh_token:";

    private final StringRedisTemplate redisTemplate;
    private final UserJpaRepository userJpaRepository;
    private final JwtHelper jwtHelper;

    @Value("${app.refresh-accessToken-expiration-ms:604800000}") // 7 dias default
    private long refreshTokenExpirationMs;

    @Override
    public User loggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assert authentication != null;
        UserJpaEntity userJpaEntity = this.userJpaRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new SecurityException("Authenticated user not found in database: AUTHENTICATION-GATEWAY-011"));
        return UserMapper.toUserDomain(userJpaEntity);
    }

    @Override
    public boolean isTokenSignatureValid(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(jwtHelper.getSignKey()).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void saveRefreshToken(UUID userId, String tokenId, long ttlSeconds) {
        redisTemplate.opsForValue().set(
                key(userId, tokenId),
                RefreshTokenStatus.ACTIVE.name(),
                Duration.ofSeconds(ttlSeconds));
    }

    @Override
    public AuthenticateOutput authenticate(User user) {
        String accessToken = jwtHelper.generateToken(user);
        String refreshToken = jwtHelper.generateRefreshToken(user);
        RefreshTokenClaims claims = extractRefreshClaims(refreshToken);
        saveRefreshToken(claims.userId(), claims.tokenId(), refreshTokenExpirationMs);
        return new AuthenticateOutput(accessToken, refreshToken);
    }

    private String key(UUID userId, String tokenId) {
        return REDIS_KEY_PREFIX + userId + ":" + tokenId;
    }

    @Override
    public boolean isRefreshTokenValid(UUID userId, String tokenId) {
        String value = redisTemplate.opsForValue().get(key(userId, tokenId));
        return RefreshTokenStatus.ACTIVE.name().equals(value);
    }

    @Override
    public void markRefreshTokenUsed(UUID userId, String tokenId) {
        redisTemplate.opsForValue().set(
                key(userId, tokenId),
                RefreshTokenStatus.USED.name(),
                Duration.ofSeconds(REUSE_DETECTION_WINDOW_SECONDS));
    }

    @Override
    public void revokeRefreshToken(UUID userId, String tokenId) {
        redisTemplate.delete(key(userId, tokenId));
    }

    @Override
    public void revokeAllRefreshTokens(UUID userId) {
        Set<String> keys = redisTemplate.keys(REDIS_KEY_PREFIX + userId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    @Override
    public RefreshTokenClaims extractRefreshClaims(String refreshToken) {
        String type = jwtHelper.getType(refreshToken);
        if (!"refresh".equals(type)) throw new InvalidRefreshTokenException();
        String userId = jwtHelper.getUserId(refreshToken);
        String tokenId = jwtHelper.getJti(refreshToken);
        return new RefreshTokenClaims(UUID.fromString(userId), tokenId);
    }
}