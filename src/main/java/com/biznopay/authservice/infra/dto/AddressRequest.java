package com.biznopay.authservice.infra.dto;

import jakarta.validation.constraints.NotNull;

public record AddressRequest(
        @NotNull(message = "Latitude is required")
        Double latitude,
        @NotNull(message = "Longitude is required")
        Double longitude,
        String street,
        String neighbourhood,
        String city,
        String province,
        String country
) {
}
