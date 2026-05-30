package com.biznopay.authservice.infra;

import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AddressTests {
    @Test
    @DisplayName("Should throw RequiredFieldException when latitude is null on build")
    public void shouldThrowRequiredFieldExceptionOnBuildAddress() {
        Assertions.assertThrows(RequiredFieldException.class, () ->
                Address.of(null, 32.5732, null, null, null, null, null));
    }

    @Test
    @DisplayName("Should throw RequiredFieldException when longitude is null on build")
    public void shouldThrowRequiredFieldExceptionWhenLongitudeIsNullOnBuild() {
        Assertions.assertThrows(RequiredFieldException.class, () ->
                Address.of(32.5732, null, null, null, null, null, null));
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when latitude is out of bounds on build Address")
    public void shouldThrowInvalidFieldWhenLatitudeIsOutOfBoundsExceptionOnBuildAddress() {
        Assertions.assertThrows(InvalidFieldException.class, () ->
                Address.of(-999.00, 32.5732, null, null, null, null, null));
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when longitude is out of bounds on build address")
    public void shouldThrowInvalidFieldWhenLongitudeIsOutOfBoundsExceptionOnBuildAddress() {
        Assertions.assertThrows(InvalidFieldException.class, () ->
                Address.of(32.5732, -999.00, null, null, null, null, null));
    }

    @Test
    @DisplayName("Should build Address with correct values on build address")
    public void shouldBuildAddressWithCorrectValuesOnBuildAddress() {
        Address address = Address.of(32.5732, 32.5732, "any_street", "any_neighbourhood", "any_city", "any_province", "any_country");
        Assertions.assertEquals(32.5732, address.getLatitude());
        Assertions.assertEquals(32.5732, address.getLongitude());
        Assertions.assertEquals("any_street", address.getStreet());
        Assertions.assertEquals("any_neighbourhood", address.getNeighbourhood());
        Assertions.assertEquals("any_city", address.getCity());
        Assertions.assertEquals("any_province", address.getProvince());
        Assertions.assertEquals("any_country", address.getCountry());
    }
}
