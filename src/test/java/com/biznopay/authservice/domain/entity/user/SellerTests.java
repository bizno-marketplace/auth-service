package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.vo.BiDocument;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static com.biznopay.authservice.testcases.SellerTestCases.*;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
public class SellerTests {
    @ParameterizedTest(name = "{0}")
    @DisplayName("Should throw exception when seller data is invalid")
    @MethodSource("com.biznopay.authservice.testcases.SellerTestCases#registerDomainCases")
    void registerDomainCases(
            String testName,
            String firstName, String lastName, String email,
            String phone, String password, String storeName,
            String storeDescription, String nuit,
            Address address, BiDocument biDocument,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) {
        if (testName.equals("Success")){
                Seller buyer = registerSeller(
                        firstName, lastName, email, phone, password,
                        storeName, storeDescription, nuit, address, biDocument);

            Assertions.assertNotNull(buyer);
            Assertions.assertNotNull(buyer.getId());
            Assertions.assertEquals(firstName, buyer.getFirstName());
            Assertions.assertEquals(lastName, buyer.getLastName());
            Assertions.assertEquals(email, buyer.getEmail());
            Assertions.assertEquals(phone, buyer.getPhone());
            Assertions.assertEquals(password, buyer.getPassword());
            Assertions.assertEquals(UserStatus.PENDING, buyer.getStatus());
            Assertions.assertEquals(storeName, buyer.getStoreName());
            Assertions.assertEquals(storeDescription, buyer.getStoreDescription());
            Assertions.assertEquals(nuit, buyer.getNuit());
            Assertions.assertEquals(address.getLatitude(),buyer.getStoreAddress().getLatitude());
            Assertions.assertEquals(address.getLongitude(), buyer.getStoreAddress().getLongitude());
            Assertions.assertEquals(address.getNeighbourhood(), buyer.getStoreAddress().getNeighbourhood());
            Assertions.assertEquals(address.getStreet(), buyer.getStoreAddress().getStreet());
            Assertions.assertEquals(address.getCity(), buyer.getStoreAddress().getCity());
            Assertions.assertEquals(address.getProvince(), buyer.getStoreAddress().getProvince());
            Assertions.assertEquals(address.getCountry(), buyer.getStoreAddress().getCountry());
            Assertions.assertEquals(biDocument.getFrontPath(), buyer.getBiDocument().getFrontPath());
            Assertions.assertEquals(biDocument.getBackPath(), buyer.getBiDocument().getBackPath());
            Assertions.assertNull(buyer.getExpiresAt());
            Assertions.assertNotNull(buyer.getCreatedAt());
            Assertions.assertNotNull(buyer.getUpdatedAt());
        }else {
            assertThatThrownBy(() -> registerSeller(
                    firstName, lastName, email, phone, password,
                    storeName, storeDescription, nuit, address, biDocument))
                    .isInstanceOf(expectedException)
                    .hasMessage(expectedMessage);
        }
    }
}