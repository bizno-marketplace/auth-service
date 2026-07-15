package com.biznopay.authservice.usecase.courier.register;

import com.biznopay.authservice.domain.enums.VehicleTypeEnum;

public record RegisterCourierInput(
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