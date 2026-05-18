package com.biznopay.authservice.infra;

import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AddressTests {
    @Test
    @DisplayName("Should throw RequiredFieldException when latitude is null on build")
    public void shouldThrowRequiredFieldExceptionOnBuildAddress() {
        Assertions.assertThrows(RequiredFieldException.class, () ->
                new Address(null, 32.5732, null, null, null, null, null));
    }

    @Test
    @DisplayName("Should throw RequiredFieldException when longitude is null on build")
    public void shouldThrowRequiredFieldExceptionWhenLongitudeIsNullOnBuild() {
        Assertions.assertThrows(RequiredFieldException.class, () ->
                new Address(32.5732, null, null, null, null, null, null));
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when latitude is out of bounds on build Address")
    public void shouldThrowInvalidFieldWhenLatitudeIsOutOfBoundsExceptionOnBuildAddress() {
        Assertions.assertThrows(InvalidFieldException.class, () ->
                new Address(-999.00, 32.5732, null, null, null, null, null));
    }

    @Test
    @DisplayName("Should throw InvalidFieldException when longitude is out of bounds on build address")
    public void shouldThrowInvalidFieldWhenLongitudeIsOutOfBoundsExceptionOnBuildAddress() {
        Assertions.assertThrows(InvalidFieldException.class, () ->
                new Address(32.5732, -999.00, null, null, null, null, null));
    }

    @Test
    @DisplayName("Should build Address with correct values on build address")
    public void shouldBuildAddressWithCorrectValuesOnBuildAddress() {
        Address address = new Address(32.5732, 32.5732, "any_street", "any_neighbourhood", "any_city", "any_province", "any_country");
        Assertions.assertEquals(32.5732, address.latitude());
        Assertions.assertEquals(32.5732, address.longitude());
        Assertions.assertEquals("any_street", address.street());
        Assertions.assertEquals("any_neighbourhood", address.neighbourhood());
        Assertions.assertEquals("any_city", address.city());
        Assertions.assertEquals("any_province", address.province());
        Assertions.assertEquals("any_country", address.country());
    }
}
