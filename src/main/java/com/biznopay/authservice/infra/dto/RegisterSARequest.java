package com.biznopay.authservice.infra.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record RegisterSARequest(
        @NotEmpty(message = "First name is required")
        @NotNull(message = "First name is required")
        String firstName,

        @NotEmpty(message = "Last name is required")
        @NotNull(message = "Last name is required")
        String lastName,

        @NotEmpty(message = "E-mail is required")
        @NotNull(message = "E-mail is required")
        String email,


        @NotEmpty(message = "Password is required")
        @NotNull(message = "Password is required")
        String password
) {
}
