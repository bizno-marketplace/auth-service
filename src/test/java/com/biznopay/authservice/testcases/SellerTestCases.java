package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.entity.user.Seller;
import com.biznopay.authservice.domain.exception.*;
import com.biznopay.authservice.domain.vo.Address;
import com.biznopay.authservice.domain.vo.BiDocument;
import com.biznopay.authservice.domain.vo.BiDocumentRequest;
import com.biznopay.authservice.usecase.user.register.seller.RegisterSellerInput;
import org.junit.jupiter.params.provider.Arguments;

import java.util.Optional;
import java.util.stream.Stream;

public class SellerTestCases {
    public static final String VALID_FIRST_NAME = "João";
    public static final String VALID_LAST_NAME = "Tembe";
    public static final String VALID_EMAIL = "joao.tembe@gmail.com";
    public static final String VALID_PHONE = "+258841234567";
    public static final String VALID_PASSWORD = "Segura@123";
    public static final String VALID_STORE_NAME = "Tembe Electronics";
    public static final String VALID_STORE_DESC = "Venda de electrónica e acessórios";
    public static final String VALID_NUIT = "400123456";
    public static final Address VALID_ADDRESS = new Address(-25.9692, 32.5732, "Av. 24 de Julho", "Sommerschield", "Maputo", "Maputo", "Mozambique");
    public static final BiDocument VALID_BI = BiDocument.of("sellers/400123456/bi/front-uuid.jpg", "sellers/400123456/bi/back-uuid.jpg");
    public static final BiDocumentRequest VALID_BI_REQUEST = new BiDocumentRequest(VALID_BI.getFrontPath().getBytes(), "sellers/400123456/bi/front-uuid.jpg", VALID_BI.getBackPath().getBytes(), "sellers/400123456/bi/back-uuid.jpg");

    public static Seller registerSeller(
            String firstName, String lastName, String email,
            String phone, String password, String storeName,
            String storeDescription, String nuit,
            Address address, BiDocument biDocument
    ) {
        return Seller.register(firstName, lastName, email, phone, password,
                storeName, storeDescription, nuit, address, biDocument);
    }

    public static RegisterSellerInput registerSellerInput(
            String firstName, String lastName, String email,
            String phone, String password, String storeName,
            String storeDescription, String nuit,
            Address address, BiDocumentRequest biDocumentRequest
    ) {
        return new RegisterSellerInput(firstName, lastName, email, phone, password,
                storeName, storeDescription, nuit, address, biDocumentRequest);
    }


    public static Stream<Arguments> invalidDomainRegisterSellerCases() {

        return Stream.of(
                Arguments.of("First name is null", null, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is empty", "", VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name too short", "Jo", VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, InvalidStringFieldLengException.class, "First name must be at least 3 characters long"),
                Arguments.of("Last name is null", VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "Last name is required"),
                Arguments.of("Last name is empty", VALID_FIRST_NAME, "", VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "Last name is required"),
                Arguments.of("Last name too short", VALID_FIRST_NAME, "Te", VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, InvalidStringFieldLengException.class, "Last name must be at least 3 characters long"),
                Arguments.of("Email is null", VALID_FIRST_NAME, VALID_LAST_NAME, null, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Email is empty", VALID_FIRST_NAME, VALID_LAST_NAME, "", VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Email invalid format", VALID_FIRST_NAME, VALID_LAST_NAME, "not-an-email", VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, InvalidEmailException.class, "Invalid E-mail"),
                Arguments.of("Phone is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, null, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Phone is empty", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "", VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Phone invalid", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "+351912345678", VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, InvalidPhoneNumberException.class, "Invalid phone number"),
                Arguments.of("Store name is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, null, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "Store name is required"),
                Arguments.of("Store name is empty", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, "", VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "Store name is required"),
                Arguments.of("Store desc is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, null, VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "Store description is required"),
                Arguments.of("Store desc is empty", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, "", VALID_NUIT, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "Store description is required"),
                Arguments.of("NUIT is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, null, VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "NUIT is required"),
                Arguments.of("NUIT is empty", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, "", VALID_ADDRESS, VALID_BI, RequiredFieldException.class, "NUIT is required"),
                Arguments.of("NUIT non-numeric", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, "ABCDEFGHI", VALID_ADDRESS, VALID_BI, InvalidNuitException.class, "NUIT must contain only digits and must be exactly 9 digits"),
                Arguments.of("NUIT wrong length", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME, VALID_STORE_DESC, "12345", VALID_ADDRESS, VALID_BI, InvalidNuitException.class, "NUIT must contain only digits and must be exactly 9 digits")
        );
    }

    public static Stream<Arguments> invalidUseCaseRegisterSellerCases() {
        return Stream.of(
                Arguments.of("Email already in use",
                        registerSellerInput(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,
                                VALID_STORE_NAME, VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI_REQUEST),
                        Optional.of(salerMock()),
                        EmailAlreadyInUseException.class,
                        "Email already in use")
        );
    }

    public static Seller salerMock() {
        return registerSeller(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_STORE_NAME,
                VALID_STORE_DESC, VALID_NUIT, VALID_ADDRESS, VALID_BI);
    }
}
