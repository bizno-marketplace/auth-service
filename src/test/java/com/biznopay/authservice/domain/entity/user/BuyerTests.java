package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.mocks.Mocks;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BuyerTests {
    @Test
    @DisplayName("Should return RequiredFieldException when delivery address is null on register")
    public void shouldReturnRequiredFieldExceptionWhenDeliveryAddressIsNull() {
        Assertions.assertThrows(RequiredFieldException.class, () -> Buyer.register("any_first_name",
                "any_last_name", "admin@bizno.co.mz", "Password@123",null));
    }
}
