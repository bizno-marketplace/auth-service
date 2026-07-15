package com.biznopay.authservice.presentation.dto;

import com.biznopay.authservice.domain.enums.VehicleTypeEnum;

public record RegisterCourierRequest(
        String firstName,
        String lastname,
        String email,
        String phone,
        String password,
        VehicleTypeEnum vehicleType,
        String licenseNumber,
        String zone
) {
}