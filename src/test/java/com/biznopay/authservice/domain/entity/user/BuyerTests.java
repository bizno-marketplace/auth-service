package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Tag("unit")
public class BuyerTests {
    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.BuyerTestCases#registerDomainCases")
    public void registerDomainCases(String testName, String firstName, String lastName, String email,
                                    String phone, String password, Address address,
                                    Class<? extends Exception> expectedException, String expectedMessage) {
        if (testName.equals("Success")) {
            Buyer buyer = Buyer.register(firstName, lastName, email, phone, password, address);
            Assertions.assertNotNull(buyer);
            Assertions.assertNotNull(buyer.getId());
            Assertions.assertEquals(firstName, buyer.getFirstName());
            Assertions.assertEquals(lastName, buyer.getLastName());
            Assertions.assertEquals(email, buyer.getEmail());
            Assertions.assertEquals(phone, buyer.getPhone());
            Assertions.assertEquals(password, buyer.getPassword());
            Assertions.assertEquals(UserStatus.PENDING, buyer.getStatus());
            for (Address cr : buyer.getDeliveryAddresses()) {
                Assertions.assertEquals(address.getLatitude(), cr.getLatitude());
                Assertions.assertEquals(address.getLongitude(), cr.getLongitude());
                Assertions.assertEquals(address.getNeighbourhood(), cr.getNeighbourhood());
                Assertions.assertEquals(address.getStreet(), cr.getStreet());
                Assertions.assertEquals(address.getCity(), cr.getCity());
                Assertions.assertEquals(address.getProvince(), cr.getProvince());
                Assertions.assertEquals(address.getCountry(), cr.getCountry());
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

    @ParameterizedTest(name = "{0}")
    @MethodSource("com.biznopay.authservice.testcases.BuyerTestCases#reconstructDomainCases")
    public void reconstructDomainCases(String testName, UUID userId, String firstName, String lastName, String email,
                                       String phone, String password, UserStatus status, List<Address> addresses, LocalDateTime expiresAt, LocalDateTime createdAt, LocalDateTime updatedAt,
                                       Class<? extends Exception> expectedException, String expectedMessage) {
        if (testName.equals("Success")) {
            Buyer buyer = Buyer.reconstruct(userId,firstName, lastName, email, phone, password, status,addresses,expiresAt,createdAt,updatedAt);
            Assertions.assertNotNull(buyer);
            Assertions.assertNotNull(buyer.getId());
            Assertions.assertEquals(firstName, buyer.getFirstName());
            Assertions.assertEquals(lastName, buyer.getLastName());
            Assertions.assertEquals(email, buyer.getEmail());
            Assertions.assertEquals(phone, buyer.getPhone());
            Assertions.assertEquals(password, buyer.getPassword());
            Assertions.assertEquals(status, buyer.getStatus());
            for (int i = 0; i < addresses.size(); i++) {
                Assertions.assertEquals(addresses.get(i).getLatitude(), buyer.getDeliveryAddresses().get(i).getLatitude());
                Assertions.assertEquals(addresses.get(i).getLongitude(), buyer.getDeliveryAddresses().get(i).getLongitude());
                Assertions.assertEquals(addresses.get(i).getNeighbourhood(), buyer.getDeliveryAddresses().get(i).getNeighbourhood());
                Assertions.assertEquals(addresses.get(i).getStreet(), buyer.getDeliveryAddresses().get(i).getStreet());
                Assertions.assertEquals(addresses.get(i).getCity(), buyer.getDeliveryAddresses().get(i).getCity());
                Assertions.assertEquals(addresses.get(i).getProvince(), buyer.getDeliveryAddresses().get(i).getProvince());
                Assertions.assertEquals(addresses.get(i).getCountry(), buyer.getDeliveryAddresses().get(i).getCountry());
            }
            Assertions.assertNotNull(buyer.getExpiresAt());
            Assertions.assertNotNull(buyer.getCreatedAt());
            Assertions.assertNotNull(buyer.getUpdatedAt());
        } else {
            org.assertj.core.api.Assertions.assertThatThrownBy(
                            () -> Buyer.reconstruct(userId,firstName, lastName, email, phone, password, status,addresses,expiresAt,createdAt,updatedAt))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedMessage);
        }
    }
}
