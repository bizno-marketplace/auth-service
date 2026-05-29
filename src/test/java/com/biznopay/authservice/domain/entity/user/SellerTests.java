package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.BiDocument;
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
    @MethodSource("com.biznopay.authservice.testcases.SellerTestCases#invalidDomainRegisterSellerCases")
    void shouldThrowWhenSellerDataIsInvalid(
            String testName,
            String firstName, String lastName, String email,
            String phone, String password, String storeName,
            String storeDescription, String nuit,
            Address address, BiDocument biDocument,
            Class<? extends Exception> expectedException,
            String expectedMessage
    ) {
        assertThatThrownBy(() -> registerSeller(
                firstName, lastName, email, phone, password,
                storeName, storeDescription, nuit, address, biDocument))
                .isInstanceOf(expectedException)
                .hasMessage(expectedMessage);
    }

    @Test
    @DisplayName("Should register seller successfully with valid data")
    void shouldRegisterSellerSuccessfully() {
        assertThatNoException().isThrownBy(() -> registerSeller(
                VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,
                VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI));
    }
}