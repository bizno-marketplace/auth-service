package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.biznopay.authservice.testcases.BuyerTestCases.*;

@Tag("unit")
public class BuyerTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.BuyerTestCases#registerDomainCases")
    public void registerDomainCases(String testName, String firstName, String lastName, String email,
                                    String phone, String password, Address address,
                                    Class<? extends Exception> expectedException, String expectedMessage) {
        if (testName.equals("Success")) {
            Buyer buyer = Buyer.register(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS);
            Assertions.assertNotNull(buyer);
            Assertions.assertNotNull(buyer.getId());
            Assertions.assertEquals(VALID_FIRST_NAME, buyer.getFirstName());
            Assertions.assertEquals(VALID_LAST_NAME, buyer.getLastName());
            Assertions.assertEquals(VALID_EMAIL, buyer.getEmail());
            Assertions.assertEquals(VALID_PHONE, buyer.getPhone());
            Assertions.assertEquals(VALID_PASSWORD, buyer.getPassword());
            Assertions.assertEquals(UserStatus.PENDING, buyer.getStatus());
            for (Address cr : buyer.getDeliveryAddresses()) {
                Assertions.assertEquals(VALID_ADDRESS.getLatitude(), cr.getLatitude());
                Assertions.assertEquals(VALID_ADDRESS.getLongitude(), cr.getLongitude());
                Assertions.assertEquals(VALID_ADDRESS.getNeighbourhood(), cr.getNeighbourhood());
                Assertions.assertEquals(VALID_ADDRESS.getStreet(), cr.getStreet());
                Assertions.assertEquals(VALID_ADDRESS.getCity(), cr.getCity());
                Assertions.assertEquals(VALID_ADDRESS.getProvince(), cr.getProvince());
                Assertions.assertEquals(VALID_ADDRESS.getCountry(), cr.getCountry());
            }
            Assertions.assertNotNull(buyer.getExpiresAt());
            Assertions.assertNotNull(buyer.getCreatedAt());
            Assertions.assertNotNull(buyer.getUpdatedAt());
        } else {
            org.assertj.core.api.Assertions.assertThatThrownBy(
                            () -> Buyer.register(firstName, lastName, email, phone, password, address))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedMessage);
        }
    }
}
