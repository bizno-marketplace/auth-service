package com.biznopay.authservice.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record LoginRequest(
        @NotEmpty(message = "E-mail is required")
        String email,
        @NotEmpty(message = "Password is required")
        String password) {
}
