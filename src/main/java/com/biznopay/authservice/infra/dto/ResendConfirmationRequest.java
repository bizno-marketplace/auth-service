package com.biznopay.authservice.infra.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

public record ResendConfirmationRequest(
        @Email(message = "Invalid email format")
        @NotEmpty( message = "E-mail is required")
        String email
) {
}
