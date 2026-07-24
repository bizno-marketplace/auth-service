package com.biznopay.authservice.domain.vo;

public record AuthenticateOutput(String token, String refreshToken) {
}
