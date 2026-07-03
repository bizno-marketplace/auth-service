package com.biznopay.authservice.infra.helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtHelper {

    @Value("app.secret-key")
    private String secretKey;

    private Key getSignKey() {
        byte[] keyByes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyByes);
    }

    public String create(Map<String, Object> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .signWith(getSignKey(), SignatureAlgorithm.HS256).compact();
    }

    public String getUsername(String token) {
        return this.extractClaim(token, Claims::getSubject);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsExtractor) {
        final Claims claims = this.extractAllClaims(token);
        return claimsExtractor.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSignKey()).build().parseClaimsJws(token).getBody();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        final String username = getUsername(token);
        return username.equals(userDetails.getUsername());
    }
}
