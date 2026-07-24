package com.biznopay.authservice.infra.helper;

import com.biznopay.authservice.domain.entity.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtHelper {

    @Value("${app.secret-key}")
    private String secretKey;

    @Value("${app.access-accessToken-expiration-ms:1800000}")   // 30 min default
    private long accessTokenExpirationMs;

    @Value("${app.refresh-accessToken-expiration-ms:604800000}") // 7 dias default
    private long refreshTokenExpirationMs;

    public Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().value().toString());
        claims.put("role", user.getRole());
        claims.put("status", user.getStatus());
        claims.put("type", "access");
        return create(claims, user.getEmail(), accessTokenExpirationMs);
    }

    public String generateToken(String userId, String role, String status, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("status", status);
        claims.put("type", "access");
        return create(claims, email, accessTokenExpirationMs);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().value().toString());
        claims.put("type", "refresh");
        claims.put("jti", UUID.randomUUID().toString());
        return create(claims, user.getEmail(), refreshTokenExpirationMs);
    }

    public String create(Map<String, Object> claims, String username, long expirationMs) {
        Date now = new Date(System.currentTimeMillis());
        Date expiration = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsExtractor) {
        final Claims claims = this.extractAllClaims(token);
        return claimsExtractor.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    public String getUsername(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    public boolean isValid(String token, UserDetails userDetails) {
        final String username = getUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractClaim(token, Claims::getExpiration);
        return expiration.before(new Date());
    }

    public String getUserId(String token) {
        return this.extractClaim(token, claims -> claims.get("userId", String.class));
    }

    public String getRole(String token) {
        return this.extractClaim(token, claims -> claims.get("role", String.class));
    }

    public String getStatus(String token) {
        return this.extractClaim(token, claims -> claims.get("status", String.class));
    }

    public String getType(String token) {
        return this.extractClaim(token, claims -> claims.get("type", String.class));
    }

    public String getJti(String token) {
        return this.extractClaim(token, claims -> claims.get("jti", String.class));
    }
}