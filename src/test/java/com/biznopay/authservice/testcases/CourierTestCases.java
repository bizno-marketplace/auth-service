package com.biznopay.authservice.testcases;

import com.biznopay.authservice.domain.enums.VehicleTypeEnum;
import com.biznopay.authservice.domain.exception.RequiredFieldException;
import org.junit.jupiter.params.provider.Arguments;

import java.util.stream.Stream;

public class CourierTestCases {
    public static final String VALID_FIRST_NAME = "João";
    public static final String VALID_LAST_NAME = "Tembe";
    public static final String VALID_EMAIL = "joao.tembe@gmail.com";
    public static final String VALID_PHONE = "+258841234567";
    public static final String VALID_PASSWORD = "Segura@123";
    public static final VehicleTypeEnum VALID_VEHICLE_TYPE = VehicleTypeEnum.BIKE;
    public static final String VALID_LICENSE_NUMBER = "1234567890";
    public static final String VALID_ZONE = "Zone 1";


    public static Stream<Arguments> registerDomainCases() {
        return Stream.of(
                Arguments.of("First name is null", null, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE, RequiredFieldException.class, "First name is required"),
                Arguments.of("Last name is null", VALID_FIRST_NAME, null, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE, RequiredFieldException.class, "Last name is required"),
                Arguments.of("E-mail is null", VALID_FIRST_NAME, VALID_LAST_NAME, null, VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE, RequiredFieldException.class, "E-mail is required"),
                Arguments.of("Phone is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, null, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE, RequiredFieldException.class, "Phone number is required"),
                Arguments.of("Password is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, null, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE, RequiredFieldException.class, "Password is required"),
                Arguments.of("Vehicle type is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, null, VALID_LICENSE_NUMBER, VALID_ZONE, RequiredFieldException.class, "Vehicle type is required"),
                Arguments.of("License number is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, null, VALID_ZONE, RequiredFieldException.class, "License number is required"),
                Arguments.of("Zone is null", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, null, RequiredFieldException.class, "Zone is required"),
                Arguments.of("Success", VALID_FIRST_NAME, VALID_LAST_NAME, VALID_EMAIL, VALID_PHONE, VALID_PASSWORD, VALID_VEHICLE_TYPE, VALID_LICENSE_NUMBER, VALID_ZONE, null, null)
        );
    }
}
