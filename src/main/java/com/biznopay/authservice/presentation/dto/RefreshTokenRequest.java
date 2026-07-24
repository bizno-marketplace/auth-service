package com.biznopay.authservice.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record RefreshTokenRequest(
        @NotEmpty(message = "Refresh accessToken is required")
        String refreshToken
) {
}
