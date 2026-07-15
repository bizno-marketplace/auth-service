package com.biznopay.authservice.presentation.dto;

import com.biznopay.authservice.domain.enums.VehicleTypeEnum;
import jakarta.validation.constraints.NotEmpty;

public record RegisterCourierRequest(
        @NotEmpty(message = "First name is required")
        String firstName,
        @NotEmpty(message = "Last name is required")
        String lastname,
        @NotEmpty(message = "E-mail is required")
        String email,
        @NotEmpty(message = "Phone is required")
        String phone,
        @NotEmpty(message = "Password is required")
        String password,
        VehicleTypeEnum vehicleType,
        @NotEmpty(message = "License number is required")
        String licenseNumber,
        @NotEmpty(message = "Zone is required")
        String zone
) {
}