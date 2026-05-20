package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.Address;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.biznopay.authservice.testcases.BuyerTestCases.*;

@Tag("unit")
public class BuyerTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.BuyerTestCases#invalidDomainRegisterCases")
    public void shouldThrowExceptionWhenRegisterWithInvalidData(String message, String firstName, String lastName, String email,
                                                                String phone, String password, Address address,
                                                                Class<? extends Exception> expectedException, String expectedMessage) {
        org.assertj.core.api.Assertions.assertThatThrownBy(
                () -> Buyer.register(firstName, lastName, email, phone, password, address))
                .isInstanceOf(expectedException)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Should return Buyer with correct values on register")
    public void shouldReturnBuyerWithCorrectValuesOnRegister() {
        Buyer buyer = Buyer.register(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS);
        Assertions.assertNotNull(buyer);
        Assertions.assertNotNull(buyer.getId());
        Assertions.assertEquals(VALID_FIRST_NAME, buyer.getFirstName());
        Assertions.assertEquals(VALID_LAST_NAME, buyer.getLastName());
        Assertions.assertEquals(VALID_EMAIL, buyer.getEmail());
        Assertions.assertEquals(VALID_PHONE, buyer.getPhone());
        Assertions.assertEquals(VALID_PASSWORD, buyer.getPassword());
        Assertions.assertEquals(UserStatus.PENDING, buyer.getStatus());
        Assertions.assertEquals(VALID_ADDRESS.latitude(), buyer.getDeliveryAddress().latitude());
        Assertions.assertEquals(VALID_ADDRESS.longitude(), buyer.getDeliveryAddress().longitude());
        Assertions.assertEquals(VALID_ADDRESS.neighbourhood(), buyer.getDeliveryAddress().neighbourhood());
        Assertions.assertEquals(VALID_ADDRESS.street(), buyer.getDeliveryAddress().street());
        Assertions.assertEquals(VALID_ADDRESS.city(), buyer.getDeliveryAddress().city());
        Assertions.assertEquals(VALID_ADDRESS.province(), buyer.getDeliveryAddress().province());
        Assertions.assertEquals(VALID_ADDRESS.country(), buyer.getDeliveryAddress().country());
        Assertions.assertNotNull(buyer.getExpiresAt());
        Assertions.assertNotNull(buyer.getCreatedAt());
        Assertions.assertNotNull(buyer.getUpdatedAt());
    }
}
