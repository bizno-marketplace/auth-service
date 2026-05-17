package com.biznopay.authservice.domain.vo;

import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

public record Address(
        Double latitude,
        Double longitude,
        String street,
        String neighbourhood,
        String city,
        String province,
        String country
) {
    public Address {
        if (latitude == null) throw new RequiredFieldException("Latitude", "Address", "ADDRESS-001");
        if (longitude == null) throw new IllegalArgumentException("Longitude is required");
        if (latitude < -90 || latitude > 90) throw new InvalidFieldException("Latitude", "Address", "ADDRESS-003");
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Invalid longitude value");
    }
}