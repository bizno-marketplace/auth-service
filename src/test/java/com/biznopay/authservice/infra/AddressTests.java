package com.biznopay.authservice.infra;

import com.biznopay.authservice.domain.exception.InvalidFieldException;
import com.biznopay.authservice.domain.vo.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class AddressTests {
    @Test
    @DisplayName("Should throw InvalidFieldException when latitude is out of bounds on build Address")
    public void shouldThrowInvalidFieldExceptionOnBuildAddress() {
        Assertions.assertThrows(InvalidFieldException.class, () ->
                new Address(-999.00, 32.5732, null, null, null, null, null));
    }
}
