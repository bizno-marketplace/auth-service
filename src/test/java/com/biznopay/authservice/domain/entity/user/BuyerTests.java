package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.mocks.Mocks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class BuyerTests {
    @Test
    @DisplayName("Should return RequiredFieldException when delivery address is null on register")
    public void shouldReturnRequiredFieldExceptionWhenDeliveryAddressIsNull() {
        Assertions.assertThrows(RequiredFieldException.class, () -> Buyer.register("any_first_name",
                "any_last_name", "admin@bizno.co.mz", "8484848484","Password@123", null));
    }

    @Test
    @DisplayName("Should return Buyer with correct values")
    public void shouldReturnBuyerWithCorrectValues(){
        Address address =  new Address(-25.9692,32.5732,"any_street","any_neighbourhood","any_city","any_province","any_country");
        Buyer buyer = Buyer.register("any_first_name", "any_last_name", "anybizno@bizno.co.mz", "848484848","Password@123",address);
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
