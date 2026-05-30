package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.entity.user.Address;
import com.biznopay.authservice.domain.entity.user.UserId;
import com.biznopay.authservice.domain.enums.UserStatus;
import com.biznopay.authservice.domain.exception.*;
import com.biznopay.authservice.presentation.dto.AddressRequest;
import com.biznopay.authservice.presentation.dto.RegisterBuyerRequest;
import org.junit.jupiter.params.provider.Arguments;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

public class BuyerTestCases {
    public static final UUID VALID_USER_ID = UUID.randomUUID();
    public static final String VALID_FIRST_NAME = "João";
    public static final String VALID_LAST_NAME = "Tembe";
    public static final String VALID_EMAIL = "joao.tembe@gmail.com";
    public static final String VALID_PHONE = "+258841234567";
    public static final String VALID_PASSWORD = "Segura@123";
    public static final UserStatus VALID_STATUS = UserStatus.PENDING;
    public static final Address VALID_ADDRESS = Address.of(-25.9692, 32.5732, "Av. 24 de Julho", "Sommerschield", "Maputo", "Maputo", "Mozambique");
    public static final List<Address> VALID_ADDRESS_LIST = List.of(VALID_ADDRESS, VALID_ADDRESS);
    public static final LocalDateTime VALID_EXPIRES_AT = LocalDateTime.now().plusDays(2);
    public static final LocalDateTime VALID_CREATED_AT = LocalDateTime.now();
    public static final LocalDateTime VALID_UPDATED_AT = LocalDateTime.now();

    public static final AddressRequest VALID_ADDRESS_REQUEST = new AddressRequest(-25.9692, 32.5732, "Av. 24 de Julho", "Sommerschield", "Maputo", "Maputo", "Mozambique");


    public static Stream<Arguments> registerDomainCases() {
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
                Arguments.of("Address is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, null, RequiredFieldException.class, "Delivery address is required"),
                Arguments.of("Address is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, null, RequiredFieldException.class, "Delivery address is required"),
                Arguments.of("Success", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_ADDRESS, null, null)
        );
    }

    public static Stream<Arguments> reconstructDomainCases() {
        return Stream.of(
                Arguments.of("User id is null", null, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, InvalidEntityIdException.class, "Invalid id for entity: com.biznopay.authservice.domain.entity.user.User"),
                Arguments.of("First name is null",VALID_USER_ID,null, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is empty", VALID_USER_ID, "", VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "First name is required"),
                Arguments.of("First name is too short", VALID_USER_ID, "Jo", VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, InvalidStringFieldLengException.class, "First name must be at least 3 characters long"),
                Arguments.of("Last name is null", VALID_USER_ID, VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "Last name is required"),
                Arguments.of("Last name is empty", VALID_USER_ID, VALID_FIRST_NAME, "", VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "Last name is required"),
                Arguments.of("Last name is too short", VALID_USER_ID, VALID_FIRST_NAME, "Jo", VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, InvalidStringFieldLengException.class, "Last name must be at least 3 characters long"),
                Arguments.of("Email is null", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, null, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Email is empty", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, "", VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Email is invalid", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, "invalid-email", VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, InvalidEmailException.class, "Invalid E-mail"),
                Arguments.of("Phone is null", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, null, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Phone is empty", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "", VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Phone is invalid", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "123", VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, InvalidPhoneNumberException.class, "Invalid phone number"),
                Arguments.of("Address is null", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, null, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, RequiredFieldException.class, "Delivery address is required"),
                Arguments.of("Success", VALID_USER_ID, VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD,VALID_STATUS, VALID_ADDRESS_LIST, VALID_EXPIRES_AT, VALID_CREATED_AT, VALID_UPDATED_AT, null, null)
        );
    }

    public static Stream<Arguments> invalidControllerRegistrationCases() {
        return Stream.of(
                Arguments.of("First name is null", new RegisterBuyerRequest(null, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "First name is required"),
                Arguments.of("First name is empty", new RegisterBuyerRequest("", VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "First name is required"),
                Arguments.of("First name is too short", new RegisterBuyerRequest("Jo", VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.UNPROCESSABLE_CONTENT, "First name must be at least 3 characters long"),
                Arguments.of("Last name is null", new RegisterBuyerRequest(VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "Last name is required"),
                Arguments.of("Last name is empty", new RegisterBuyerRequest(VALID_FIRST_NAME, "", VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "Last name is required"),
                Arguments.of("Last name is too short", new RegisterBuyerRequest(VALID_FIRST_NAME, "Jo", VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.UNPROCESSABLE_CONTENT, "Last name must be at least 3 characters long"),
                Arguments.of("Email is null", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, null, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "E-mail is required"),
                Arguments.of("Email is empty", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, "", VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "E-mail is required"),
                Arguments.of("Email is invalid", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, "invalid-email", VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.UNPROCESSABLE_CONTENT, "Invalid E-mail"),
                Arguments.of("Password is null", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, null, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "Password is required"),
                Arguments.of("Password is empty", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "", VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "Password is required"),
                Arguments.of("Password is invalid", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, "123", VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.UNPROCESSABLE_CONTENT, "Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character"),
                Arguments.of("Phone is null", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, null, VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "Phone number is required"),
                Arguments.of("Phone is empty", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, "", VALID_ADDRESS_REQUEST), HttpStatus.BAD_REQUEST, "Phone number is required"),
                Arguments.of("Phone is invalid", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, "889999999", VALID_ADDRESS_REQUEST), HttpStatus.UNPROCESSABLE_CONTENT, "Invalid phone number"),
                Arguments.of("Latitude is null", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, new AddressRequest(null, 32.5732, "", "", "", "", "")), HttpStatus.BAD_REQUEST, "Latitude is required"),
                Arguments.of("Latitude is invalid", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, new AddressRequest(180.00, 32.5732, "", "", "", "", "")), HttpStatus.UNPROCESSABLE_CONTENT, "Invalid Latitude on Address"),
                Arguments.of("Longitude is null", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, new AddressRequest(32.5732, null, "", "", "", "", "")), HttpStatus.BAD_REQUEST, "Longitude is required"),
                Arguments.of("Longitude is invalid", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, new AddressRequest(32.5732, 190.00, "", "", "", "", "")), HttpStatus.UNPROCESSABLE_CONTENT, "Invalid Longitude on Address"),
                Arguments.of("Conflict", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.CONFLICT, "E-mail already in use"),
                Arguments.of("Success", new RegisterBuyerRequest(VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PASSWORD, VALID_PHONE, VALID_ADDRESS_REQUEST), HttpStatus.OK, "We've sent an activation link to provided email: " + VALID_EMAIL)
        );
    }

}
