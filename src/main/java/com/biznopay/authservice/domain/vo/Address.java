package com.biznopay.authservice.domain.vo;

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
        if (latitude == null) throw new IllegalArgumentException("Latitude is required");
        if (longitude == null) throw new IllegalArgumentException("Longitude is required");
        if (latitude < -90 || latitude > 90) throw new IllegalArgumentException("Invalid latitude value");
        if (longitude < -180 || longitude > 180) throw new IllegalArgumentException("Invalid longitude value");
    }
}