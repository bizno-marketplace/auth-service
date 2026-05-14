package com.biznopay.authservice.infra.dto;

import jakarta.validation.constraints.NotEmpty;

public record ResendConfirmationRequest(
        @NotEmpty( message = "E-mail is required")
        String email
) {
}
