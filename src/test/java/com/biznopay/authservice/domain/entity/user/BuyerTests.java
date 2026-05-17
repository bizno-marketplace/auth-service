package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.InvalidPhoneNumberException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static com.biznopay.authservice.mocks.Mocks.addressMock;

public class BuyerTests {
    @Test
    @DisplayName("Should throw InvalidPhoneNumberException when phone number is invalid or not Mozambican phone number on register")
    public void shouldThrowInvalidPhoneNumberExceptionWhenPhoneNumberIsInvalidOrNotMozambicanPhoneNumberOnRegister() {
        Assertions.assertThrows(InvalidPhoneNumberException.class, () -> Buyer.register("any_first_name",
                "any_last_name", "admin@bizno.co.mz", "8884848484", "Password@123", addressMock()));
    }

    @Test
    @DisplayName("Should throw  RequiredFieldException when delivery address is null on register")
    public void shouldReturnRequiredFieldExceptionWhenDeliveryAddressIsNull() {
        Assertions.assertThrows(RequiredFieldException.class, () -> Buyer.register("any_first_name",
                "any_last_name", "admin@bizno.co.mz", "848484848", "Password@123", null));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw RequiredFieldException when phone number is empty or null on register")
    public void shouldThrowRequiredFieldExceptionWhenPhoneNumberIsEmptyOrNullOnRegister(String phone){
        Assertions.assertThrows(RequiredFieldException.class, () -> Buyer.register("any_first_name",
                "any_last_name", "admin@bizno.co.mz", phone, "Password@123", addressMock()));
    }

    @Test
    @DisplayName("Should return Buyer with correct values on register")
    public void shouldReturnBuyerWithCorrectValuesOnRegister() {
        Address address = new Address(-25.9692, 32.5732, "any_street", "any_neighbourhood", "any_city", "any_province", "any_country");
        Buyer buyer = Buyer.register("any_first_name", "any_last_name", "anybizno@bizno.co.mz", "848484848", "Password@123", address);
        Assertions.assertNotNull(buyer);
        Assertions.assertNotNull(buyer.getId());
        Assertions.assertEquals("any_first_name", buyer.getFirstName());
        Assertions.assertEquals("any_last_name", buyer.getLastName());
        Assertions.assertEquals("anybizno@bizno.co.mz", buyer.getEmail());
        Assertions.assertEquals("848484848", buyer.getPhone());
        Assertions.assertEquals("Password@123", buyer.getPassword());
        Assertions.assertEquals(UserStatus.PENDING, buyer.getStatus());
        Assertions.assertEquals(-25.9692, buyer.getDeliveryAddress().latitude());
        Assertions.assertEquals(32.5732, buyer.getDeliveryAddress().longitude());
        Assertions.assertEquals("any_neighbourhood", buyer.getDeliveryAddress().neighbourhood());
        Assertions.assertEquals("any_street", buyer.getDeliveryAddress().street());
        Assertions.assertEquals("any_city", buyer.getDeliveryAddress().city());
        Assertions.assertEquals("any_province", buyer.getDeliveryAddress().province());
        Assertions.assertEquals("any_country", buyer.getDeliveryAddress().country());
        Assertions.assertNotNull(buyer.getExpiresAt());
        Assertions.assertNotNull(buyer.getCreatedAt());
        Assertions.assertNotNull(buyer.getUpdatedAt());
    }
}
