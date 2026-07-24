package com.biznopay.authservice.usecase.auth.login;

public record LoginOutput(String accessToken, String refreshToken) {
}
