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
import java.util.function.Function;

@Component
public class JwtHelper {

    @Value("${app.secret-key}")
    private String secretKey;

    public Key getSignKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String generate(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId().value().toString());
        claims.put("role", user.getRole());
        claims.put("status", user.getStatus());
        return create(claims, user.getEmail());
    }

    public String generate(String userId, String role, String status, String email) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("status", status);
        return create(claims, email);
    }

    public String create(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
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
        return username.equals(userDetails.getUsername());
    }

    public boolean isValid(String token, User user) {
        final String username = getUsername(token);
        return username.equals(user.getEmail());
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
}
