package com.biznopay.authservice.presentation.dto;

import jakarta.validation.constraints.NotEmpty;

public record RegisterBuyerRequest(
        @NotEmpty(message = "First name is required")
        String firstName,

        @NotEmpty(message = "Last name is required")
        String lastName,

        @NotEmpty(message = "E-mail is required")
        String email,

        @NotEmpty(message = "Password is required")
        String password,

        @NotEmpty(message = "Phone number is required")
        String phoneNumber,

        AddressRequest deliveryAddress
) {
}
