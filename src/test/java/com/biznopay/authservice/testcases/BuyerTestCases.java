package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.exception.InvalidEmailException;
import com.biznopay.authservice.domain.exception.InvalidPhoneNumberException;
import com.biznopay.authservice.domain.exception.InvalidStringFieldLengException;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import com.biznopay.authservice.domain.vo.Address;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class BuyerTestCases {
    public static final String VALID_FIRST_NAME = "João";
    public static final String VALID_LAST_NAME = "Tembe";
    public static final String VALID_EMAIL = "joao.tembe@gmail.com";
    public static final String VALID_PHONE = "+258841234567";
    public static final String VALID_PASSWORD = "Segura@123";
    public static final Address VALID_ADDRESS = new Address(-25.9692, 32.5732, "Av. 24 de Julho", "Sommerschield", "Maputo", "Maputo", "Mozambique");

    public static Stream<Arguments> invalidDomainRegisterCases() {
        return Stream.of(
                Arguments.of("First name is null", null, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is empty", "", VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is too short", "Jo", VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, InvalidStringFieldLengException.class, "First name must be at least 3 characters long"),
                Arguments.of("Last name is null", VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, RequiredFieldException.class, "Last name is required"),
                Arguments.of("Last name is empty", VALID_FIRST_NAME, "", VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, RequiredFieldException.class, "Last name is required"),
                Arguments.of("Last name is too short", VALID_FIRST_NAME, "Jo", VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, InvalidStringFieldLengException.class, "Last name must be at least 3 characters long"),
                Arguments.of("Email is null", VALID_FIRST_NAME, VALID_LAST_NAME, null, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Email is empty", VALID_FIRST_NAME, VALID_LAST_NAME, "", VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Email is invalid", VALID_FIRST_NAME, VALID_LAST_NAME, "invalid-email", VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, InvalidEmailException.class, "Invalid E-mail"),
                Arguments.of("Phone is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, null, VALID_PASSWORD, VALID_ADDRESS, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Phone is empty", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "", VALID_PASSWORD, VALID_ADDRESS, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Phone is invalid", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "123", VALID_PASSWORD, VALID_ADDRESS, InvalidPhoneNumberException.class, "Invalid phone number"),
                Arguments.of("Address is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, null, RequiredFieldException.class, "Delivery address is required"));
    }
}
