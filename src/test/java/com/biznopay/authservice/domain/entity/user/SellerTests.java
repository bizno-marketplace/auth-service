package com.biznopay.authservice.domain.entity.user;

import com.biznopay.authservice.domain.exception.*;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.BiDocument;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Tag("unit")
public class SellerTests {

    private static final UserId VALID_ID = UserId.generate();
    private static final String VALID_FIRST_NAME = "João";
    private static final String VALID_LAST_NAME = "Tembe";
    private static final String VALID_EMAIL = "joao.tembe@gmail.com";
    private static final String VALID_PHONE = "+258841234567";
    private static final String VALID_PASSWORD = "Segura@123";
    private static final String VALID_STORE_NAME = "Tembe Electronics";
    private static final String VALID_STORE_DESC = "Venda de electrónica e acessórios";
    private static final String VALID_NUIT = "400123456";
    private static final Address VALID_ADDRESS = new Address(-25.9692, 32.5732, "Av. 24 de Julho", "Sommerschield", "Maputo", "Maputo", "Mozambique");
    private static final BiDocument VALID_BI = BiDocument.of("sellers/400123456/bi/front-uuid.jpg", "sellers/400123456/bi/back-uuid.jpg");

    static Stream<Arguments> invalidSellerCases() {
        Address validAddress = new Address(-25.9692, 32.5732, "Av. 24 de Julho", "Sommerschield", "Maputo", "Maputo", "Mozambique");
        BiDocument validBi = BiDocument.of("sellers/400123456/bi/front-uuid.jpg", "sellers/400123456/bi/back-uuid.jpg");

        return Stream.of(
                Arguments.of("First name is null", null, "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is empty", "", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name too short", "Jo", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, InvalidStringFieldLengException.class, "First name must be at least 3 characters long"),
                Arguments.of("Last name is null", "João", null, "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "Last name is required"),
                Arguments.of("Last name is empty", "João", "", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "Last name is required"),
                Arguments.of("Last name too short", "João", "Te", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, InvalidStringFieldLengException.class, "Last name must be at least 3 characters long"),
                Arguments.of("Email is null", "João", "Tembe", null, "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Email is empty", "João", "Tembe", "", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Email invalid format", "João", "Tembe", "not-an-email", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, InvalidEmailException.class, "Invalid E-mail"),
                Arguments.of("Phone is null", "João", "Tembe", "joao.tembe@gmail.com", null, "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Phone is empty", "João", "Tembe", "joao.tembe@gmail.com", "", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Phone invalid", "João", "Tembe", "joao.tembe@gmail.com", "+351912345678", "Segura@123", "Tembe Electronics", "Venda de electrónica", "400123456", validAddress, validBi, InvalidPhoneNumberException.class, "Invalid phone number"),
                Arguments.of("Store name is null", "João", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", null, "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "Store name is required"),
                Arguments.of("Store name is empty", "João", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "", "Venda de electrónica", "400123456", validAddress, validBi, RequiredFieldException.class, "Store name is required"),
                Arguments.of("Store desc is null", "João", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", null, "400123456", validAddress, validBi, RequiredFieldException.class, "Store description is required"),
                Arguments.of("Store desc is empty", "João", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "", "400123456", validAddress, validBi, RequiredFieldException.class, "Store description is required"),
                Arguments.of("NUIT is null", "João", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", null, validAddress, validBi, RequiredFieldException.class, "NUIT is required"),
                Arguments.of("NUIT is empty", "João", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "", validAddress, validBi, RequiredFieldException.class, "NUIT is required"),
                Arguments.of("NUIT non-numeric", "João", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "ABCDEFGHI", validAddress, validBi, InvalidNuitException.class, "NUIT must contain only digits and must be exactly 9 digits"),
                Arguments.of("NUIT wrong length", "João", "Tembe", "joao.tembe@gmail.com", "+258841234567", "Segura@123", "Tembe Electronics", "Venda de electrónica", "12345", validAddress, validBi, InvalidNuitException.class, "NUIT must contain only digits and must be exactly 9 digits")
        );
    }

    private Seller registerSeller(
            String firstName, String lastName, String email,
            String phone, String password, String storeName,
            String storeDescription, String nuit,
            Address address, BiDocument biDocument
    ) {
        return Seller.register(VALID_ID, firstName, lastName, email, phone, password,
                storeName, storeDescription, nuit, address, biDocument);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("invalidSellerCases")
    @DisplayName("Should throw exception when seller data is invalid")
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