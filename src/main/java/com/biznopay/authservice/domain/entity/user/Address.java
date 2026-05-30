package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.exception.InvalidEntityIdException;
import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;

public class Address {
    private final Long id;
    private final Double latitude;
    private final Double longitude;
    private final String street;
    private final String neighbourhood;
    private final String city;
    private final String province;
    private final String country;

    private Address(Long id, Double latitude, Double longitude, String street, String neighbourhood, String city, String province, String country) {
        this.id = id;
        this.latitude = validateLatitude(latitude);
        this.longitude = validateLongitude(longitude);
        this.street = street;
        this.neighbourhood = neighbourhood;
        this.city = city;
        this.province = province;
        this.country = country;
    }

    public static Address of(Double latitude, Double longitude, String street, String neighbourhood, String city, String province, String country) {
        return new Address(null, latitude, longitude, street, neighbourhood, city, province, country);
    }

    public static Address reconstruct(Long id, Double latitude, Double longitude, String street, String neighbourhood, String city, String province, String country) {
        id = validateId(id);
        return new Address(id, latitude, longitude, street, neighbourhood, city, province, country);
    }

    private static Long validateId(Long id) {
        if (id == null)
            throw new RequiredFieldException("Id", "Address", "ADDRESS-005");
        if (id <= 0)
            throw new InvalidEntityIdException(Address.class.getName(), "ADDRESS-006");
        return id;
    }

    //Validations
    private Double validateLatitude(Double latitude) {
        if (latitude == null)
            throw new RequiredFieldException("Latitude", "Address", "ADDRESS-001");
        if (latitude < -90 || latitude > 90)
            throw new InvalidFieldException("Latitude", "Address", "ADDRESS-002");

        return latitude;
    }

    private Double validateLongitude(Double longitude) {
        if (longitude == null)
            throw new RequiredFieldException("Longitude", "Address", "ADDRESS-003");
        if (longitude < -180 || longitude > 180)
            throw new InvalidFieldException("Longitude", "Address", "ADDRESS-004");
        return longitude;
    }

    public Long getId() {
        return id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getStreet() {
        return street;
    }

    public String getNeighbourhood() {
        return neighbourhood;
    }

    public String getCity() {
        return city;
    }

    public String getProvince() {
        return province;
    }

    public String getCountry() {
        return country;
    }
}
